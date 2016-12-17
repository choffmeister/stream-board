package de.choffmeister.streamboard.http

import java.nio.file.Path

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import de.choffmeister.microserviceutils.ShutdownDelay
import de.choffmeister.microserviceutils.http.{LoggingDirective, PagingDirective}

import scala.concurrent.{ExecutionContext, Future}

class HttpServer(interface: String, port: Int, webDirectory: Option[Path])(implicit val system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends JsonProtocol with PagingDirective with LoggingDirective with WebsocketStreamDirective {
  val routes =
    path("_health") {
      if (!ShutdownDelay.isShuttingDown.isCompleted) complete(StatusCodes.OK, "Ok")
      else complete(StatusCodes.InternalServerError, "Shutting down")
    } ~
    pathPrefix("api") {
      reject
    } ~
    StaticContentRoutes(webDirectory)

  def bind(): Future[ServerBinding] = {
    val liveRoutes = logRequestResponse(this.getClass, LoggingDirective.defaultAugmenter)(routes)

    Http().bindAndHandle(liveRoutes, interface, port)
  }
}
