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
package com.querydsl.sql;

import com.querydsl.core.Tuple;
import com.querydsl.core.annotations.Immutable;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.CollectionUtils;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * {@code PrimaryKey} defines a primary key on table
 *
 * @param <E> expression type
 * @author tiwe
 */
@Immutable
public final class PrimaryKey<E> implements Serializable, ProjectionRole<Tuple> {

  private static final long serialVersionUID = -6913344535043394649L;

  private final RelationalPath<?> entity;

  private final List<? extends Path<?>> localColumns;

  @Nullable private transient volatile Expression<Tuple> mixin;

  public PrimaryKey(RelationalPath<?> entity, Path<?>... localColumns) {
    this(entity, Arrays.asList(localColumns));
  }

  public PrimaryKey(RelationalPath<?> entity, List<? extends Path<?>> localColumns) {
    this.entity = entity;
    this.localColumns = CollectionUtils.unmodifiableList(localColumns);
    this.mixin = ExpressionUtils.list(Tuple.class, localColumns);
  }

  public RelationalPath<?> getEntity() {
    return entity;
  }

  public List<? extends Path<?>> getLocalColumns() {
    return localColumns;
  }

  public BooleanExpression in(CollectionExpression<?, Tuple> coll) {
    return Expressions.booleanOperation(Ops.IN, getProjection(), coll);
  }

  @Override
  public Expression<Tuple> getProjection() {
    if (mixin == null) {
      mixin = ExpressionUtils.list(Tuple.class, localColumns);
    }
    return mixin;
  }
}
