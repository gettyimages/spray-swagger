# spray-swagger

Spray-Swagger brings [swagger](wordnik/swagger-core) support for [spray](spray.io) Apis. THe included ```SwaggerHttpService``` route will inspect Scala types with Swagger annotations and build a swagger compliant endpoint compatible with a [swagger compliant ui](wordnik/swagger-ui).

It is beneficial to read the [swagger spec](https://github.com/wordnik/swagger-spec/blob/master/versions/1.2.md) to understand the api and resource declaration semantics behind Swagger as well as Swagger's annotation library.

## Getting Spray-Swagger

The jars are hosted on [sonatype](https://oss.sonatype.org). Using Sbt:

```
libraryDependencies += "com.gettyimages" % "spray-swagger_2.10" % "0.3.0"
```

## SwaggerHttpService

The ```SwaggerHttpService``` is a trait extending Spray's ```HttpService```. It will generate the appropriate Swagger json schema based on a set of inputs. These inputs are passed in by implementing required function definitions which describe your api.

The  ```SwagerHttpService``` will contain a ```routes``` property you can concatenate along with your existing routes. This will expose an endpoint at ```<baseUrl>/<specPath>/<resourcePath>``` with the specified ```apiVersion``` and ```swaggerVersion```.

The service also requires a set of ```apiTypes``` and ```modelTypes``` you want to expose via Swagger. These types include the appropriate swagger annotations for describing your api. The ```SwaggerHttpService``` will inspect these annotations and build the approrpiate Swagger response.

An example ```SwaggerHttpService``` snippet which exposes [Wordnik's PetStore](http://swagger.wordnik.com/) resources, ```Pet```, ```User``` and ```Store```. The routes property can be concatenated to your other route definitions:

```
new SwaggerHttpService {
       def actorRefFactory = context
       def apiTypes = Seq(typeOf[PetService], typeOf[UserService], typeOf[StoreService])
       def modelTypes = Seq(typeOf[Pet], typeOf[Tag], typeOf[Category])
       def apiVersion = "1.0"
       def swaggerVersion = "1.2"
       def baseUrl = "http://localhost:8080"
       def specPath = "api"
       def resourcePath = "api-docs"
     }.routes
```

## Adding Swagger Annotations

## Swagger UI

This library does not include [Swagger's UI](wordnik/swagger-ui) only the api support for powering a UI. Adding such a UI to your Spray app is easy with Spray's ```getFromResource``` and ```getFromResourceDirectory``` support.

To add a Swagger UI to your site, simply drop the static site files into the resources directory of your project. The following trait will expose a ```swagger``` route hosting files from the ```resources/swagger/`` directory: 

```
trait Site extends HttpService {
  val site =
    path("swagger") { getFromResource("swagger/index.html") } ~
      getFromResourceDirectory("swagger")
}
```

You can then mix this trait with a new or existing Spray class with an ``actorRefFactory``` and concatenate the ```site``` route value to your existing route definitions.
