package util

import akka.actor.ActorSystem
import slack.api.SlackApiClient
import slack.models.{ActionField, Attachment}

import scala.concurrent.{ExecutionContextExecutor, Future}

trait SlackTrait extends ConfigTrait {

  lazy val token: String = config.getString("slack.token")
  lazy val channel: String = config.getString("slack.channel")

  lazy val botName: String = config.getString("slack.botName")
  lazy val iconUrl: String = config.getString("slack.iconUrl")
  lazy val footerIcon: String = config.getString("slack.footerIcon")


  lazy val client: SlackApiClient = SlackApiClient(token)

  def sendMessage(message: String,
                  iconUrl: String=iconUrl,
                  fallback: String,
                  pretext: String,
                  title: String,
                  title_link: String,
                  text: String,
                  color: String,
                  footer: String,
                  footer_icon: String=footerIcon,
                  ts: Long
                 ): Future[String] = {
    given system: ActorSystem = ActorSystem("slack")
    given ec: ExecutionContextExecutor = system.dispatcher

    client.postChatMessage(
      channelId = channel,
      username = Some(botName),
      text = message,
      iconUrl = Some(iconUrl),
      attachments = Some(Seq(
        Attachment(fallback = Some(fallback),
          pretext = Some(pretext),
          title = Some(title),
          title_link = Some(title_link),
          text = Some(text),
          color = Some(color),
          footer = Some(footer),
          footer_icon = Some(footer_icon),
          ts = Some(ts),
          actions = Some(Seq(
            ActionField(
              name = "Resolve",
              text = "Resolve",
              `type` = "button"
            ),
            ActionField(
              name = "Ignore",
              text = "Ignore",
              `type` = "button"
            ),
            ActionField(
              name = "Select",
              text = "Select Assignee...",
              `type` = "static_select"
            )
          ))

        )
      ))
    )
  }

}
