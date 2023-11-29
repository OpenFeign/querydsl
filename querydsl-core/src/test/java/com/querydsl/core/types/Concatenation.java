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

import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.Unmodifiable;

/**
 * @author tiwe
 */
public class Concatenation extends FactoryExpressionBase<String> {

  private static final long serialVersionUID = -355693583588722395L;

  @Unmodifiable private final List<Expression<?>> args;

  public Concatenation(Expression<?>... args) {
    super(String.class);
    this.args = Arrays.asList(args);
  }

  @Override
  @Unmodifiable
  public List<Expression<?>> getArgs() {
    return args;
  }

  @Override
  public String newInstance(Object... args) {
    StringBuilder builder = new StringBuilder();
    for (Object a : args) {
      builder.append(a);
    }
    return builder.toString();
  }

  @Override
  public <R, C> R accept(Visitor<R, C> v, C context) {
    return v.visit(this, context);
  }
}
