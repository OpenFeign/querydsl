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
import com.querydsl.core.types.dsl.ComparableExpression
import com.querydsl.core.types.dsl.Expressions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ComparableExpressionExtensionsTest {

    private val score: ComparableExpression<Int> = Expressions.comparablePath(Int::class.javaObjectType, "score")
    private val other: ComparableExpression<Int> = Expressions.comparablePath(Int::class.javaObjectType, "other")
    private val nil: ComparableExpression<Int>? = null
    private val nilValue: Int? = null
    private val nilExpr: Expression<Int>? = null

    @Test
    fun `gt against a value covers the four null combinations`() {
        val c: ComparableExpression<Int>? = score
        assertEquals(score.gt(5), c.gt(5))
        assertNull(c.gt(nilValue))
        assertNull(nil.gt(5))
        assertNull(nil.gt(nilValue))
    }

    @Test
    fun `gt against an expression covers the four null combinations`() {
        val c: ComparableExpression<Int>? = score
        val e: Expression<Int>? = other
        assertEquals(score.gt(other), c.gt(e))
        assertNull(c.gt(nilExpr))
        assertNull(nil.gt(e))
        assertNull(nil.gt(nilExpr))
    }

    @Test
    fun `goe against a value covers the four null combinations`() {
        val c: ComparableExpression<Int>? = score
        assertEquals(score.goe(5), c.goe(5))
        assertNull(c.goe(nilValue))
        assertNull(nil.goe(5))
        assertNull(nil.goe(nilValue))
    }

    @Test
    fun `goe against an expression covers the four null combinations`() {
        val c: ComparableExpression<Int>? = score
        val e: Expression<Int>? = other
        assertEquals(score.goe(other), c.goe(e))
        assertNull(c.goe(nilExpr))
        assertNull(nil.goe(e))
        assertNull(nil.goe(nilExpr))
    }

    @Test
    fun `lt against a value covers the four null combinations`() {
        val c: ComparableExpression<Int>? = score
        assertEquals(score.lt(5), c.lt(5))
        assertNull(c.lt(nilValue))
        assertNull(nil.lt(5))
        assertNull(nil.lt(nilValue))
    }

    @Test
    fun `lt against an expression covers the four null combinations`() {
        val c: ComparableExpression<Int>? = score
        val e: Expression<Int>? = other
        assertEquals(score.lt(other), c.lt(e))
        assertNull(c.lt(nilExpr))
        assertNull(nil.lt(e))
        assertNull(nil.lt(nilExpr))
    }

    @Test
    fun `loe against a value covers the four null combinations`() {
        val c: ComparableExpression<Int>? = score
        assertEquals(score.loe(5), c.loe(5))
        assertNull(c.loe(nilValue))
        assertNull(nil.loe(5))
        assertNull(nil.loe(nilValue))
    }

    @Test
    fun `loe against an expression covers the four null combinations`() {
        val c: ComparableExpression<Int>? = score
        val e: Expression<Int>? = other
        assertEquals(score.loe(other), c.loe(e))
        assertNull(c.loe(nilExpr))
        assertNull(nil.loe(e))
        assertNull(nil.loe(nilExpr))
    }

    @Test
    fun `between judges each bound independently`() {
        val c: ComparableExpression<Int>? = score
        val lo: Int? = 1
        val hi: Int? = 10
        assertEquals(score.between(1, 10), c.between(lo to hi)) // both -> BETWEEN
        assertEquals(score.goe(1), c.between(lo to null))       // lower-only -> >=
        assertEquals(score.loe(10), c.between(null to hi))      // upper-only -> <=
        assertNull(c.between(null to null))                     // neither -> skip
        assertNull(nil.between(lo to hi))                       // this null
    }

    @Test
    fun `notBetween mirrors between`() {
        val c: ComparableExpression<Int>? = score
        val lo: Int? = 1
        val hi: Int? = 10
        assertEquals(score.notBetween(1, 10), c.notBetween(lo to hi))
        assertEquals(score.lt(1), c.notBetween(lo to null))
        assertEquals(score.gt(10), c.notBetween(null to hi))
        assertNull(c.notBetween(null to null))
        assertNull(nil.notBetween(lo to hi))
    }

    @Test
    fun `between with closed range`() {
        val c: ComparableExpression<Int>? = score
        assertEquals(score.between(20, 60), c.between(20..60))
        assertNull(nil.between(20..60))
    }
}
