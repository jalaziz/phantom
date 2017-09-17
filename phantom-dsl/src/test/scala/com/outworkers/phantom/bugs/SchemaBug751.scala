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
package com.outworkers.phantom.bugs

case class SchemaBug751()

import com.outworkers.phantom.dsl._


case class CardNumber(value: String) extends AnyVal
case class CardStatus(value: String) extends AnyVal
case class CustomerId(value: UUID)   extends AnyVal

case class CustomerCard(number: CardNumber, status: CardStatus)

// removed other fields from "Customer" for brevity
case class Customer(id: CustomerId, cards: List[CustomerCard])

abstract class Customers extends Table[Customers, Customer] {

  // Kebs macros supply the implicit JsonReader[CustomerCard] and JsonWriter[CustomerCard]
  // used by the toJson and parseJson methods from spray.json package
  implicit val customerCardPrimitive: Primitive[CustomerCard] =
  Primitive.json[CustomerCard](_.toJson.toString)(_.parseJson.convertTo[CustomerCard])

  override def tableName: String = "customers"

  object id          extends UUIDColumn with PartitionKey
  object cards       extends JsonListColumn[CustomerCard]
}