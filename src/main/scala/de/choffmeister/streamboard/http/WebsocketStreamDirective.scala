package de.choffmeister.streamboard.http

import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import gnieh.diffson.sprayJson._
import spray.json.{JsObject, JsonFormat, RootJsonFormat}

import scala.concurrent.ExecutionContext

trait WebsocketStreamDirective extends JsonProtocol with DiffsonProtocol {
  def websocketStream[T](stream: Source[T, Any])(implicit mat: Materializer, ec: ExecutionContext, format: RootJsonFormat[T]): Route = {
    parameter('diff.as[Boolean].?) { diff =>
      val flow = Flow[Message]
        .mapConcat[T] {
        case tm: TextMessage => tm.textStream.runWith(Sink.ignore); Nil
        case bm: BinaryMessage => bm.dataStream.runWith(Sink.ignore); Nil
      }
      .merge(stream)
        // in case a diff stream was requested, always remember the previous element
        // to be able to create the patch instructions
        .statefulMapConcat(() => {
        var last = JsObject.empty
        (current) => {
          val temp = last
          last = format.write(current).asJsObject
          (temp, last) :: Nil
        }
      })
      // convert to json text message
      .map { case (last, current) =>
        if (diff.contains(true)) {
          val diffWriter = implicitly[JsonFormat[JsonPatch]]
          val diff = JsonDiff.diff(last, current, remember = false)
          TextMessage(diffWriter.write(diff).compactPrint)
        } else {
          TextMessage(current.compactPrint)
        }
      }

      handleWebSocketMessages(flow)
    }
  }
}
