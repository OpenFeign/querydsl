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
import com.querydsl.core.util.CollectionUtils;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.Unmodifiable;

/**
 * Default implementation of the {@link TemplateExpression} interface
 *
 * @author tiwe
 * @param <T> expression type
 */
@Immutable
public class TemplateExpressionImpl<T> extends ExpressionBase<T> implements TemplateExpression<T> {

  private static final long serialVersionUID = 6951623726800809083L;

  @Unmodifiable private final List<?> args;

  private final Template template;

  protected TemplateExpressionImpl(Class<? extends T> type, Template template, Object... args) {
    this(type, template, Arrays.asList(args));
  }

  protected TemplateExpressionImpl(Class<? extends T> type, Template template, List<?> args) {
    super(type);
    this.args = CollectionUtils.unmodifiableList(args);
    this.template = template;
  }

  @Override
  public final Object getArg(int index) {
    return getArgs().get(index);
  }

  @Override
  @Unmodifiable
  public final List<?> getArgs() {
    return args;
  }

  @Override
  public final Template getTemplate() {
    return template;
  }

  @Override
  public final boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (o instanceof TemplateExpression) {
      TemplateExpression<?> c = (TemplateExpression<?>) o;
      return c.getTemplate().equals(template)
          && c.getType().equals(getType())
          && c.getArgs().equals(args);

    } else {
      return false;
    }
  }

  @Override
  public final <R, C> R accept(Visitor<R, C> v, C context) {
    return v.visit(this, context);
  }
}
