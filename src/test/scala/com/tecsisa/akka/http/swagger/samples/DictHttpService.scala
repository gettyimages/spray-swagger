package com.tecsisa.akka.http.swagger.samples

import akka.actor.{Actor, ActorSystem}
import akka.http.server.Directives
import akka.stream.scaladsl.ImplicitFlowMaterializer
import com.tecsisa.akka.http.swagger.utils.JsonMarshalling
import com.wordnik.swagger.annotations._
import com.wordnik.swagger.core.util.JsonSerializer

import scala.concurrent.ExecutionContextExecutor


@Api(value = "/dict", description = "This is a dictionary api.")
trait DictHttpService {
  _: Actor with ImplicitFlowMaterializer with Directives
    with JsonMarshalling =>

  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor

  implicit val formats = org.json4s.DefaultFormats

  var dict: Map[String, String] = Map[String, String]()

  @ApiOperation(value = "Add dictionary entry.", notes = "Will a new entry to the dictionary, indexed by key, with an optional expiration value.", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "entry", value = "Key/Value pair of dictionary entry, with optional expiration time.", required = true, dataType = "DictEntry", paramType = "body")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Client Error")
  ))
  def createRoute = post {
    path("/dict") {
      entity(as[DictEntry]) { e =>
        dict += e.key -> e.value
        complete("ok")
      }
    }
  }

  @ApiOperation(value = "Find entry by key.", notes = "Will look up the dictionary entry for the provided key.", response = classOf[DictEntry], httpMethod = "GET", nickname = "someothername")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "key", value = "Keyword for the dictionary entry.", required = true, dataType = "String", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Dictionary does not exist.")
  ))
  def readRoute = get {
    path("/dict" / Segment) { key =>
      complete(dict(key))
    }
  }

  def toJsonString(data: Any): String = {
    if (data.getClass.equals(classOf[String])) {
      data.asInstanceOf[String]
    } else {
      JsonSerializer.asJson(data.asInstanceOf[AnyRef])
    }

  }

}

