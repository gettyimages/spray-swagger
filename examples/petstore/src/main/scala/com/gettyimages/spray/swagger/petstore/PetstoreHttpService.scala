package com.gettyimages.spray.swagger.petstore

import scala.annotation.meta.field

import akka.actor.Actor

import org.json4s.DefaultFormats

import spray.routing._
import spray.http.HttpHeaders.RawHeader
import spray.httpx.Json4sSupport

import com.wordnik.swagger.annotations._

import com.gettyimages.spray.swagger.SwaggerHttpService

import scala.reflect.runtime.universe.typeOf
import spray.http.HttpHeaders.RawHeader
import com.gettyimages.spray.swagger.petstore.Tag
import com.gettyimages.spray.swagger.petstore.Pet
import com.gettyimages.spray.swagger.petstore.Category

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
      def modelTypes = Seq(typeOf[Pet], typeOf[Tag], typeOf[Category])
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

  @ApiOperation(httpMethod = "GET", response = classOf[Pet], value = "Find pet by ID", notes = "Returns a pet based on ID", nickname = "getPetById")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "petId", required = true, dataType = "int", paramType = "path", value = "ID of pet that needs to be fetched")))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Invalid ID supplied"), new ApiResponse(code = 404, message = "Pet not found")))
  def petGetRoute = get { path("pet" / IntNumber) { petId =>
    complete(s"Hello, I'm pet ${petId}!")
  } }

  def routes = petGetRoute
}

object Status extends Enumeration {
  type Status = Value
  val available, pending, sold = Value
}

@ApiModel(description = "A pet object")
case class Pet(
  @(ApiModelProperty @field)(value = "unique identifier for the pet")
  val id: Int,
  @(ApiModelProperty @field)
  val name: String,
  @(ApiModelProperty @field)(dataType = "Category")
  val category: Option[Category],
  @(ApiModelProperty @field)
  val photoUrls: Option[Array[String]],
  @(ApiModelProperty @field)(dataType = "Tag")
  val tags: Option[Array[Tag]],
  @(ApiModelProperty @field)(value = "pet status in the store")
  val status: Option[Status.Status])

@ApiModel
case class Tag(
  @(ApiModelProperty @field)
  val id: Option[Int],
  @(ApiModelProperty @field)
  val name: Option[String])

@ApiModel
case class Category(
  @(ApiModelProperty @field)
  val id: Option[Int],
  @(ApiModelProperty @field)
  val name: Option[String])
