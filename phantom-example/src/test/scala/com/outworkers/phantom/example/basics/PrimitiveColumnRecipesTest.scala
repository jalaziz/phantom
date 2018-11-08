/*
 * Copyright 2013 - 2017 Outworkers Ltd.
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
package com.outworkers.phantom.example.basics

import com.outworkers.phantom.dsl._
import com.outworkers.phantom.example.ExampleSuite
import com.outworkers.util.samplers._

class PrimitiveColumnRecipesTest extends ExampleSuite {

  it should "insert a new record in the recipes table and retrieve it" in {
    val sample = gen[Recipe]

    val chain = for {
      store <- database.PrimitiveColumnRecipes.store(sample).future()
      res <- database.PrimitiveColumnRecipes.findRecipeById(sample.id)
    } yield res

    whenReady(chain) { res =>
      res shouldBe defined
      res.value shouldEqual sample
    }
  }

  it should "update the author of a recipe" in {
    val sample = gen[Recipe]
    val newAuthor = gen[ShortString].value

    val chain = for {
      store <- database.Recipes.store(sample).future()
      res <- database.Recipes.findRecipeById(sample.id)
      updateAuthor <- database.Recipes.updateRecipeAuthor(sample.id, newAuthor)
      res2 <- database.Recipes.findRecipeById(sample.id)
    } yield (res, res2)

    whenReady(chain) { case (res, res2) =>
      res shouldBe defined
      res.value shouldEqual sample
      res2 shouldBe defined
      res2.value shouldEqual sample.copy(author = newAuthor)
    }
  }

  it should "retrieve an empty ingredients set" in {
    val sample = gen[Recipe]

    val chain = for {
      store <- database.PrimitiveColumnRecipes.insert()
        .value(_.id, sample.id)
        .value(_.name, sample.name)
        .value(_.title, sample.title)
        .value(_.author, sample.author)
        .value(_.description, sample.description)
        .value(_.timestamp, sample.timestamp)
        .future()
      res <- database.PrimitiveColumnRecipes.findRecipeById(sample.id)
    } yield res

    whenReady(chain) { res =>
      res shouldBe defined
      res.value.ingredients shouldBe empty
      res.value.props shouldBe empty
    }
  }
}
