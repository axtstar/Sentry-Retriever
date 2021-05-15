package orm

import io.circe.JsonObject

case class Issue(
                  id: String,
                  title:String,
                  permalink: String,
                  firstSeen: String,
                  lastSeen: String,
                  shortId: String,
                  level: String
                ) {
  override def toString: String = {
    s"id: $id title: $title permalink: $permalink firstSeen: $firstSeen lastSeen: $lastSeen shortId: $shortId level: $level"
  }
}

object Issue {
  import scalikejdbc._

  def getInstance(param: JsonObject): Issue ={
    Issue(
      id=param("id").get.as[String].getOrElse(""),
      title = param("title").get.as[String].getOrElse(""),
      permalink = param("permalink").get.as[String].getOrElse(""),
      firstSeen = param("firstSeen").get.as[String].getOrElse(""),
      lastSeen = param("lastSeen").get.as[String].getOrElse(""),
      shortId = param("shortId").get.as[String].getOrElse(""),
      level = param("level").get.as[String].getOrElse(""),
    )
  }

  def create(using session:DBSession): Boolean = {
    import scala.language.implicitConversions

    sql"""
         | create table IF NOT EXISTS sentry_issues (
         | id text primary key,
         | title text,
         | permalink text,
         | firstSeen text,
         | lastSeen text,
         | shortId text,
         | level text,
         | isSlack bool
         | )
         """.stripMargin
      .execute
      .apply()
  }

  def insertOne(e: Issue)(using session:DBSession): Int = {
    import scala.language.implicitConversions

    sql"""
         | insert or replace into sentry_issues
         | (id,
         | title,
         | permalink,
         | firstSeen,
         | lastSeen,
         | shortId,
         | level)
         | values
         | (${e.id},
         | ${e.title},
         | ${e.permalink},
         | ${e.firstSeen},
         | ${e.lastSeen},
         | ${e.shortId},
         | ${e.level})
         |""".stripMargin
      .update
      .apply()
  }

}

