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
import com.querydsl.core.types.dsl.SimpleExpression
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SimpleExpressionExtensionsTest {

    private val name: SimpleExpression<String> = Expressions.stringPath("name")
    private val other: SimpleExpression<String> = Expressions.stringPath("other")
    private val nil: SimpleExpression<String>? = null
    private val nilValue: String? = null
    private val nilExpr: Expression<String>? = null

    @Test
    fun `eq against a value covers the four null combinations`() {
        val s: SimpleExpression<String>? = name
        assertEquals(name.eq("a"), s.eq("a"))
        assertNull(s.eq(nilValue))
        assertNull(nil.eq("a"))
        assertNull(nil.eq(nilValue))
    }

    @Test
    fun `eq against an expression covers the four null combinations`() {
        val s: SimpleExpression<String>? = name
        val e: Expression<String>? = other
        assertEquals(name.eq(other), s.eq(e))
        assertNull(s.eq(nilExpr))
        assertNull(nil.eq(e))
        assertNull(nil.eq(nilExpr))
    }

    @Test
    fun `ne against a value covers the four null combinations`() {
        val s: SimpleExpression<String>? = name
        assertEquals(name.ne("a"), s.ne("a"))
        assertNull(s.ne(nilValue))
        assertNull(nil.ne("a"))
        assertNull(nil.ne(nilValue))
    }

    @Test
    fun `ne against an expression covers the four null combinations`() {
        val s: SimpleExpression<String>? = name
        val e: Expression<String>? = other
        assertEquals(name.ne(other), s.ne(e))
        assertNull(s.ne(nilExpr))
        assertNull(nil.ne(e))
        assertNull(nil.ne(nilExpr))
    }

    @Test
    fun `in covers the four null combinations plus empty`() {
        val s: SimpleExpression<String>? = name
        assertEquals(name.`in`(listOf("a", "b")), s.`in`(listOf("a", "b")))
        assertNull(s.`in`(null))
        assertNull(s.`in`(emptyList()))
        assertNull(nil.`in`(listOf("a")))
        assertNull(nil.`in`(null))
    }
}
