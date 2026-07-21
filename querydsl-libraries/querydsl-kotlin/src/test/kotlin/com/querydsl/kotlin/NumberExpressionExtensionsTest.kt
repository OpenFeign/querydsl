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

package com.querydsl.kotlin

import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.NumberExpression
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NumberExpressionExtensionsTest {

    private val age: NumberExpression<Int> = Expressions.numberPath(Int::class.javaObjectType, "age")
    private val other: NumberExpression<Int> = Expressions.numberPath(Int::class.javaObjectType, "other")
    private val nil: NumberExpression<Int>? = null
    private val nilValue: Int? = null
    private val nilExpr: Expression<Int>? = null

    @Test
    fun `gt against a value covers the four null combinations`() {
        val n: NumberExpression<Int>? = age
        assertEquals(age.gt(20), n.gt(20))
        assertNull(n.gt(nilValue))
        assertNull(nil.gt(20))
        assertNull(nil.gt(nilValue))
    }

    @Test
    fun `gt against an expression covers the four null combinations`() {
        val n: NumberExpression<Int>? = age
        val e: Expression<Int>? = other
        assertEquals(age.gt(other), n.gt(e))
        assertNull(n.gt(nilExpr))
        assertNull(nil.gt(e))
        assertNull(nil.gt(nilExpr))
    }

    @Test
    fun `goe against a value covers the four null combinations`() {
        val n: NumberExpression<Int>? = age
        assertEquals(age.goe(20), n.goe(20))
        assertNull(n.goe(nilValue))
        assertNull(nil.goe(20))
        assertNull(nil.goe(nilValue))
    }

    @Test
    fun `goe against an expression covers the four null combinations`() {
        val n: NumberExpression<Int>? = age
        val e: Expression<Int>? = other
        assertEquals(age.goe(other), n.goe(e))
        assertNull(n.goe(nilExpr))
        assertNull(nil.goe(e))
        assertNull(nil.goe(nilExpr))
    }

    @Test
    fun `lt against a value covers the four null combinations`() {
        val n: NumberExpression<Int>? = age
        assertEquals(age.lt(20), n.lt(20))
        assertNull(n.lt(nilValue))
        assertNull(nil.lt(20))
        assertNull(nil.lt(nilValue))
    }

    @Test
    fun `lt against an expression covers the four null combinations`() {
        val n: NumberExpression<Int>? = age
        val e: Expression<Int>? = other
        assertEquals(age.lt(other), n.lt(e))
        assertNull(n.lt(nilExpr))
        assertNull(nil.lt(e))
        assertNull(nil.lt(nilExpr))
    }

    @Test
    fun `loe against a value covers the four null combinations`() {
        val n: NumberExpression<Int>? = age
        assertEquals(age.loe(20), n.loe(20))
        assertNull(n.loe(nilValue))
        assertNull(nil.loe(20))
        assertNull(nil.loe(nilValue))
    }

    @Test
    fun `loe against an expression covers the four null combinations`() {
        val n: NumberExpression<Int>? = age
        val e: Expression<Int>? = other
        assertEquals(age.loe(other), n.loe(e))
        assertNull(n.loe(nilExpr))
        assertNull(nil.loe(e))
        assertNull(nil.loe(nilExpr))
    }
}
