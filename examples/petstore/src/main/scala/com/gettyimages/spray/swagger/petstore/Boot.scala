package com.gettyimages.spray.swagger.petstore

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

object Boot extends App {
  implicit val system = ActorSystem("petstore")

  val petstore = system.actorOf(Props[PetstoreServiceActor], "petstore-http-actor")

  IO(Http) ! Http.Bind(petstore, "localhost", port = 8080)
}
