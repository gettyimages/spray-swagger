/**
 * Copyright 2014 Getty Imges, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gettyimages.spray.swagger

import com.gettyimages.spray.swagger.model.{License, Contact, Info}
import akka.actor.{ActorRefFactory, ActorSystem}
import io.swagger.annotations._
import javax.ws.rs.Path
import io.swagger.jaxrs.config.ReaderConfig
import spray.routing.HttpService
import spray.httpx.Json4sSupport
import scala.reflect.runtime.universe._
import scala.collection.JavaConversions._


case class Dog(breed: String)

class NestedService(system: ActorSystem) {self =>
  val swaggerService = new SwaggerHttpService {
    override val apiTypes = Seq(typeOf[Dogs.type])
    override val host = "some.domain.com"

    override val basePath = "api-doc"

    override val description = "Dogs love APIs"

    override val readerConfig = new ReaderConfig {
      def getIgnoredRoutes(): java.util.Collection[String] = List()

      def isScanAllResources(): Boolean = false
    }

    override val info: Info = Info(
      description = "Dogs love APIs",
      version = "1.0",
      title = "Test API Service",
      termsOfService = "Lenient",
      contact = Some(Contact("Lassie", "http://lassie.com", "lassie@tvland.com")),
      license = Some(License("Apache", "http://license.apache.com")))

    implicit def actorRefFactory: ActorRefFactory = system
  }

  @Api(value="/dogs", description="This is the dogs resource")
  @Path(value = "/dogs")
  object Dogs extends HttpService with Json4sSupport {

    implicit def actorRefFactory: ActorRefFactory = self.system
    implicit val json4sFormats = org.json4s.DefaultFormats

    @ApiOperation(value="List all of the dogs",
      notes = "Dogs are identified by unique strings",
      response = classOf[ListReply[Dog]],
      httpMethod = "GET",
      nickname = "getDogs"
    )
    @ApiResponses(Array(
      new ApiResponse(code = 200,
        message = "OK"),
      new ApiResponse(code = 404, message = "Dog not found"),
      new ApiResponse(code = 500, message = "Internal Server Error")
    ))
    def getDogs = path("dogs"){
      complete("dogs")
    }
  }
}