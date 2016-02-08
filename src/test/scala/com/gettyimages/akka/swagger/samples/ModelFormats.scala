package com.gettyimages.akka.swagger.samples

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.unmarshalling._
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.server.directives.MarshallingDirectives

/**
 * @author rleibman
 */
trait ModelFormats
    extends DefaultJsonProtocol
    with SprayJsonSupport {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  implicit val dictEntryformats = jsonFormat3(DictEntry)
}
