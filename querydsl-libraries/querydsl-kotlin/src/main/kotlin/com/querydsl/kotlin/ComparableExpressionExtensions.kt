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
import com.querydsl.core.types.dsl.ComparableExpression

/**
 * Null-safe greater-than against a value, skipping when either side is null.
 *
 * @param value the value to compare against, or null to skip
 * @return this > value, or null
 */
infix fun <T : Comparable<T>> ComparableExpression<T>?.gt(value: T?): BooleanExpression? =
    if (this == null || value == null) null else this.gt(value)

/**
 * Null-safe greater-than against another expression, skipping when either side is null.
 *
 * @param expr the expression to compare against, or null to skip
 * @return this > expr, or null
 */
infix fun <T : Comparable<T>> ComparableExpression<T>?.gt(expr: Expression<T>?): BooleanExpression? =
    if (this == null || expr == null) null else this.gt(expr)

/**
 * Null-safe greater-than-or-equal against a value, skipping when either side is null.
 *
 * @param value the value to compare against, or null to skip
 * @return this >= value, or null
 */
infix fun <T : Comparable<T>> ComparableExpression<T>?.goe(value: T?): BooleanExpression? =
    if (this == null || value == null) null else this.goe(value)

/**
 * Null-safe greater-than-or-equal against another expression, skipping when either side is null.
 *
 * @param expr the expression to compare against, or null to skip
 * @return this >= expr, or null
 */
infix fun <T : Comparable<T>> ComparableExpression<T>?.goe(expr: Expression<T>?): BooleanExpression? =
    if (this == null || expr == null) null else this.goe(expr)

/**
 * Null-safe less-than against a value, skipping when either side is null.
 *
 * @param value the value to compare against, or null to skip
 * @return this < value, or null
 */
infix fun <T : Comparable<T>> ComparableExpression<T>?.lt(value: T?): BooleanExpression? =
    if (this == null || value == null) null else this.lt(value)

/**
 * Null-safe less-than against another expression, skipping when either side is null.
 *
 * @param expr the expression to compare against, or null to skip
 * @return this < expr, or null
 */
infix fun <T : Comparable<T>> ComparableExpression<T>?.lt(expr: Expression<T>?): BooleanExpression? =
    if (this == null || expr == null) null else this.lt(expr)

/**
 * Null-safe less-than-or-equal against a value, skipping when either side is null.
 *
 * @param value the value to compare against, or null to skip
 * @return this <= value, or null
 */
infix fun <T : Comparable<T>> ComparableExpression<T>?.loe(value: T?): BooleanExpression? =
    if (this == null || value == null) null else this.loe(value)

/**
 * Null-safe less-than-or-equal against another expression, skipping when either side is null.
 *
 * @param expr the expression to compare against, or null to skip
 * @return this <= expr, or null
 */
infix fun <T : Comparable<T>> ComparableExpression<T>?.loe(expr: Expression<T>?): BooleanExpression? =
    if (this == null || expr == null) null else this.loe(expr)

/**
 * Null-safe BETWEEN with partial-range support, judging each bound independently.
 *
 * Both bounds present yields BETWEEN, lower-only yields `>=`, upper-only yields `<=`,
 * and neither is skipped.
 *
 * @param range a `from to to` pair where either bound may be null
 * @return this BETWEEN from AND to (or a one-sided `>=` / `<=`), or null
 */
infix fun <T : Comparable<T>> ComparableExpression<T>?.between(range: Pair<T?, T?>): BooleanExpression? {
    if (this == null) return null
    val (from, to) = range
    return when {
        from != null && to != null -> this.between(from, to)
        from != null -> this.goe(from)
        to != null -> this.loe(to)
        else -> null
    }
}

/**
 * Null-safe BETWEEN over a Kotlin [ClosedRange], where both bounds are always present.
 *
 * @param range the closed range (`from..to`)
 * @return this BETWEEN from AND to, or null
 */
infix fun <T : Comparable<T>> ComparableExpression<T>?.between(range: ClosedRange<T>): BooleanExpression? =
    this?.between(range.start, range.endInclusive)

/**
 * Null-safe NOT BETWEEN, mirroring [between] with partial-range support.
 *
 * Both bounds present yields NOT BETWEEN, lower-only yields `<`, upper-only yields `>`,
 * and neither is skipped.
 *
 * @param range a `from to to` pair where either bound may be null
 * @return this NOT BETWEEN from AND to (or a one-sided `<` / `>`), or null
 */
infix fun <T : Comparable<T>> ComparableExpression<T>?.notBetween(range: Pair<T?, T?>): BooleanExpression? {
    if (this == null) return null
    val (from, to) = range
    return when {
        from != null && to != null -> this.notBetween(from, to)
        from != null -> this.lt(from)
        to != null -> this.gt(to)
        else -> null
    }
}
