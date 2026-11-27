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

import com.querydsl.core.annotations.QueryProjection
import com.querydsl.core.types.ConstructorExpression
import com.querydsl.core.types.ExpressionException
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@JvmInline
value class AccountId(val value: String)

@JvmInline
value class Version(val value: Long)

data class AccountDto @QueryProjection constructor(val id: AccountId, val age: Int)

data class VersionedDto @QueryProjection constructor(val version: Version, val name: String)

class UnannotatedDto(val id: AccountId)

class InternalDto internal constructor(val id: AccountId)

class CompanionOnlyDto private constructor(val id: AccountId) {
    companion object {
        fun of(value: String) = CompanionOnlyDto(AccountId(value))
    }
}

class DefaultArgumentDto(val name: String = "default")

class DefaultArgumentBridgeDto private constructor(val name: String, val count: Int) {
    constructor(name: String = "default") : this(name, 1)
}

class ValueClassProjectionTest {

    private val idExpression = Expressions.constant("a-1")
    private val ageExpression = Expressions.constant(1)

    private fun accountProjection() = Projections.constructor(AccountDto::class.java, idExpression, ageExpression)

    @Test
    fun createsDtoThroughValueClassConstructor() {
        assertEquals(AccountDto(AccountId("a-1"), 2), accountProjection().newInstance("a-1", 2))
    }

    @Test
    fun turnsNullPrimitiveArgumentIntoZero() {
        assertEquals(AccountDto(AccountId("a-1"), 0), accountProjection().newInstance("a-1", null))
    }

    @Test
    fun leavesCallerArgumentsUnchanged() {
        val arguments = arrayOf<Any?>("a-1", null)
        accountProjection().newInstance(*arguments)
        assertContentEquals(arrayOf<Any?>("a-1", null), arguments)
    }

    @Test
    fun createsDtoAfterSerializationRoundTrip() {
        val serialized = ByteArrayOutputStream()
        ObjectOutputStream(serialized).use { it.writeObject(accountProjection()) }
        @Suppress("UNCHECKED_CAST")
        val restoredProjection = ObjectInputStream(ByteArrayInputStream(serialized.toByteArray())).use {
            it.readObject() as ConstructorExpression<AccountDto>
        }
        assertEquals(accountProjection(), restoredProjection)
        assertEquals(AccountDto(AccountId("a-2"), 3), restoredProjection.newInstance("a-2", 3))
    }

    @Test
    fun createsDtoWithPrimitiveBackedValueClass() {
        val projection = Projections.constructor(
            VersionedDto::class.java, Expressions.constant(1L), Expressions.constant("n"))
        assertEquals(VersionedDto(Version(7), "n"), projection.newInstance(7L, "n"))
    }

    @Test
    fun rejectsValueClassConstructorWithoutQueryProjection() {
        assertFailsWith<ExpressionException> { Projections.constructor(UnannotatedDto::class.java, idExpression) }
    }

    @Test
    fun rejectsInternalValueClassConstructor() {
        assertFailsWith<ExpressionException> { Projections.constructor(InternalDto::class.java, idExpression) }
    }

    @Test
    fun rejectsPrivateConstructorCalledFromCompanion() {
        assertFailsWith<ExpressionException> { Projections.constructor(CompanionOnlyDto::class.java, idExpression) }
    }

    @Test
    fun rejectsDefaultArgumentConstructor() {
        assertFailsWith<ExpressionException> {
            Projections.constructor(DefaultArgumentDto::class.java, idExpression, ageExpression)
        }
    }

    @Test
    fun rejectsDefaultArgumentBridgeWithPrivateTwin() {
        assertFailsWith<ExpressionException> {
            Projections.constructor(DefaultArgumentBridgeDto::class.java, idExpression, ageExpression)
        }
    }
}
