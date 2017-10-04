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
package com.outworkers.phantom
import com.outworkers.phantom.macros.RootMacro

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.whitebox

@compileTimeOnly("enable macro paradise to expand macro annotations")
class cassandra extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro TableAnnotationMacro.impl
}

@macrocompat.bundle
class TableAnnotationMacro(val c: whitebox.Context) extends RootMacro {

  import c.universe._

  def impl(annottees: c.Expr[Any]*): Tree = {
    annottees.map(_.tree) match {

      case (classDef @ q"$mods class $tpname[..$tparams] $ctorMods(...$params) extends { ..$earlydefns } with ..$parents { $self => ..$stats }")
        :: Nil if classDef.tpe <:< typeOf[CassandraTable[_, _]] =>


        classDef.children.foreach(c => println(showCode(c)))

        q"""$classDef"""

      case (classDef @ q"$mods class $tpname[..$tparams] $ctorMods(...$params) extends { ..$earlydefns } with ..$parents { $self => ..$stats }")
        :: q"object $objName extends { ..$objEarlyDefs } with ..$objParents { $objSelf => ..$objDefs }"
        :: Nil if classDef.tpe <:< typeOf[CassandraTable[_, _]] =>

        classDef.children.foreach(c => println(showCode(c)))

        q"""$classDef"""

      case _ => c.abort(c.enclosingPosition, "Invalid annotation target, table can only be applied to Cassandra tables")
    }
  }
}