/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.codegen;

import fluentq.codegen.utils.model.TypeCategory;
import fluentq.core.types.Expression;
import fluentq.core.types.Path;
import fluentq.core.types.dsl.BooleanExpression;
import fluentq.core.types.dsl.BooleanPath;
import fluentq.core.types.dsl.BooleanTemplate;
import fluentq.core.types.dsl.ComparableExpression;
import fluentq.core.types.dsl.ComparablePath;
import fluentq.core.types.dsl.ComparableTemplate;
import fluentq.core.types.dsl.DatePath;
import fluentq.core.types.dsl.DateTemplate;
import fluentq.core.types.dsl.DateTimePath;
import fluentq.core.types.dsl.DateTimeTemplate;
import fluentq.core.types.dsl.EnumExpression;
import fluentq.core.types.dsl.EnumPath;
import fluentq.core.types.dsl.EnumTemplate;
import fluentq.core.types.dsl.NumberExpression;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.NumberTemplate;
import fluentq.core.types.dsl.SimpleExpression;
import fluentq.core.types.dsl.SimplePath;
import fluentq.core.types.dsl.SimpleTemplate;
import fluentq.core.types.dsl.StringExpression;
import fluentq.core.types.dsl.StringPath;
import fluentq.core.types.dsl.StringTemplate;
import fluentq.core.types.dsl.TemporalExpression;
import fluentq.core.types.dsl.TimePath;
import fluentq.core.types.dsl.TimeTemplate;

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
