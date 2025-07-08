/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.querydsl.scala

object Numeric {
  implicit val byte: Numeric[Byte] = new Numeric[Byte]
  implicit val byte2: Numeric[java.lang.Byte] = new Numeric[java.lang.Byte]
  implicit val double: Numeric[Double] = new Numeric[Double]
  implicit val double2: Numeric[java.lang.Double] = new Numeric[java.lang.Double]
  implicit val float: Numeric[Float] = new Numeric[Float]
  implicit val float2: Numeric[java.lang.Float] = new Numeric[java.lang.Float]
  implicit val int: Numeric[Int] = new Numeric[Int]
  implicit val int2: Numeric[java.lang.Integer] = new Numeric[java.lang.Integer]
  implicit val long: Numeric[Long] = new Numeric[Long]
  implicit val long2: Numeric[java.lang.Long] = new Numeric[java.lang.Long]
  implicit val short: Numeric[Short] = new Numeric[Short]
  implicit val short2: Numeric[java.lang.Short] = new Numeric[java.lang.Short]

  implicit val bigDecimal: Numeric[java.math.BigDecimal] = new Numeric[java.math.BigDecimal]
  implicit val bigInteger: Numeric[java.math.BigInteger] = new Numeric[java.math.BigInteger]

  implicit val richByte: Numeric[scala.runtime.RichByte] = new Numeric[scala.runtime.RichByte]
  implicit val richChar: Numeric[scala.runtime.RichChar] = new Numeric[scala.runtime.RichChar]
  implicit val richDouble: Numeric[scala.runtime.RichDouble] = new Numeric[scala.runtime.RichDouble]
  implicit val richFloat: Numeric[scala.runtime.RichFloat] = new Numeric[scala.runtime.RichFloat]
  implicit val richInt: Numeric[scala.runtime.RichInt] = new Numeric[scala.runtime.RichInt]
  implicit val richLong: Numeric[scala.runtime.RichLong] = new Numeric[scala.runtime.RichLong]
  implicit val richShort: Numeric[scala.runtime.RichShort] = new Numeric[scala.runtime.RichShort]

}

/**
 * Type class for Number types
 */
class Numeric[Num] extends Serializable