package com.gettyimages.akka.swagger

import scala.reflect.runtime.universe._

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.scalatest.Matchers

import org.scalatest.WordSpec

import com.gettyimages.akka.swagger.samples._

import akka.actor.ActorSystem
import akka.http._
import akka.http.scaladsl._
import akka.http.scaladsl.client._
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.testkit._
import akka.http.scaladsl.unmarshalling._
import akka.stream.ActorMaterializer
import spray.json._
import spray.json.DefaultJsonProtocol._
import spray.json.pimpString

class SwaggerHttpServiceSpec
    extends WordSpec
    with Matchers
    with ScalatestRouteTest {

  val myMaterializer = materializer

  val swaggerService = new SwaggerHttpService with HasActorSystem {
    override implicit val actorSystem: ActorSystem = system
    override implicit val materializer: ActorMaterializer = myMaterializer
    override val apiTypes = Seq(typeOf[PetHttpService], typeOf[UserHttpService])
    override val basePath = "/api"
    override val host = "http://some.domain.com"
    //    override def apiVersion = "2.0"
    //    override def baseUrl = "http://some.domain.com/api"
    //    override def docsPath = "docs-are-here"
    //apiInfo, not used
    //authorizations, not used
  }

  implicit val formats = org.json4s.DefaultFormats

  "The SwaggerHttpService" when {
    "accessing the root doc path" should {
      "return the basic set of api info" in {
        Get("/swagger.json") ~> swaggerService.routes ~> check {
          handled shouldBe true
          contentType shouldBe ContentTypes.`application/json`
          val str = responseAs[String]
          val response = parse(str)
          (response \ "swagger").extract[String] shouldEqual "2.0"
          val paths = (response \ "paths").extract[JObject]
          paths.values.size shouldEqual 2
          val petPath = (paths \ "/pet")
          (petPath \ "post" \ "summary").extract[String] shouldEqual "Add a new pet to the store"
        }
      }
    }
  }

}
