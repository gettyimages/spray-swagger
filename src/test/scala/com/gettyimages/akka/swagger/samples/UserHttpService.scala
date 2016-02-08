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
package com.gettyimages.akka.swagger.samples

import io.swagger.annotations._
import javax.ws.rs.Path
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives._
import spray.json.DefaultJsonProtocol
import akka.stream.ActorMaterializer
import akka.actor.ActorSystem

@Api(value = "/user", description = "Operations about users.", produces = "application/json")
@Path("/user")
trait UserHttpService
    extends Directives
    with ModelFormats {
  implicit val actorSystem = ActorSystem("mysystem")
  implicit val materializer = ActorMaterializer()
  import actorSystem.dispatcher
  @ApiOperation(value = "Updated user", notes = "This can only be done by the logged in user.", nickname = "updateUser", httpMethod = "PUT")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "username", value = "ID of user that needs to be updated", required = true, dataType = "string", paramType = "path"),
    new ApiImplicitParam(name = "body", value = "Updated user object.", required = false, dataType = "string", paramType = "form")))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "User not found"),
    new ApiResponse(code = 400, message = "Invalid username supplied")))
  def readRoute = put {
    path("/user" / Segment) { id ⇒
      complete(id)
    }
  }

  @ApiOperation(value = "Get user by name", notes = "", response = classOf[User], nickname = "getUserByName", httpMethod = "GET")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "userId", value = "ID of user that needs to be updated", required = true, dataType = "string", paramType = "path"),
    new ApiImplicitParam(name = "name", value = "Updated name of the user.", required = false, dataType = "string", paramType = "form"),
    new ApiImplicitParam(name = "status", value = "Updated status of the user.", required = false, dataType = "string", paramType = "form")))
  @ApiResponses(Array(
    new ApiResponse(code = 404, message = "User does not exist.")))
  def getUser = post {
    path("/user" / Segment) { id ⇒
      {
        formFields('name, 'status) { (name, status) ⇒
          complete("ok")
        }
      }
    }
  }

}
case class User(username: String, status: String)
