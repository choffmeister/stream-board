package de.choffmeister.streamboard

import akka.Done
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import de.choffmeister.microserviceutils.ShutdownDelay
import de.choffmeister.streamboard.http.HttpServer
import de.choffmeister.streamboard.utils.Logger
import de.choffmeister.streamboard.utils.RichConfig._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class Application(implicit val system: ActorSystem, mat: Materializer) extends Logger {
  val httpConfig = system.settings.config.getConfig("http")
  val interface = httpConfig.getString("interface")
  val port = httpConfig.getInt("port")
  val webDirectory = httpConfig.getOptionalPath("web-directory")
  val httpServer = new HttpServer(interface, port, webDirectory)
}

object Application extends App {
  implicit val system = ActorSystem("stream-board")
  implicit val materializer = ActorMaterializer()

  val application = new Application()
  val init = for {
    _ <- application.httpServer.bind()
  } yield Done

  init.onComplete {
    case Success(_) =>
      system.log.info("Initialization done")
    case Failure(cause) =>
      system.log.error(cause, "Initialization error")
      Thread.sleep(1000L)
      System.exit(1)
  }

  ShutdownDelay.registerShutdownHook()
  ShutdownDelay.isShuttingDown.onSuccess { case delay =>
    system.log.info("Received signal to terminate, shutting down in {} ms", delay.toMillis)
  }
}
