/**
 * Copyright 2013 Getty Imges, Inc.
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

//based on swagger specs - https://github.com/wordnik/swagger-core/wiki/API-Declaration
case class ListApi(path: String, 
					description: Option[String], 
					operations: Option[List[Operation]])

case class ApiListing(swaggerVersion: String, 
						apiVersion: String, 
						basePath: String,
						resourcePath: String,
						apis: List[ListApi],
						models: Option[Map[String, Model]])
					
case class ResourceListing(swaggerVersion: String,
                             apiVersion: String,
                             apis: List[ListApi],
                             info: Option[ApiInfo] = None,
                             authorizations: Option[Map[String, Authorization]] = None
)

abstract class Authorization(
    val `type`: String
)

case class ApiKeyAuthorization(
    val passAs: String
) extends Authorization("apiKey")

case class ApiInfo(
    title: String,
    description: String,
    contact: Option[String] = None,
    termsOfServiceUrl: Option[String] = None,
    license: Option[String] = None,
    licenseUrl: Option[String] = None 
)
 
case class Model(id: String,
                properties: Map[String, ModelProperty],
                description: Option[String] = None,
                `extends`: Option[String] = None,
                discriminator: Option[String] = None,
                subTypes: Option[List[String]] = None
) {
}

case class ModelProperty(
                  description: String,
                  `type`: String,
                  defaultValue: Option[String] = None,
                  enum: Option[Set[String]] = None,
                  required: Boolean = true,
                  items: Option[Map[String,String]] = None,
                  uniqueItems: Option[Boolean] = None,
                  maximum: Option[Int] = None,
                  minimum: Option[Int] = None,
                  position: Int = 0
)
                  
case class Operation(httpMethod: String,
                     summary: String,
                     nickname: String,
                     responseClass: String = "void",
                     notes: Option[String] = None,
                     deprecated: Boolean = false,
                     parameters: List[Parameter] = Nil,
                     responseMessages: Option[List[Response]] = None,
                     produces: Option[List[String]] = None,
                     consumes: Option[List[String]] = None,
                     position: Int = 0)

case class Endpoint(path: String,
                    description: String,
                    secured: Boolean = false,
                    operations: List[Operation] = Nil)

case class Response(code: Int,
                 message: String,
                 responseModel: Option[String] = None)

case class Parameter(name: String,
                 description: String,
                 `type`: String,
                 paramType: String,
                 notes: Option[String] = None,
                 defaultValue: Option[String] = None,                     
                 required: Boolean = true,
                 allowMultiple: Boolean = false) {
  
  private val ValidParamTypes = Set("path", "query", "body", "header", "form") 
  
  require(
    ValidParamTypes.contains(paramType.toLowerCase),
    s"Invalid ParamType: ${paramType} ValidParamTypes: $ValidParamTypes" 
  )
}