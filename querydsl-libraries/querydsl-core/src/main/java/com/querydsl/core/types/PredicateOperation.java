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
package com.querydsl.core.types;

import com.querydsl.core.annotations.Immutable;
import java.io.Serial;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * {@code PredicateOperation} provides a Boolean typed {@link Operation} implementation
 *
 * @author tiwe
 */
@Immutable
public final class PredicateOperation extends OperationImpl<Boolean> implements Predicate {

  @Serial private static final long serialVersionUID = -5371430939203772072L;

  @Nullable private transient volatile Predicate not;

  protected PredicateOperation(Operator operator, List<Expression<?>> args) {
    super(Boolean.class, operator, args);
  }

  @Override
  public Predicate not() {
    if (not == null) {
      not = ExpressionUtils.predicate(Ops.NOT, this);
    }
    return not;
  }
}
