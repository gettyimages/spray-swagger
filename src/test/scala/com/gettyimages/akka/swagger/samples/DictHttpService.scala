package com.gettyimages.akka.swagger.samples

import io.swagger.annotations._
import javax.ws.rs.Path
import spray.json._
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.unmarshalling._
import akka.http.scaladsl.server.directives.MarshallingDirectives
import akka.stream.ActorMaterializer
import akka.actor.ActorSystem

@Api(value = "/dict", description = "This is a dictionary api.")
@Path("/")
trait DictHttpService
    extends Directives
    with ModelFormats {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  implicit val actorSystem = ActorSystem("mysystem")
  implicit val materializer = ActorMaterializer()

  val me = DictEntry("", "", None)

  val yoyo = as[DictEntry]

  var dict: Map[String, String] = Map[String, String]()

  @ApiOperation(value = "Add dictionary entry.", notes = "Will a new entry to the dictionary, indexed by key, with an optional expiration value.", httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "entry", value = "Key/Value pair of dictionary entry, with optional expiration time.", required = true, dataType = "DictEntry", paramType = "body")))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Client Error")))
  def createRoute = post {
    path("/dict") {
      entity(as[DictEntry]) { e ⇒
        dict += e.key -> e.value
        complete("ok")
      }
    }
  }

  @ApiOperation(value = "Find entry by key.", notes = "Will look up the dictionary entry for the provided key.", response = classOf[DictEntry], httpMethod = "GET", nickname = "someothername")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "key", value = "Keyword for the dictionary entry.", required = true, dataType = "String", paramType = "path")))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "Dictionary does not exist.")))
  def readRoute = get {
    path("/dict" / Segment) { key ⇒
      complete(dict(key))
    }
  }

}
