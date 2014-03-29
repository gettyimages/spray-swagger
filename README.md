# spray-swagger

Spray-swagger brings [swagger](wordnik/swagger-core) support for [spray](spray.io) based APIs. This library includes a set of annotations for service traits, route functions, and models which are exposed via the included ```SwaggerHttpService```. The endpoint can be dropped into a [swagger compliant ui](wordnik/swagger-ui).

It is beneficial to read the [swagger spec](https://github.com/wordnik/swagger-spec/blob/master/versions/1.2.md) to understand the api and resource declaration semantics behind Swagger.

## SwaggerHttpService

The ```SwaggerHttpService``` is a trait extending Spray's ```HttpService```. It will generate the appropriate Swagger json schema based on a set of inputs. These inputs are passed in by implementing required function definitions which describe your api.

The  ```SwagerHttpService``` will contain a ```routes``` property you can concatenate along with your existing routes. This will expose an endpoint at ```<baseUrl>/<specPath>/<resourcePath>``` witht he specified ```apiVersion``` and ```swaggerVersion```.

Finally you include any ```apiTypes``` and ```modelTypes``` you want to expose via Swagger. These are sequences of types which include the appropriate swagger annotations for describing your Api. The ```SwaggerHttpService``` will inspect these annotations and build the approrpiate Swagger response.

An example snippet which exposes two resources, ```Widget``` and ```Order```. The routes property can be concatenated to your other route definitions:

```
new SwaggerHttpService {
       def actorRefFactory = context
       def apiTypes = Seq(typeOf[WidgetService], typeOf[OrderService])
       def modelTypes = Seq(typeOf[Widget], typeOf[Order])
       def apiVersion = "1.0"
       def swaggerVersion = "1.2"
       def baseUrl = "http://localhost:8080"
       def specPath = "api"
       def resourcePath = "api-docs"
     }.routes
```

