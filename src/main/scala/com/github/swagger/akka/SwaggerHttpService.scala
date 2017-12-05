/*
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
package com.github.swagger.akka

import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.server.{Directives, PathMatchers, Route}
import com.github.swagger.akka.model.{Info, asScala, scala2swagger}
import io.swagger.jaxrs.Reader
import io.swagger.jaxrs.config.DefaultReaderConfig
import io.swagger.models.auth.SecuritySchemeDefinition
import io.swagger.models.{ExternalDocs, Scheme, Swagger}
import io.swagger.util.{Json, Yaml}
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.collection.mutable.{ListBuffer, Map => MutableMap}
import scala.util.control.NonFatal

object SwaggerHttpService {

  val readerConfig = new DefaultReaderConfig

  def removeInitialSlashIfNecessary(path: String): String =
    if(path.startsWith("/")) removeInitialSlashIfNecessary(path.substring(1)) else path
  def prependSlashIfNecessary(path: String): String  = if(path.startsWith("/")) path else s"/$path"

  private[akka] def apiDocsBase(path: String) = PathMatchers.separateOnSlashes(removeInitialSlashIfNecessary(path))
  private[akka] val logger = LoggerFactory.getLogger(classOf[SwaggerHttpService])
}

trait SwaggerGenerator {
  import SwaggerHttpService._
  def apiClasses: Set[Class[_]]
  def host: String = ""
  def basePath: String = "/"
  def apiDocsPath: String = "api-docs"
  def info: Info = Info()
  def schemes: List[Scheme] = List(Scheme.HTTP)
  def securitySchemeDefinitions: Map[String, SecuritySchemeDefinition] = Map.empty
  def externalDocs: Option[ExternalDocs] = None
  def vendorExtensions: Map[String, Object] = Map.empty
  def unwantedDefinitions: Seq[String] = Seq.empty

  def swaggerConfig: Swagger = {
    val modifiedPath = prependSlashIfNecessary(basePath)
    val swagger = new Swagger().basePath(modifiedPath).info(info).schemes(asJavaMutableList(schemes))
    if(StringUtils.isNotBlank(host)) swagger.host(host)
    swagger.setSecurityDefinitions(asJavaMutableMap(securitySchemeDefinitions))
    swagger.vendorExtensions(asJavaMutableMap(vendorExtensions))
    externalDocs match {
      case Some(ed) => swagger.externalDocs(ed)
      case None => swagger
    }
  }

  def reader = new Reader(swaggerConfig, readerConfig)

  def generateSwaggerJson: String = {
    try {
      Json.pretty().writeValueAsString(filteredSwagger)
    } catch {
      case NonFatal(t) => {
        logger.error("Issue with creating swagger.json", t)
        throw t
      }
    }
  }

  def generateSwaggerYaml: String = {
    try {
      Yaml.pretty().writeValueAsString(filteredSwagger)
    } catch {
      case NonFatal(t) => {
        logger.error("Issue with creating swagger.yaml", t)
        throw t
      }
    }
  }

  private[akka] def asJavaMutableList[T](list: List[T]) = {
    (new ListBuffer[T] ++ list).asJava
  }

  private[akka] def asJavaMutableMap[K, V](map: Map[K, V]) = {
    (MutableMap.empty[K, V] ++ map).asJava
  }

  private[akka] def filteredSwagger: Swagger = {
    val swagger: Swagger = reader.read(apiClasses.asJava)
    if (!unwantedDefinitions.isEmpty) {
      swagger.setDefinitions(asScala(swagger.getDefinitions).filterKeys(definitionName => !unwantedDefinitions.contains(definitionName)).asJava)
    }
    swagger
  }
}

trait SwaggerHttpService extends Directives with SwaggerGenerator {
  import SwaggerHttpService._
  def routes: Route = {
    val base = apiDocsBase(apiDocsPath)
    path(base / "swagger.json") {
      get {
        complete(HttpEntity(MediaTypes.`application/json`, generateSwaggerJson))
      }
    } ~
      path(base / "swagger.yaml") {
        get {
          complete(HttpEntity(CustomMediaTypes.`text/vnd.yaml`, generateSwaggerYaml))
        }
      }
  }
}
