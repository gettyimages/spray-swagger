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
 * limitations under the License.*/
package com.tecsisa.akka.http.swagger

import com.tecsisa.akka.http.swagger.samples.{TestApiWithOnlyDataType, TestApiWithPathOperation}
import com.wordnik.swagger.core.SwaggerContext
import org.scalatest.{Matchers, WordSpec}

import scala.reflect.runtime.universe._

class AkkaHttpApiScannerSpec
  extends WordSpec
  with Matchers {

  "The AkkaHttpApiScanner object" when {
    "listing resources" should {
      "identify correct API classes based on type and API annotations" in {
        val classes = new AkkaHttpApiScanner(Seq(typeOf[TestApiWithPathOperation], typeOf[TestApiWithOnlyDataType])).classes
        classes.length shouldEqual (2)
        classes.find(clazz => clazz == SwaggerContext.loadClass("com.tecsisa.akka.http.swagger.samples.TestApiWithPathOperation")).nonEmpty shouldBe (true)
        classes.find(clazz => clazz == SwaggerContext.loadClass("com.tecsisa.akka.http.swagger.samples.TestApiWithOnlyDataType")).nonEmpty shouldBe (true)
      }
    }
  }
}

