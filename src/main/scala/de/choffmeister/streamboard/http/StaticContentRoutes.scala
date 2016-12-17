package de.choffmeister.streamboard.http

import java.nio.file.Path

import akka.actor._
import akka.http.scaladsl.model.{HttpCharsets, MediaTypes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.Materializer

import scala.concurrent.ExecutionContext

object StaticContentRoutes {
  def apply(webDirectory: Option[Path])(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext): Route = {
    webDirectory match {
      case Some(dir) =>
        def index = getFromFile(dir.resolve("index.html").toFile, MediaTypes.`text/html`.toContentType(HttpCharsets.`UTF-8`))
        encodeResponse {
          // serve asset files
          path("assets" / Segment)(file => getFromFile(dir.resolve("assets").resolve(file).toFile)) ~
          // serve index.html as index
          pathEndOrSingleSlash(index)
        }
      case _ =>
        reject
    }
  }
}
