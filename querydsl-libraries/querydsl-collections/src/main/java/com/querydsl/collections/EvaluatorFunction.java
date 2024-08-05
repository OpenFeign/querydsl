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
import java.util.function.Function;

/**
 * Function implementation which uses an {@link Evaluator} for transformation
 *
 * @param <S> source type
 * @param <T> target type
 * @author tiwe
 */
public class EvaluatorFunction<S, T> implements Function<S, T> {

  private final Evaluator<T> ev;

  public EvaluatorFunction(Evaluator<T> ev) {
    this.ev = ev;
  }

  @Override
  public T apply(S input) {
    if (input.getClass().isArray()) {
      return ev.evaluate((Object[]) input);
    } else {
      return ev.evaluate(input);
    }
  }
}
