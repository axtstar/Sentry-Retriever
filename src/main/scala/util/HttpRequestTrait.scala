package util

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._

import scala.concurrent.{ExecutionContext, Future}

trait HttpRequestTrait {

  def getData(url: String, headers: List[HttpHeader], entity: RequestEntity, method: HttpMethod = HttpMethods.POST) = {
    given system: ActorSystem = ActorSystem("SingleRequest")
    given executionContext: ExecutionContext = system.dispatcher

    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(
        HttpRequest(
          method=method,
          uri = url,
          headers=headers,
          entity=entity)
      )

    responseFuture
  }
}
