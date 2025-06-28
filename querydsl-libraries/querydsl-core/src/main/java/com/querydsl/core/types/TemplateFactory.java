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

import com.google.common.collect.MapMaker;
import com.querydsl.core.types.Template.Element;
import com.querydsl.core.util.CollectionUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * {@code TemplateFactory} is a factory for {@link Template} instances
 *
 * @author tiwe
 */
public class TemplateFactory {

  private static final Map<String, Operator> OPERATORS;

  static {
    var operators = new HashMap<String, Operator>();
    operators.put("+", Ops.ADD);
    operators.put("-", Ops.SUB);
    operators.put("*", Ops.MULT);
    operators.put("/", Ops.DIV);
    OPERATORS = Collections.unmodifiableMap(operators);
  }

  public static final TemplateFactory DEFAULT = new TemplateFactory('\\');

  private static final Constant<String> PERCENT = ConstantImpl.create("%");

  private static final Pattern elementPattern =
      Pattern.compile(
          """
          \\{\
          (%?%?)\
          (\\d+)\
          (?:([+-/*])(?:(\\d+)|'(-?\\d+(?:\\.\\d+)?)'))?\
          ([slu%]?%?)\
          \\}\
          """);

  private final ConcurrentMap<String, Template> cache = new MapMaker().weakKeys().makeMap();

  private final char escape;

  private final Function<Object, Object> toLowerCase =
      new Function<>() {
        @Override
        public Object apply(Object arg) {
          if (arg instanceof Constant<?>) {
            return ConstantImpl.create(apply(arg.toString()).toString());
          } else if (arg instanceof Expression<?> expression) {
            return ExpressionUtils.operation(String.class, Ops.LOWER, expression);
          } else {
            return String.valueOf(arg).toLowerCase();
          }
        }
      };

  private final Function<Object, Object> toUpperCase =
      new Function<>() {
        @Override
        public Object apply(Object arg) {
          if (arg instanceof Constant<?>) {
            return ConstantImpl.create(apply(arg.toString()).toString());
          } else if (arg instanceof Expression<?> expression) {
            return ExpressionUtils.operation(String.class, Ops.UPPER, expression);
          } else {
            return String.valueOf(arg).toUpperCase();
          }
        }
      };

  private final Function<Object, Object> toStartsWithViaLike =
      new Function<>() {
        @Override
        public Object apply(Object arg) {
          if (arg instanceof Constant<?>) {
            return ConstantImpl.create(apply(arg.toString()).toString());
          } else if (arg instanceof Expression<?> expression) {
            return ExpressionUtils.operation(String.class, Ops.CONCAT, expression, PERCENT);
          } else {
            return escapeForLike(String.valueOf(arg)) + "%";
          }
        }
      };

  private final Function<Object, Object> toStartsWithViaLikeLower =
      new Function<>() {
        @Override
        public Object apply(Object arg) {
          if (arg instanceof Constant<?>) {
            return ConstantImpl.create(apply(arg.toString()).toString());
          } else if (arg instanceof Expression<?> expression) {
            Expression<String> concatenated =
                ExpressionUtils.operation(String.class, Ops.CONCAT, expression, PERCENT);
            return ExpressionUtils.operation(String.class, Ops.LOWER, concatenated);
          } else {
            return escapeForLike(String.valueOf(arg).toLowerCase()) + "%";
          }
        }
      };

  private final Function<Object, Object> toEndsWithViaLike =
      new Function<>() {
        @Override
        public Object apply(Object arg) {
          if (arg instanceof Constant) {
            return ConstantImpl.create(apply(arg.toString()).toString());
          } else if (arg instanceof Expression<?> expression) {
            return ExpressionUtils.operation(String.class, Ops.CONCAT, PERCENT, expression);
          } else {
            return "%" + escapeForLike(String.valueOf(arg));
          }
        }
      };

  private final Function<Object, Object> toEndsWithViaLikeLower =
      new Function<>() {
        @Override
        public Object apply(Object arg) {
          if (arg instanceof Constant<?>) {
            return ConstantImpl.create(apply(arg.toString()).toString());
          } else if (arg instanceof Expression<?> expression) {
            Expression<String> concatenated =
                ExpressionUtils.operation(String.class, Ops.CONCAT, PERCENT, expression);
            return ExpressionUtils.operation(String.class, Ops.LOWER, concatenated);
          } else {
            return "%" + escapeForLike(String.valueOf(arg).toLowerCase());
          }
        }
      };

