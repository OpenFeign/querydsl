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

import com.querydsl.core.types.dsl.Expressions
import com.querydsl.core.types.dsl.StringExpression
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StringExpressionExtensionsTest {

    private val name: StringExpression = Expressions.stringPath("name")
    private val nil: StringExpression? = null

    @Test
    fun `like covers the four null combinations`() {
        val s: StringExpression? = name
        assertEquals(name.like("%a%"), s.like("%a%"))
        assertNull(s.like(null))
        assertNull(nil.like("%a%"))
        assertNull(nil.like(null))
    }

    @Test
    fun `contains covers the four null combinations`() {
        val s: StringExpression? = name
        assertEquals(name.contains("a"), s.contains("a"))
        assertNull(s.contains(null))
        assertNull(nil.contains("a"))
        assertNull(nil.contains(null))
    }

    @Test
    fun `startsWith covers the four null combinations`() {
        val s: StringExpression? = name
        assertEquals(name.startsWith("a"), s.startsWith("a"))
        assertNull(s.startsWith(null))
        assertNull(nil.startsWith("a"))
        assertNull(nil.startsWith(null))
    }

    @Test
    fun `endsWith covers the four null combinations`() {
        val s: StringExpression? = name
        assertEquals(name.endsWith("z"), s.endsWith("z"))
        assertNull(s.endsWith(null))
        assertNull(nil.endsWith("z"))
        assertNull(nil.endsWith(null))
    }
}
