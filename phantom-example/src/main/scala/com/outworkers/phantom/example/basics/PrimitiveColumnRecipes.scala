/*
 * Copyright 2013 - 2018 Outworkers Ltd.
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

import java.util.UUID
import scala.concurrent.{Future => ScalaFuture}

import com.outworkers.phantom.Table
import com.outworkers.phantom.dsl._
import org.joda.time.DateTime

abstract class PrimitiveColumnRecipes extends Table[PrimitiveColumnRecipes, Recipe] {
  object id extends Col[UUID] with PartitionKey {
    // You can override the name of your key to whatever you like.
    // The default will be the name used for the object, in this case "id".
    override lazy val name = "the_primary_key"
  }

  // Now we define a column for each field in our case class.
  object name extends Col[String]
  object title extends Col[String]
  object author extends Col[String]
  object description extends Col[String]

  // Custom data types can be stored easily.
  // Cassandra collections target a small number of items, but usage is trivial.
  object ingredients extends Col[Set[String]]

  object props extends Col[Map[String, String]]

  object timestamp extends Col[DateTime]

  // you can even rename the table in the schema to whatever you like.
  override lazy val tableName = "my_custom_table"

  // now you have the full power of Cassandra in really cool one liners.
  // The future will do all the heavy lifting for you.
  // If there is an error you get a failed Future.
  // If there is no matching record you get a None.
  // The "one" method will select a single record, as it's name says.
  // It will always have a LIMIT 1 in the query sent to Cassandra.
  // select.where(_.id eqs UUID.randomUUID()).one() translates to
  // SELECT * FROM my_custom_table WHERE id = the_id_value LIMIT 1;
  def findRecipeById(id: UUID): ScalaFuture[Option[Recipe]] = {
    select.where(_.id eqs id).one()
  }
}
