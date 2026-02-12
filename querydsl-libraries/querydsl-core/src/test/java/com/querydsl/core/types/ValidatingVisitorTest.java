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

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Param;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unchecked")
class ValidatingVisitorTest {

  private final Set<Expression<?>> known = new HashSet<>();

  private final ValidatingVisitor validator = ValidatingVisitor.DEFAULT;

  @BeforeEach
  void setUp() {
    known.add(ExpressionUtils.path(Object.class, "path"));
  }

  @Test
  void visitConstantOfQVoid() {
    validator.visit(ConstantImpl.create("XXX"), known);
  }

  @Test
  void visitFactoryExpressionOfQVoid() {
    validator.visit(new QBean(Object.class, ExpressionUtils.path(String.class, "path")), known);
  }

  @Test
  void visitOperationOfQVoid() {
    validator.visit((Operation) Expressions.path(Object.class, "path").isNull(), known);
  }

  @Test
  void visitParamExpressionOfQVoid() {
    validator.visit(new Param(Object.class, "prop"), known);
  }

  @Test
  void visitPathOfQVoid() {
    validator.visit(ExpressionUtils.path(Object.class, "path"), known);
  }

  @Test
  void visitSubQueryExpressionOfQVoid() {
    validator.visit(new SubQueryExpressionImpl(Object.class, new DefaultQueryMetadata()), known);
  }

  @Test
  void visitTemplateExpressionOfQVoid() {
    validator.visit(Expressions.template(Object.class, "XXX"), known);
  }
}
