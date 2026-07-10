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
import com.querydsl.core.types.dsl.NumberExpression

// NumberExpression does not extend ComparableExpression, so these comparison helpers are declared
// separately from the ComparableExpression extensions.

/**
 * Null-safe greater-than against a value, skipping when either side is null.
 *
 * @param value the value to compare against, or null to skip
 * @return this > value, or null
 */
infix fun <T> NumberExpression<T>?.gt(value: T?): BooleanExpression? where T : Number, T : Comparable<T> =
    if (this == null || value == null) null else this.gt(value)

/**
 * Null-safe greater-than against another expression, skipping when either side is null.
 *
 * @param expr the expression to compare against, or null to skip
 * @return this > expr, or null
 */
infix fun <T> NumberExpression<T>?.gt(expr: Expression<T>?): BooleanExpression? where T : Number, T : Comparable<T> =
    if (this == null || expr == null) null else this.gt(expr)

/**
 * Null-safe greater-than-or-equal against a value, skipping when either side is null.
 *
 * @param value the value to compare against, or null to skip
 * @return this >= value, or null
 */
infix fun <T> NumberExpression<T>?.goe(value: T?): BooleanExpression? where T : Number, T : Comparable<T> =
    if (this == null || value == null) null else this.goe(value)

/**
 * Null-safe greater-than-or-equal against another expression, skipping when either side is null.
 *
 * @param expr the expression to compare against, or null to skip
 * @return this >= expr, or null
 */
infix fun <T> NumberExpression<T>?.goe(expr: Expression<T>?): BooleanExpression? where T : Number, T : Comparable<T> =
    if (this == null || expr == null) null else this.goe(expr)

/**
 * Null-safe less-than against a value, skipping when either side is null.
 *
 * @param value the value to compare against, or null to skip
 * @return this < value, or null
 */
infix fun <T> NumberExpression<T>?.lt(value: T?): BooleanExpression? where T : Number, T : Comparable<T> =
    if (this == null || value == null) null else this.lt(value)

/**
 * Null-safe less-than against another expression, skipping when either side is null.
 *
 * @param expr the expression to compare against, or null to skip
 * @return this < expr, or null
 */
infix fun <T> NumberExpression<T>?.lt(expr: Expression<T>?): BooleanExpression? where T : Number, T : Comparable<T> =
    if (this == null || expr == null) null else this.lt(expr)

/**
 * Null-safe less-than-or-equal against a value, skipping when either side is null.
 *
 * @param value the value to compare against, or null to skip
 * @return this <= value, or null
 */
infix fun <T> NumberExpression<T>?.loe(value: T?): BooleanExpression? where T : Number, T : Comparable<T> =
    if (this == null || value == null) null else this.loe(value)

/**
 * Null-safe less-than-or-equal against another expression, skipping when either side is null.
 *
 * @param expr the expression to compare against, or null to skip
 * @return this <= expr, or null
 */
infix fun <T> NumberExpression<T>?.loe(expr: Expression<T>?): BooleanExpression? where T : Number, T : Comparable<T> =
    if (this == null || expr == null) null else this.loe(expr)
