package util

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.*

import scala.concurrent.{ExecutionContext, Future}

trait HttpRequestTrait:

  def getData(url: String, headers: List[HttpHeader], entity: RequestEntity, method: HttpMethod = HttpMethods.POST) =
    given ActorSystem = ActorSystem("SingleRequest")

    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(
        HttpRequest(
          method=method,
          uri = url,
          headers=headers,
          entity=entity)
      )

    responseFuture
