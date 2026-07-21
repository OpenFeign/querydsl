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
import com.querydsl.core.types.Ops
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExpressionExtensionsTest {

    private val a: BooleanExpression = Expressions.booleanPath("a")
    private val b: BooleanExpression = Expressions.booleanPath("b")

    private fun nil(): Expression<Boolean>? = null

    @Test
    fun `and judges each side independently`() {
        val an: Expression<Boolean>? = a
        val bn: Expression<Boolean>? = b
        assertEquals(Expressions.booleanOperation(Ops.AND, a, b), an and bn) // both -> AND
        assertEquals(Expressions.asBoolean(a), an and null)                   // this only -> this
        assertEquals(Expressions.asBoolean(b), nil() and bn)                  // arg only  -> arg
        assertNull(nil() and null)                                            // neither   -> skip
    }

    @Test
    fun `or judges each side independently`() {
        val an: Expression<Boolean>? = a
        val bn: Expression<Boolean>? = b
        assertEquals(Expressions.booleanOperation(Ops.OR, a, b), an or bn)
        assertEquals(Expressions.asBoolean(a), an or null)
        assertEquals(Expressions.asBoolean(b), nil() or bn)
        assertNull(nil() or null)
    }

    @Test
    fun `xor skips when either side is null`() {
        val an: Expression<Boolean>? = a
        val bn: Expression<Boolean>? = b
        assertEquals(Expressions.booleanOperation(Ops.XOR, a, b), an xor bn)
        assertNull(an xor null)
        assertNull(nil() xor bn)
        assertNull(nil() xor null)
    }

    @Test
    fun `not skips when null`() {
        val an: Expression<Boolean>? = a
        assertEquals(Expressions.booleanOperation(Ops.NOT, a), !an)
        assertNull(!nil())
    }

    @Test
    fun `non-null BooleanExpression and resolves to the member method`() {
        val result: BooleanExpression = a.and(b) // must compile as non-null (member wins)
        assertEquals(a.and(b), result)
    }
}
