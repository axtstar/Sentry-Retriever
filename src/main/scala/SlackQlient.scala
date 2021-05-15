import java.time.Instant
import java.time.format.DateTimeFormatter

import util.SlackTrait

import scala.concurrent.Future

object SlackQlient extends SlackTrait {

  // TODO not from config but from API
  lazy val ruleUrl: String = config.getString("sentry.ruleUrl")
  lazy val ruleName: String = config.getString("sentry.ruleName")


  def send(title: String, message: String,link: Option[String], colorLevel: String, shortId: String, dataCreated: String): Future[String] = {
    val string_to_date = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(dataCreated)
    val instant = Instant.from(string_to_date)

    val ts = instant.toEpochMilli

    sendMessage("",
      fallback = s"$message ${link.getOrElse("")}",
      pretext = "",
      title = title ,
      title_link = link.getOrElse(""),
      text = message,
      color = colorLevel, // #7CD197",
      footer = s"$shortId via <$ruleUrl|$ruleName>",
      ts = ts
    )
  }
}
