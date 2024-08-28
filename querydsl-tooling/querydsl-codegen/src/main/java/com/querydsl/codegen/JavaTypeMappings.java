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
package com.querydsl.codegen;

import com.querydsl.codegen.utils.model.TypeCategory;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.ComparableTemplate;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.DateTimeTemplate;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.EnumTemplate;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.SimpleTemplate;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.core.types.dsl.TemporalExpression;
import com.querydsl.core.types.dsl.TimePath;
import com.querydsl.core.types.dsl.TimeTemplate;

/**
 * {@code JavaTypeMappings} defines mappings from {@link TypeCategory} instances to {@link
 * Expression} types
 *
 * @author tiwe
 */
public class JavaTypeMappings extends TypeMappings {

  public JavaTypeMappings() {
    register(TypeCategory.STRING, StringExpression.class, StringPath.class, StringTemplate.class);
    register(
        TypeCategory.BOOLEAN, BooleanExpression.class, BooleanPath.class, BooleanTemplate.class);
    register(
        TypeCategory.COMPARABLE,
        ComparableExpression.class,
        ComparablePath.class,
        ComparableTemplate.class);
    register(TypeCategory.ENUM, EnumExpression.class, EnumPath.class, EnumTemplate.class);
    register(TypeCategory.DATE, TemporalExpression.class, DatePath.class, DateTemplate.class);
    register(
        TypeCategory.DATETIME,
        TemporalExpression.class,
        DateTimePath.class,
        DateTimeTemplate.class);
    register(TypeCategory.TIME, TemporalExpression.class, TimePath.class, TimeTemplate.class);
    register(TypeCategory.NUMERIC, NumberExpression.class, NumberPath.class, NumberTemplate.class);
    register(TypeCategory.SIMPLE, SimpleExpression.class, SimplePath.class, SimpleTemplate.class);

    register(TypeCategory.ARRAY, Expression.class, SimplePath.class, SimpleTemplate.class);
    register(TypeCategory.COLLECTION, Expression.class, SimplePath.class, SimpleTemplate.class);
    register(TypeCategory.SET, Expression.class, SimplePath.class, SimpleTemplate.class);
    register(TypeCategory.LIST, Expression.class, SimplePath.class, SimpleTemplate.class);
    register(TypeCategory.MAP, Expression.class, SimplePath.class, SimpleTemplate.class);

    register(TypeCategory.CUSTOM, Expression.class, Path.class, SimpleTemplate.class);
    register(TypeCategory.ENTITY, Expression.class, Path.class, SimpleTemplate.class);
  }
}
