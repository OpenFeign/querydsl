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
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.SimpleExpression

/**
 * Null-safe equality against a value, skipping the condition when either side is null.
 *
 * @param value the value to compare against, or null to skip
 * @return this = value, or null
 */
infix fun <T> SimpleExpression<T>?.eq(value: T?): BooleanExpression? =
    if (this == null || value == null) null else this.eq(value)

/**
 * Null-safe equality against another expression, skipping the condition when either side is null.
 *
 * @param expr the expression to compare against, or null to skip
 * @return this = expr, or null
 */
infix fun <T> SimpleExpression<T>?.eq(expr: Expression<in T>?): BooleanExpression? =
    if (this == null || expr == null) null else this.eq(expr)

/**
 * Null-safe inequality against a value, skipping the condition when either side is null.
 *
 * @param value the value to compare against, or null to skip
 * @return this != value, or null
 */
infix fun <T> SimpleExpression<T>?.ne(value: T?): BooleanExpression? =
    if (this == null || value == null) null else this.ne(value)

/**
 * Null-safe inequality against another expression, skipping the condition when either side is null.
 *
 * @param expr the expression to compare against, or null to skip
 * @return this != expr, or null
 */
infix fun <T> SimpleExpression<T>?.ne(expr: Expression<in T>?): BooleanExpression? =
    if (this == null || expr == null) null else this.ne(expr)

/**
 * Null-safe IN that skips the condition when this is null or the collection is null or empty.
 *
 * @param values the values to match, or null/empty to skip
 * @return this IN values, or null
 */
infix fun <T> SimpleExpression<T>?.`in`(values: Collection<T>?): BooleanExpression? =
    if (this == null || values.isNullOrEmpty()) null else this.`in`(values)
