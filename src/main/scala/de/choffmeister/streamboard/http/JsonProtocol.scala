package de.choffmeister.streamboard.http

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import de.choffmeister.microserviceutils.json.{HalResourceJsonProtocol, InstantJsonProtocol}
import spray.json._

trait JsonProtocol extends DefaultJsonProtocol
    with SprayJsonSupport
    with HalResourceJsonProtocol
    with InstantJsonProtocol {
}
