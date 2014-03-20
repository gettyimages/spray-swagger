package com.gettyimages.spray.swagger.petstore

import scala.annotation.meta.field

import akka.actor.Actor

import org.json4s.DefaultFormats

import spray.routing._
import spray.http.HttpHeaders.RawHeader
import spray.httpx.Json4sSupport

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiModel
import com.wordnik.swagger.annotations.ApiModelProperty
import com.wordnik.swagger.annotations.ApiImplicitParams
import com.wordnik.swagger.annotations.ApiImplicitParam

import com.gettyimages.spray.swagger.SwaggerHttpService

import scala.reflect.runtime.universe.typeOf

class PetstoreServiceActor extends Actor with HttpService {

  override def actorRefFactory = context
  
  override def receive = runRoute(
    (new PetstoreHttpService{
      override def actorRefFactory = context
    }).routes ~
    //TODO: Bake in CORS support
    respondWithHeader(RawHeader("Access-Control-Allow-Origin", "http://swagger.wordnik.com")) { (new SwaggerHttpService {
      def actorRefFactory = context
      def apiTypes = Seq(typeOf[PetstoreHttpService])
      def modelTypes = Seq(typeOf[Pet])
      def apiVersion = "1.0"
      def swaggerVersion = "1.2"
      def baseUrl = "http://localhost:8080"
      def specPath = "api"
      def resourcePath = "api-docs" 
    }).routes }
  )
}

@Api(value = "/pet", description = "Operations about pets")
trait PetstoreHttpService extends HttpService with Json4sSupport {

  override def json4sFormats = DefaultFormats

  @ApiOperation(httpMethod = "GET", response = classOf[Pet], value = "Returns a pet based on ID")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "petId", required = true, dataType = "int", paramType = "path", value = "ID of pet that needs to be fetched")
  ))
  def petGetRoute = get { path("pet" / IntNumber) { petId =>
    complete(s"Hello, I'm pet ${petId}!")
  } }

  def routes = petGetRoute
}

@ApiModel(description = "A pet object")
case class Pet(
  @(ApiModelProperty @field)(value = "unique identifier for the pet")
  val id: Int,
  val name: String
)
