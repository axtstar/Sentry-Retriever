import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.{Logger, LoggerFactory}
import orm.{Event, Issue}

import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps
import scalikejdbc._
import scalikejdbc.config._

object Main:

  lazy val config: Config = ConfigFactory.load()
  lazy val mode: String = config.getString("mode")

  lazy val logger: Logger = LoggerFactory.getLogger("Main")

  def main(args: Array[String]): Unit =
    logger.info(s"mode: $mode")
    given system:ActorSystem = ActorSystem("slack")
    given executionContext:ExecutionContext = system.dispatcher

    DBs.setupAll()

    DB localTx { implicit session: DBSession =>
      Event.create
      Issue.create
    }

    val result = (for {
      resultIssues <- SentryRetriever.getIssues
      resultEvents <- SentryRetriever.getEvents
    } yield {

      lazy val is: List[Issue] = Await.result(resultIssues, 60 second)
      lazy val es: List[Event] = Await.result(resultEvents, 60 second)

      DB localTx { implicit session: DBSession =>
        es.map { e =>
          Event.insertOne(e)
        }
        is.map { i =>
          Issue.insertOne(i)
        }
      }

      // retrieve not slacked
      val list = DB localTx { implicit session: DBSession =>
        Event.getAllByIsNotSlack
      }

      val target = list.groupBy(_.groupID)

      DB localTx { implicit session: DBSession =>
        target.foreach {
          e =>
            val i = is.find(_.id == e._2(0).groupID)
            //slack
            i match {
              case Some(ii) =>
                val slaQ = for {
                  slaQ <- SlackQlient.send(
                    title = e._2(0).title,
                    message = e._2(0).culprit,
                    link = Some(i.get.permalink),
                    colorLevel = if(ii.level=="error") "#E03E2F" else "#2788CE",
                    shortId = i.get.shortId,
                    dataCreated = e._2(0).dateCreated
                  )
                } yield slaQ
                val slackResult = Await.result(slaQ, 60 second)
                logger.info(s"slackResult: $slackResult")
              case _ =>
                logger.info(s"slackResult: Nothing")
            }
        }
        Event.updateIsSlack(list)
      }
    }).recover {
      case e: Exception => throw e
    }

    Await.result(result, Duration.Inf)
    System.exit(0)


