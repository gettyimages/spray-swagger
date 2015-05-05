package com.tecsisa.akka.http.swagger.utils

import akka.http.marshalling.{PredefinedToEntityMarshallers, _}
import akka.http.model.{ContentTypeRange, MediaRange, MediaTypes}
import akka.http.unmarshalling.Unmarshaller.UnsupportedContentTypeException
import akka.http.unmarshalling.{PredefinedFromEntityUnmarshallers, _}
import akka.http.util.FastFuture
import akka.stream.FlowMaterializer
import org.json4s.Formats

import scala.concurrent.ExecutionContext

trait JsonMarshalling {
  implicit def feum[A: Manifest](implicit formats: Formats, m: FlowMaterializer, ec: ExecutionContext): FromEntityUnmarshaller[A] =
    PredefinedFromEntityUnmarshallers.stringUnmarshaller.flatMapWithInput { (entity, s) =>
      if (entity.contentType().mediaType == MediaTypes.`application/json`)
        FastFuture.successful(org.json4s.native.Serialization.read[A](s))
      else
        FastFuture.failed(
          UnsupportedContentTypeException(ContentTypeRange(MediaRange(MediaTypes.`application/json`)))
        )
    }

  implicit def tem[A <: AnyRef](implicit formats: Formats): ToEntityMarshaller[A] = {
    val stringMarshaller = PredefinedToEntityMarshallers.stringMarshaller(MediaTypes.`application/json`)
    stringMarshaller.compose(org.json4s.native.Serialization.writePretty[A])
  }
}