package de.choffmeister.streamboard

import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import de.choffmeister.streamboard.http.JsonProtocol
import org.scalatest.concurrent.Eventually
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._

class ApplicationTest extends FlatSpec with ScalatestRouteTest with Matchers with Eventually with JsonProtocol {
  implicit val timeout = RouteTestTimeout(10.seconds)
  val application = new Application()
  val routes = application.httpServer.routes

  "Application" should "respond to health check" in {
    Get("/_health") ~> routes ~> check {
      responseAs[String] should be("Ok")
    }
  }
}
