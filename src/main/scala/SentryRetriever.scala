import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpEntity, HttpMethods}
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.config.{Config, ConfigFactory}
import io.circe.DecodingFailure
import orm.{Event, Issue}
import util.HttpRequestTrait

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps

object SentryRetriever extends HttpRequestTrait:

  lazy val config: Config = ConfigFactory.load()
  lazy val token: String = config.getString("sentry.auth_token")
  lazy val organization: String = config.getString("sentry.organization")
  lazy val groupID: String = config.getString("sentry.groupID")

  def getIssues: Future[Future[List[Issue]]] =
    given system: ActorSystem = ActorSystem("slack")
    given executionContext: ExecutionContext = system.dispatcher

    import io.circe.*, io.circe.parser.*

    val events = for { events <- getData(
      url = s"https://sentry.io/api/0/projects/$organization/$groupID/issues/",
      headers = List(
        RawHeader("Authorization",s"Bearer $token")),
      entity = HttpEntity.Empty,
      method = HttpMethods.GET
    )} yield events

    events.map {
      target =>
        for {
          t <- Unmarshal(target.entity).to[String]
        } yield {
          println(t)
          val doc: Json = parse(t).getOrElse(Json.Null)
          doc.as[List[JsonObject]] match {
            case Right(list) =>
              list.map {
                param =>
                  Issue.getInstance(param)
              }
            case Left(f)=>
              throw f
          }
        }
    }.recover {
      case e: Exception => throw e
    }

  def getEvents: Future[Future[List[Event]]] =
    given system: ActorSystem = ActorSystem("slack")
    given executionContext: ExecutionContext = system.dispatcher

    import io.circe.*, io.circe.parser.*

    val events = for { events <- getData(
      url = s"https://sentry.io/api/0/projects/$organization/$groupID/events/",
      headers = List(
        RawHeader("Authorization",s"Bearer $token")),
      entity = HttpEntity.Empty,
      method = HttpMethods.GET
    )} yield events

    events.map {
      target =>
        for {
          t <- Unmarshal(target.entity).to[String]
        } yield {
          println(t)
          val doc: Json = parse(t).getOrElse(Json.Null)
          doc.as[List[JsonObject]] match {
            case Right(list) =>
              list.map {
                param =>
                  Event.getInstance(param)
              }
            case Left(f) =>
              throw f
          }
        }
    }.recover {
      case e: Exception => throw e
    }
