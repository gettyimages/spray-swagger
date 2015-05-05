package com.tecsisa.akka.http.swagger

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.model.ContentTypes
import akka.http.server.Directives
import akka.http.testkit.ScalatestRouteTest
import akka.stream.scaladsl.ImplicitFlowMaterializer
import akka.testkit.TestActorRef
import com.tecsisa.akka.http.swagger.samples.{PetHttpService, UserHttpService}
import com.tecsisa.akka.http.swagger.utils.JsonMarshalling
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContextExecutor
import scala.reflect.runtime.universe._


class SwaggerHttpServiceSpec
  extends WordSpec
  with Matchers
  with ScalatestRouteTest {
  val systemTest: ActorSystem = system

  val swaggerService: SwaggerHttpService = TestActorRef.create(system, Props(new SwaggerHttpService
    with Actor with ImplicitFlowMaterializer with Directives with JsonMarshalling {
    def apiTypes = Seq(typeOf[PetHttpService], typeOf[UserHttpService])
    def apiVersion = "2.0"
    override def baseUrl = "http://some.domain.com/api"
    override def docsPath = "docs-are-here"
    override def receive = Actor.emptyBehavior
    override implicit val system: ActorSystem = systemTest
    override implicit val formats = org.json4s.DefaultFormats
    override implicit def executor: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global // Not needed for testing
    val route = swaggerRoutes
  }), "test").underlyingActor


  implicit val formats = org.json4s.DefaultFormats

  "The SwaggerHttpService" when {
    "accessing the root doc path" should {
      "return the basic set of api info" in {
        Get("/docs-are-here") ~> swaggerService.swaggerRoutes ~> check {
          handled shouldBe true
          contentType shouldBe ContentTypes.`application/json`
          val response = parse(responseAs[String])
          (response \ "apiVersion").extract[String] shouldEqual "2.0"
          (response \ "swaggerVersion").extract[String] shouldEqual "1.2"
          val apis = (response \ "apis").extract[Array[JValue]]
          apis.size shouldEqual 2
          val api = apis.filter(a => (a \ "path").extract[String] == "/pet").head
          (api \ "description").extract[String] shouldEqual "Operations about pets."
          (api \ "path").extract[String] shouldEqual "/pet"
          //need api info
        }
      }
    }
    "accessing a sub-resource" should {
      "return the api description" in {
        Get("/docs-are-here/pet") ~> swaggerService.swaggerRoutes ~> check {
          handled shouldBe true
          contentType shouldBe ContentTypes.`application/json`
          val response = parse(responseAs[String])
          (response \ "apiVersion").extract[String] shouldEqual "2.0"
          (response \ "resourcePath").extract[String] shouldEqual "/pet"
          val apis = (response \ "apis").extract[Array[JValue]]
          apis.size shouldEqual 2
          val api = apis.filter(a => (a \ "path").extract[String] == "/pet/{petId}").head
          (api \ "path").extract[String] shouldEqual "/pet/{petId}"
          val ops = (api \ "operations").extract[Array[JValue]]
          ops.size shouldEqual 3
          val models = (response \ "models").extract[JObject]
          val pet = (models \ "Pet").extract[JObject]
          (pet \ "id").extract[String] shouldEqual "Pet"
          (pet \ "name").extract[String] shouldEqual "Pet"
          (pet \ "qualifiedType").extract[String] shouldEqual "com.tecsisa.akka.http.swagger.samples.Pet"
          (pet \ "properties" \ "id" \ "type").extract[String] shouldEqual "int"
          (pet \ "properties" \ "id" \ "qualifiedType").extract[String] shouldEqual "int"
          (pet \ "properties" \ "id" \ "required").extract[Boolean] shouldEqual false
          (pet \ "properties" \ "id" \ "type").extract[String] shouldEqual "int"
          (pet \ "properties" \ "id" \ "allowableValues").extract[JValue] shouldEqual JObject(List())
          (pet \ "properties" \ "name" \ "type").extract[String] shouldEqual "string"
          (pet \ "properties" \ "birthDate" \ "type").extract[String] shouldEqual "Date"
          (pet \ "subTypes").extract[JValue] shouldEqual JArray(List())
        }
      }
    }
  }

  }
