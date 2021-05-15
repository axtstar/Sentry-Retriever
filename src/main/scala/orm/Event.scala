package orm

import io.circe.{Json, JsonObject}

case class Event(
                  id: String,
                  projectID: String,
                  dateCreated: String,
                  title:String,
                  culprit: String,
                  message: String,
                  groupID: String,
                  tags: String,
                  isSlack: Option[Boolean]
                ) {
  override def toString: String = {
    s"id: $id title: $title message: $message"
  }
}

object Event {

  import scalikejdbc._

  def getInstance(param: JsonObject): Event = {
    Event(
      id=param("id").get.as[String].getOrElse(""),
      projectID=param("projectID").get.as[String].getOrElse(""),
      dateCreated=param("dateCreated").get.as[String].getOrElse(""),
      title=param("title").get.as[String].getOrElse(""),
      culprit=param("culprit").get.as[String].getOrElse(""),
      message=param("message").get.as[String].getOrElse(""),
      groupID=param("groupID").get.as[String].getOrElse(""),
      tags=param("tags").get.toString(),
      isSlack=None
    )
  }

  def getAllByIsNotSlack(using session:DBSession):List[Event] = {
    import scala.language.implicitConversions

    sql"""
         |select * from sentry_events
         |where isSlack is null or isSlack = false
         """.stripMargin
      .map{
        param =>
          Event(
            id=param.get[String]("id"),
            projectID=param.get[String]("projectID"),
            dateCreated=param.get[String]("dateCreated"),
            title=param.get[String]("title"),
            culprit=param.get[String]("culprit"),
            message=param.get[String]("message"),
            groupID=param.get[String]("groupID"),
            tags=param.get[String]("tags"),
            isSlack=param.get[Option[Boolean]]("isSlack")
          )
      }
      .list
      .apply()

  }

  def create(using session:DBSession): Boolean ={
    import scala.language.implicitConversions

    sql"""
          | create table IF NOT EXISTS sentry_events (
          | id text primary key,
          | projectID text,
          | dateCreated text,
          | title text,
          | culprit text,
          | message text,
          | groupID text,
          | tags json,
          | isSlack bool
          | )
         """.stripMargin
      .execute
      .apply()
  }

  def insertOne(e: Event)(using session:DBSession): Int ={
    import scala.language.implicitConversions

    sql"""
         | insert or ignore into sentry_events
         | (id, projectID, dateCreated, title, culprit, message, groupID, tags)
         | values
         | (${e.id}, ${e.projectID}, ${e.dateCreated}, ${e.title}, ${e.culprit}, ${e.message}, ${e.groupID}, ${e.tags})
         |""".stripMargin
      .update
      .apply()
  }

  def updateIsSlack(es:List[Event])(using session:DBSession): Int ={
    import scala.language.implicitConversions

    sql"""
         | update sentry_events
         | set isSlack = true
         | where groupID in (${es.map(_.groupID)})
         |""".stripMargin
      .update
      .apply()

  }

}


