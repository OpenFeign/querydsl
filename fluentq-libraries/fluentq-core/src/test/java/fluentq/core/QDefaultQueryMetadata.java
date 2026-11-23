/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.core;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Expression;
import fluentq.core.types.OrderSpecifier;
import fluentq.core.types.ParamExpression;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.Predicate;
import fluentq.core.types.ValidatingVisitor;
import fluentq.core.types.dsl.BeanPath;
import fluentq.core.types.dsl.BooleanPath;
import fluentq.core.types.dsl.EntityPathBase;
import fluentq.core.types.dsl.ListPath;
import fluentq.core.types.dsl.MapPath;
import fluentq.core.types.dsl.PathInits;
import fluentq.core.types.dsl.SetPath;
import fluentq.core.types.dsl.SimplePath;
import java.io.Serial;

/** QDefaultQueryMetadata is a FluentQ query type for DefaultQueryMetadata */
public class QDefaultQueryMetadata extends EntityPathBase<DefaultQueryMetadata> {

  @Serial private static final long serialVersionUID = 2000363531;

  public static final QDefaultQueryMetadata defaultQueryMetadata =
      new QDefaultQueryMetadata("defaultQueryMetadata");

  public final BooleanPath distinct = createBoolean("distinct");

  public final SetPath<Expression<?>, SimplePath<Expression<?>>> exprInJoins =
      this.<Expression<?>, SimplePath<Expression<?>>>createSet(
          "exprInJoins", Expression.class, SimplePath.class, PathInits.DIRECT);

  public final SetPath<QueryFlag, SimplePath<QueryFlag>> flags =
      this.<QueryFlag, SimplePath<QueryFlag>>createSet(
          "flags", QueryFlag.class, SimplePath.class, PathInits.DIRECT);

  public final ListPath<Expression<?>, SimplePath<Expression<?>>> groupBy =
      this.<Expression<?>, SimplePath<Expression<?>>>createList(
          "groupBy", Expression.class, SimplePath.class, PathInits.DIRECT);

  public final SimplePath<Predicate> having = createSimple("having", Predicate.class);

  public final ListPath<JoinExpression, SimplePath<JoinExpression>> joins =
      this.<JoinExpression, SimplePath<JoinExpression>>createList(
          "joins", JoinExpression.class, SimplePath.class, PathInits.DIRECT);

  public final SimplePath<QueryModifiers> modifiers =
      createSimple("modifiers", QueryModifiers.class);

  public final ListPath<OrderSpecifier<?>, SimplePath<OrderSpecifier<?>>> orderBy =
      this.<OrderSpecifier<?>, SimplePath<OrderSpecifier<?>>>createList(
          "orderBy", OrderSpecifier.class, SimplePath.class, PathInits.DIRECT);

  public final MapPath<ParamExpression<?>, Object, SimplePath<Object>> params =
      this.<ParamExpression<?>, Object, SimplePath<Object>>createMap(
          "params", ParamExpression.class, Object.class, SimplePath.class);

  public final ListPath<Expression<?>, SimplePath<Expression<?>>> projection =
      this.<Expression<?>, SimplePath<Expression<?>>>createList(
          "projection", Expression.class, SimplePath.class, PathInits.DIRECT);

  public final BooleanPath unique = createBoolean("unique");

  public final BooleanPath validate = createBoolean("validate");

  public final SimplePath<ValidatingVisitor> validatingVisitor =
      createSimple("validatingVisitor", ValidatingVisitor.class);

  public final SimplePath<Predicate> where = createSimple("where", Predicate.class);

  public QDefaultQueryMetadata(String variable) {
    super(DefaultQueryMetadata.class, forVariable(variable));
  }

  public QDefaultQueryMetadata(BeanPath<? extends DefaultQueryMetadata> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  public QDefaultQueryMetadata(PathMetadata metadata) {
    super(DefaultQueryMetadata.class, metadata);
  }
}
