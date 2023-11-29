/*
 * Copyright 2010, Mysema Ltd
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
package com.querydsl.codegen.utils;

/**
 * Evaluator defines an interface for returning a value as a result of evaluating an expression
 * using the given argument array
 *
 * @author tiwe
 */
public interface Evaluator<T> {

  /**
   * @param args
   * @return
   */
  T evaluate(Object... args);

  /**
   * @return
   */
  Class<? extends T> getType();
}