  private final Function<Object, Object> toContainsViaLike =
      new Function<>() {
        @Override
        public Object apply(Object arg) {
          if (arg instanceof Constant<?>) {
            return ConstantImpl.create(apply(arg.toString()).toString());
          } else if (arg instanceof Expression<?> expression) {
            Expression<String> concatenated =
                ExpressionUtils.operation(String.class, Ops.CONCAT, PERCENT, expression);
            return ExpressionUtils.operation(String.class, Ops.CONCAT, concatenated, PERCENT);
          } else {
            return "%" + escapeForLike(String.valueOf(arg)) + "%";
          }
        }
      };

  private final Function<Object, Object> toContainsViaLikeLower =
      new Function<>() {
        @Override
        public Object apply(Object arg) {
          if (arg instanceof Constant<?>) {
            return ConstantImpl.create(apply(arg.toString()).toString());
          } else if (arg instanceof Expression<?> expression) {
            Expression<String> concatenated =
                ExpressionUtils.operation(String.class, Ops.CONCAT, PERCENT, expression);
            concatenated =
                ExpressionUtils.operation(String.class, Ops.CONCAT, concatenated, PERCENT);
            return ExpressionUtils.operation(String.class, Ops.LOWER, concatenated);
          } else {
            return "%" + escapeForLike(String.valueOf(arg).toLowerCase()) + "%";
          }
        }
      };

  public TemplateFactory(char escape) {
    this.escape = escape;
  }

  public Template create(String template) {
    return cache.computeIfAbsent(
        template,
        t -> {
          var m = elementPattern.matcher(t);
          final List<Element> elements = new ArrayList<>();
          var end = 0;
          while (m.find()) {
            if (m.start() > end) {
              elements.add(new Template.StaticText(t.substring(end, m.start())));
            }
            var premodifiers = m.group(1).toLowerCase(Locale.ENGLISH);
            var index = Integer.parseInt(m.group(2));
            var postmodifiers = m.group(6).toLowerCase(Locale.ENGLISH);
            var asString = false;
            Function<Object, Object> transformer = null;
            switch (premodifiers.length()) {
              case 1:
                transformer = toEndsWithViaLike;
                break;
              case 2:
                transformer = toEndsWithViaLikeLower;
                break;
            }
            switch (postmodifiers.length()) {
              case 1:
                switch (postmodifiers.charAt(0)) {
                  case '%':
                    if (transformer == null) {
                      transformer = toStartsWithViaLike;
                    } else {
                      transformer = toContainsViaLike;
                    }
                    break;
                  case 'l':
                    transformer = toLowerCase;
                    break;
                  case 'u':
                    transformer = toUpperCase;
                    break;
                  case 's':
                    asString = true;
                    break;
                }
                break;
              case 2:
                if (transformer == null) {
                  transformer = toStartsWithViaLikeLower;
                } else {
                  transformer = toContainsViaLikeLower;
                }
                break;
            }
            if (m.group(4) != null) {
              var operator = OPERATORS.get(m.group(3));
              var index2 = Integer.parseInt(m.group(4));
              elements.add(new Template.Operation(index, index2, operator, asString));
            } else if (m.group(5) != null) {
              var operator = OPERATORS.get(m.group(3));
              Number number;
              if (m.group(5).contains(".")) {
                number = new BigDecimal(m.group(5));
              } else {
                number = Integer.valueOf(m.group(5));
              }
              elements.add(new Template.OperationConst(index, number, operator, asString));
            } else if (asString) {
              elements.add(new Template.AsString(index));
            } else if (transformer != null) {
              elements.add(new Template.Transformed(index, transformer));
            } else {
              elements.add(new Template.ByIndex(index));
            }
            end = m.end();
          }
          if (end < t.length()) {
            elements.add(new Template.StaticText(t.substring(end)));
          }
          return new Template(t, CollectionUtils.unmodifiableList(elements));
        });
  }

  public String escapeForLike(String str) {
    final var rv = new StringBuilder(str.length() + 3);
    for (var i = 0; i < str.length(); i++) {
      final var ch = str.charAt(i);
      if (ch == escape || ch == '%' || ch == '_') {
        rv.append(escape);
      }
      rv.append(ch);
    }
    return rv.toString();
  }
}
