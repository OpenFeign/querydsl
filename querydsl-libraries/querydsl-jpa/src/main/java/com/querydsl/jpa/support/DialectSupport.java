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
package com.querydsl.jpa.support;

import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Template;
import com.querydsl.jpa.hibernate.HibernateUtil;
import com.querydsl.sql.SQLTemplates;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.type.BasicTypeReference;

final class DialectSupport {

  private DialectSupport() {}

  public static Map<String, DialectFunctionTemplate> createPatterns(SQLTemplates templates) {
    Map<String, DialectFunctionTemplate> functions = new HashMap<>();
    functions.put("second", createFunction(templates, Ops.DateTimeOps.SECOND));
    functions.put("minute", createFunction(templates, Ops.DateTimeOps.MINUTE));
    functions.put("hour", createFunction(templates, Ops.DateTimeOps.HOUR));
    functions.put("day", createFunction(templates, Ops.DateTimeOps.DAY_OF_MONTH));
    functions.put("week", createFunction(templates, Ops.DateTimeOps.WEEK));
    functions.put("month", createFunction(templates, Ops.DateTimeOps.MONTH));
    functions.put("year", createFunction(templates, Ops.DateTimeOps.YEAR));
    return functions;
  }

  public static DialectFunctionTemplate createFunction(SQLTemplates templates, Operator operator) {
    Template template = templates.getTemplate(operator);
    BasicTypeReference<?> type = HibernateUtil.getType(operator.getType());
    return new DialectFunctionTemplate(convert(template), type);
  }

  public static String convert(Template template) {
    var builder = new StringBuilder();
    for (Template.Element element : template.getElements()) {
      if (element instanceof Template.AsString) {
        builder.append("?").append(((Template.AsString) element).getIndex() + 1);
      } else if (element instanceof Template.ByIndex) {
        builder.append("?").append(((Template.ByIndex) element).getIndex() + 1);
      } else if (element instanceof Template.Transformed) {
        builder.append("?").append(((Template.Transformed) element).getIndex() + 1);
      } else if (element instanceof Template.StaticText) {
        builder.append(((Template.StaticText) element).getText());
      } else {
        throw new IllegalStateException("Unsupported element " + element);
      }
    }
    return builder.toString();
  }

  public static void extendRegistry(
      SQLTemplates templates, FunctionContributions functionContributions) {
    var functionRegistry = functionContributions.getFunctionRegistry();
    var functions = DialectSupport.createPatterns(templates);

    var basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();
    functions.forEach(
        (name, template) ->
            functionRegistry.registerPattern(
                name, template.pattern(), basicTypeRegistry.resolve(template.type())));
  }

  public static void extendRegistry(
      String name,
      DialectSupport.DialectFunctionTemplate template,
      FunctionContributions functionContributions) {
    var functionRegistry = functionContributions.getFunctionRegistry();

    var basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();
    functionRegistry.registerPattern(
        name, template.pattern(), basicTypeRegistry.resolve(template.type()));
  }

  static class DialectFunctionTemplate {
    private final String pattern;
    private final BasicTypeReference<?> type;

    DialectFunctionTemplate(String pattern, BasicTypeReference<?> type) {
      this.pattern = pattern;
      this.type = type;
    }

    String pattern() {
      return pattern;
    }

    BasicTypeReference<?> type() {
      return type;
    }
  }
}
