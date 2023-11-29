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
package com.querydsl.collections;

import com.querydsl.codegen.utils.Evaluator;
import com.querydsl.core.EmptyMetadata;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathExtractor;
import com.querydsl.core.types.Predicate;
import java.util.Collections;
import java.util.function.Function;

/**
 * {@code GuavaHelpers} provides functionality to wrap Querydsl {@link Predicate} instances to Guava
 * predicates and Querydsl {@link Expression} instances to Guava functions
 *
 * @author tiwe
 */
public final class FunctionalHelpers {

  private static final DefaultEvaluatorFactory evaluatorFactory =
      new DefaultEvaluatorFactory(CollQueryTemplates.DEFAULT);

  /**
   * Wrap a Querydsl predicate into a Guava predicate
   *
   * @param predicate predicate to wrapped
   * @return Guava predicate
   */
  public static <T> java.util.function.Predicate<T> wrap(Predicate predicate) {
    Path<?> path = predicate.accept(PathExtractor.DEFAULT, null);
    if (path != null) {
      final Evaluator<Boolean> ev = createEvaluator(path.getRoot(), predicate);
      return ev::evaluate;
    } else {
      throw new IllegalArgumentException("No path in " + predicate);
    }
  }

  /**
   * Wrap a Querydsl expression into a Guava function
   *
   * @param projection projection to wrap
   * @return Guava function
   */
  public static <F, T> Function<F, T> wrap(Expression<T> projection) {
    Path<?> path = projection.accept(PathExtractor.DEFAULT, null);
    if (path != null) {
      final Evaluator<T> ev = createEvaluator(path.getRoot(), projection);
      return ev::evaluate;
    } else {
      throw new IllegalArgumentException("No path in " + projection);
    }
  }

  private static <F, T> Evaluator<T> createEvaluator(Path<F> path, Expression<T> projection) {
    return evaluatorFactory.create(
        EmptyMetadata.DEFAULT, Collections.singletonList(path), projection);
  }

  private FunctionalHelpers() {}
}
