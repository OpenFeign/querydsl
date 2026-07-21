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

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.StringExpression

/**
 * Null-safe LIKE that skips the condition when either side is null.
 *
 * @param pattern the LIKE pattern, or null to skip
 * @return this LIKE pattern, or null
 */
infix fun StringExpression?.like(pattern: String?): BooleanExpression? =
    if (this == null || pattern == null) null else this.like(pattern)

/**
 * Null-safe CONTAINS that skips the condition when either side is null.
 *
 * @param substring the substring to match, or null to skip
 * @return this LIKE %substring%, or null
 */
infix fun StringExpression?.contains(substring: String?): BooleanExpression? =
    if (this == null || substring == null) null else this.contains(substring)

/**
 * Null-safe STARTS WITH that skips the condition when either side is null.
 *
 * @param prefix the prefix to match, or null to skip
 * @return this LIKE prefix%, or null
 */
infix fun StringExpression?.startsWith(prefix: String?): BooleanExpression? =
    if (this == null || prefix == null) null else this.startsWith(prefix)

/**
 * Null-safe ENDS WITH that skips the condition when either side is null.
 *
 * @param suffix the suffix to match, or null to skip
 * @return this LIKE %suffix, or null
 */
infix fun StringExpression?.endsWith(suffix: String?): BooleanExpression? =
    if (this == null || suffix == null) null else this.endsWith(suffix)
