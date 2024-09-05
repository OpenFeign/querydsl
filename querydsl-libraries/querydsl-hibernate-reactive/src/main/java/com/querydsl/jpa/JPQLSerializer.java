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
package com.querydsl.jpa;

import com.querydsl.core.JoinExpression;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.support.SerializerBase;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathType;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.MathUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

/**
 * {@code JPQLSerializer} serializes Querydsl expressions into JPQL syntax.
 *
 * @author tiwe
 */
public class JPQLSerializer extends SerializerBase<JPQLSerializer> {

  private static final Set<? extends Operator> NUMERIC =
      Collections.unmodifiableSet(
          EnumSet.of(
              Ops.ADD, Ops.SUB, Ops.MULT, Ops.DIV, Ops.LT, Ops.LOE, Ops.GT, Ops.GOE, Ops.BETWEEN));

  private static final Set<? extends Operator> CASE_OPS =
      Collections.unmodifiableSet(EnumSet.of(Ops.CASE_ELSE));

  private static final String COMMA = ", ";

  private static final String DELETE = "delete from ";

  private static final String FROM = "from ";

  private static final String GROUP_BY = "\ngroup by ";

  private static final String HAVING = "\nhaving ";

  private static final String ORDER_BY = "\norder by ";

  private static final String SELECT = "select ";

  private static final String SELECT_COUNT = "select count(";

  private static final String SELECT_COUNT_DISTINCT = "select count(distinct ";

  private static final String SELECT_DISTINCT = "select distinct ";

  private static final String SET = "\nset ";

  private static final String UPDATE = "update ";

  private static final String INSERT = "insert into ";

  private static final String VALUES = "\nvalues ";

  private static final String WHERE = "\nwhere ";

  private static final String WITH = " with ";

  private static final String ON = " on ";

  private static final Map<JoinType, String> joinTypes = new EnumMap<>(JoinType.class);

  private final JPQLTemplates templates;

  private final EntityManager entityManager;

  private boolean inProjection = false;

  private boolean inCaseOperation = false;

  static {
    joinTypes.put(JoinType.DEFAULT, COMMA);
    joinTypes.put(JoinType.FULLJOIN, "\n  full join ");
    joinTypes.put(JoinType.INNERJOIN, "\n  inner join ");
    joinTypes.put(JoinType.JOIN, "\n  inner join ");
    joinTypes.put(JoinType.LEFTJOIN, "\n  left join ");
    joinTypes.put(JoinType.RIGHTJOIN, "\n  right join ");
  }

  private boolean wrapElements = false;

  public JPQLSerializer(JPQLTemplates templates) {
    this(templates, null);
  }

  public JPQLSerializer(JPQLTemplates templates, EntityManager em) {
    super(templates);
    this.templates = templates;
    this.entityManager = em;
  }

  private String getEntityName(Class<?> clazz) {
    final var entityAnnotation = clazz.getAnnotation(Entity.class);
    if (entityAnnotation != null && entityAnnotation.name().length() > 0) {
      return entityAnnotation.name();
    } else if (clazz.getPackage() != null && clazz.getPackage().getName().length() > 0) {
      var pn = clazz.getPackage().getName();
      return clazz.getName().substring(pn.length() + 1);
    } else {
      return clazz.getName();
    }
  }

  private void handleJoinTarget(JoinExpression je) {
    // type specifier
    if (je.getTarget() instanceof EntityPath<?>) {
      final EntityPath<?> pe = (EntityPath<?>) je.getTarget();
      if (pe.getMetadata().isRoot()) {
        append(getEntityName(pe.getType()));
        append(" ");
      }
      handle(je.getTarget());
    } else if (je.getTarget() instanceof Operation) {
      Operation<?> op = (Operation) je.getTarget();
      if (op.getOperator() == Ops.ALIAS) {
        var treat = false;
        if (Collection.class.isAssignableFrom(op.getArg(0).getType())) {
          if (op.getArg(0) instanceof CollectionExpression) {
            Class<?> par = ((CollectionExpression) op.getArg(0)).getParameter(0);
            treat = !par.equals(op.getArg(1).getType());
          }
        } else if (Map.class.isAssignableFrom(op.getArg(0).getType())) {
          if (op.getArg(0) instanceof MapExpression) {
            Class<?> par = ((MapExpression) op.getArg(0)).getParameter(1);
            treat = !par.equals(op.getArg(1).getType());
          }
        } else {
          treat = !op.getArg(0).getType().equals(op.getArg(1).getType());
        }
        if (treat) {
          Expression<?> entityName = ConstantImpl.create(getEntityName(op.getArg(1).getType()));
          Expression<?> t =
              ExpressionUtils.operation(op.getType(), JPQLOps.TREAT, op.getArg(0), entityName);
          op = ExpressionUtils.operation(op.getType(), Ops.ALIAS, t, op.getArg(1));
        }
      }
      handle(op);
    } else {
      handle(je.getTarget());
    }
  }

  public void serialize(QueryMetadata metadata, boolean forCountRow, @Nullable String projection) {
    final Expression<?> select = metadata.getProjection();
    final List<JoinExpression> joins = metadata.getJoins();
    final Predicate where = metadata.getWhere();
    final List<? extends Expression<?>> groupBy = metadata.getGroupBy();
    final Predicate having = metadata.getHaving();
    final List<OrderSpecifier<?>> orderBy = metadata.getOrderBy();

    // select
    var inProjectionOrig = inProjection;
    inProjection = true;
    if (projection != null) {
      append(SELECT).append(projection).append("\n");

    } else if (forCountRow) {
      if (!groupBy.isEmpty()) {
        append(SELECT_COUNT_DISTINCT);
        handle(", ", groupBy);
      } else {
        if (!metadata.isDistinct()) {
          append(SELECT_COUNT);
        } else {
          append(SELECT_COUNT_DISTINCT);
        }
        if (select != null) {
          if (select instanceof FactoryExpression) {
            handle(joins.get(0).getTarget());
          } else {
            // TODO : make sure this works
            handle(select);
          }
        } else {
          handle(joins.get(0).getTarget());
        }
      }
      append(")\n");

    } else if (select != null || !joins.isEmpty()) {
      if (!metadata.isDistinct()) {
        append(SELECT);
      } else {
        append(SELECT_DISTINCT);
      }
      if (select != null) {
        handle(select);
      } else {
        handle(joins.get(0).getTarget());
      }
      append("\n");
    }
    inProjection = inProjectionOrig;

    // from
    if (!joins.isEmpty()) {
      append(FROM);
      serializeSources(forCountRow, joins);
    }

    // where
    if (where != null) {
      append(WHERE).handle(where);
    }

    // group by
    if (!groupBy.isEmpty() && !forCountRow) {
      append(GROUP_BY).handle(COMMA, groupBy);
    }

    // having
    if (having != null) {
      append(HAVING).handle(having);
    }

    // order by
    if (!orderBy.isEmpty() && !forCountRow) {
      append(ORDER_BY);
      var first = true;
      for (final OrderSpecifier<?> os : orderBy) {
        if (!first) {
          append(COMMA);
        }
        handle(os.getTarget());
        append(os.getOrder() == Order.ASC ? " asc" : " desc");
        if (os.getNullHandling() == OrderSpecifier.NullHandling.NullsFirst) {
          append(" nulls first");
        } else if (os.getNullHandling() == OrderSpecifier.NullHandling.NullsLast) {
          append(" nulls last");
        }
        first = false;
      }
    }
  }

  public void serializeForDelete(QueryMetadata md) {
    append(DELETE);
    handleJoinTarget(md.getJoins().get(0));
    if (md.getWhere() != null) {
      append(WHERE).handle(md.getWhere());
    }
  }

  private static String relativePathString(Expression<?> root, Path<?> path) {
    var pathString = new StringBuilder(path.getMetadata().getName().length());
    while (path.getMetadata().getParent() != null && !path.equals(root)) {
      if (pathString.length() > 0) {
        pathString.insert(0, '.');
      }
      pathString.insert(0, path.getMetadata().getName());
      path = path.getMetadata().getParent();
    }
    return pathString.toString();
  }

  public void serializeForInsert(
      QueryMetadata md,
      Collection<Path<?>> columns,
      List<Object> values,
      SubQueryExpression<?> query,
      Map<Path<?>, Expression<?>> inserts) {
    append(INSERT);
    final var root = md.getJoins().get(0);
    append(getEntityName(root.getTarget().getType()));
    append(" (");
    var first = true;
    for (Path<?> path : columns) {
      if (!first) {
        append(", ");
      }

      append(relativePathString(root.getTarget(), path));
      first = false;
    }
    append(")\n");

    if (values != null && values.size() > 0) {
      append(VALUES);
      append(" (");
      first = true;
      for (Object value : values) {
        if (!first) {
          append(", ");
        }
        handle(value);
        first = false;
      }
      append(")");
    } else if (inserts != null && inserts.entrySet().size() > 0) {
      first = true;
      for (Map.Entry<Path<?>, Expression<?>> entry : inserts.entrySet()) {
        if (!first) {
          append(", ");
        }
        handle(entry.getKey());
        append(" = ");
        handle(entry.getValue());
        first = false;
      }
    } else {
      serialize(query.getMetadata(), false, null);
    }
  }

  public void serializeForUpdate(QueryMetadata md, Map<Path<?>, Expression<?>> updates) {
    append(UPDATE);
    handleJoinTarget(md.getJoins().get(0));
    append(SET);
    var first = true;
    for (Map.Entry<Path<?>, Expression<?>> entry : updates.entrySet()) {
      if (!first) {
        append(", ");
      }
      handle(entry.getKey());
      append(" = ");
      handle(entry.getValue());
      first = false;
    }
    if (md.getWhere() != null) {
      append(WHERE).handle(md.getWhere());
    }
  }

  private void serializeSources(boolean forCountRow, List<JoinExpression> joins) {
    for (var i = 0; i < joins.size(); i++) {
      final var je = joins.get(i);
      if (i > 0) {
        append(joinTypes.get(je.getType()));
      }
      if (je.hasFlag(JPAQueryMixin.FETCH) && !forCountRow) {
        handle(JPAQueryMixin.FETCH);
      }
      handleJoinTarget(je);
      if (je.getCondition() != null) {
        append(templates.isWithForOn() ? WITH : ON);
        handle(je.getCondition());
      }
    }
  }

  @Override
  public void visitConstant(Object constant) {
    if (inCaseOperation && templates.isCaseWithLiterals()) {
      if (constant instanceof Collection<?> collection) {
        append("(");
        var first = true;
        for (Object o : collection) {
          if (!first) {
            append(", ");
          }
          visitLiteral(o);
          first = false;
        }
        append(")");
      } else {
        visitLiteral(constant);
      }
    } else {
      var wrap = templates.wrapConstant(constant);
      if (wrap) {
        append("(");
      }
      super.visitConstant(constant);
      if (wrap) {
        append(")");
      }
    }
  }

  public void visitLiteral(Object constant) {
    append(templates.asLiteral(constant));
  }

  @Override
  protected void serializeConstant(int parameterIndex, String constantLabel) {
    append("?");
    append(Integer.toString(parameterIndex));
  }

  @Override
  public Void visit(SubQueryExpression<?> query, Void context) {
    append("(");
    serialize(query.getMetadata(), false, null);
    append(")");
    return null;
  }

  @Override
  public Void visit(Path<?> expr, Void context) {
    // only wrap a PathCollection, if it the pathType is PROPERTY
    var wrap =
        wrapElements
            && (Collection.class.isAssignableFrom(expr.getType())
                || Map.class.isAssignableFrom(expr.getType()))
            && expr.getMetadata().getPathType().equals(PathType.PROPERTY);
    if (wrap) {
      append("elements(");
    }
    super.visit(expr, context);
    if (wrap) {
      append(")");
    }
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void visitOperation(
      Class<?> type, Operator operator, List<? extends Expression<?>> args) {
    var oldInCaseOperation = inCaseOperation;
    inCaseOperation = CASE_OPS.contains(operator);
    var oldWrapElements = wrapElements;
    wrapElements = templates.wrapElements(operator);

    if (operator == Ops.EQ
        && args.get(1) instanceof Operation
        && ((Operation) args.get(1)).getOperator() == Ops.QuantOps.ANY) {
      args = Arrays.<Expression<?>>asList(args.get(0), ((Operation) args.get(1)).getArg(0));
      visitOperation(type, Ops.IN, args);

    } else if (operator == Ops.NE
        && args.get(1) instanceof Operation
        && ((Operation) args.get(1)).getOperator() == Ops.QuantOps.ANY) {
      args = Arrays.<Expression<?>>asList(args.get(0), ((Operation) args.get(1)).getArg(0));
      visitOperation(type, Ops.NOT_IN, args);

    } else if (operator == Ops.IN || operator == Ops.NOT_IN) {
      if (args.get(1) instanceof Path) {
        visitAnyInPath(type, operator, args);
      } else if (args.get(0) instanceof Path && args.get(1) instanceof Constant<?>) {
        visitPathInCollection(type, operator, args);
      } else {
        super.visitOperation(type, operator, args);
      }

    } else if (operator == Ops.NUMCAST) {
      visitNumCast(args);

    } else if (operator == Ops.EXISTS && args.get(0) instanceof SubQueryExpression) {
      final var subQuery = (SubQueryExpression) args.get(0);
      append("exists (");
      serialize(subQuery.getMetadata(), false, templates.getExistsProjection());
      append(")");

    } else if (operator == Ops.MATCHES || operator == Ops.MATCHES_IC) {
      super.visitOperation(
          type,
          Ops.LIKE,
          Arrays.asList(
              args.get(0), ExpressionUtils.regexToLike((Expression<String>) args.get(1))));

    } else if (operator == Ops.LIKE && args.get(1) instanceof Constant<?>) {
      final var escape = String.valueOf(templates.getEscapeChar());
      final var escaped = args.get(1).toString().replace(escape, escape + escape);
      super.visitOperation(
          String.class, Ops.LIKE, Arrays.asList(args.get(0), ConstantImpl.create(escaped)));

    } else if (NUMERIC.contains(operator)) {
      super.visitOperation(type, operator, normalizeNumericArgs(args));

    } else if (operator == Ops.ALIAS) {
      if (args.get(1) instanceof Path && !((Path<?>) args.get(1)).getMetadata().isRoot()) {
        Path<?> path = (Path<?>) args.get(1);
        args =
            Arrays.asList(
                args.get(0), ExpressionUtils.path(path.getType(), path.getMetadata().getName()));
      }
      super.visitOperation(type, operator, args);

    } else {
      try {
        super.visitOperation(type, operator, args);
      } catch (IllegalArgumentException e) {
        if (operator.getClass().getName().endsWith("SQLOps")) {
          throw new IllegalArgumentException(
              String.format(
                  """
                  SQL Expressions like %s are not supported in JPQL - the query language for JPA.\
                   SQLExpressions.* can only be used in JPQL queries when these functions\
                   are registered as custom function in your ORM.%n	To fix this issue, you\
                   have three options:%n	1) If you do want to use advanced, dialect\
                   specific, SQL functions within JPQL, make sure to make these functions\
                   available to your ORM through custom functions and register these with\
                   your JPATemplates instance.%n	2) Use JPASQLQuery instead. This allows\
                   you to generate a pure SQL query based on your JPA metamodel.%n	3)\
                   Consider using the Blaze-Persistence QueryDSL integration.\
                   Blaze-Persistence is an extension on top of JPA that makes various SQL\
                   specific functions like window functions available to JPQL.""",
                  operator.name()),
              e);
        } else {
          throw e;
        }
      }
    }

    inCaseOperation = oldInCaseOperation;
    wrapElements = oldWrapElements;
  }

  private void visitNumCast(List<? extends Expression<?>> args) {
    @SuppressWarnings("unchecked") // this is the second argument's type
    var rightArg = (Constant<Class<?>>) args.get(1);

    final Class<?> targetType = rightArg.getConstant();
    final var typeName = templates.getTypeForCast(targetType);
    visitOperation(
        targetType, JPQLOps.CAST, Arrays.asList(args.get(0), ConstantImpl.create(typeName)));
  }

  private void visitPathInCollection(
      Class<?> type, Operator operator, List<? extends Expression<?>> args) {
    Path<?> lhs = (Path<?>) args.get(0);
    @SuppressWarnings("unchecked")
    Constant<? extends Collection<?>> rhs = (Constant<? extends Collection<?>>) args.get(1);
    if (rhs.getConstant().isEmpty()) {
      operator = operator == Ops.IN ? Ops.EQ : Ops.NE;
      args = Arrays.<Expression<?>>asList(Expressions.ONE, Expressions.TWO);
    } else if (entityManager != null
        && !templates.isPathInEntitiesSupported()
        && args.get(0).getType().isAnnotationPresent(Entity.class)) {
      final var metamodel = entityManager.getMetamodel();
      final var util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
      final EntityType<?> entityType = metamodel.entity(args.get(0).getType());
      if (entityType.hasSingleIdAttribute()) {
        SingularAttribute<?, ?> id = getIdProperty(entityType);
        // turn lhs into id path
        lhs = ExpressionUtils.path(id.getJavaType(), lhs, id.getName());
        // turn rhs into id collection
        Set<Object> ids = new HashSet<>();
        for (Object entity : rhs.getConstant()) {
          ids.add(util.getIdentifier(entity));
        }
        rhs = ConstantImpl.create(ids);
        args = Arrays.asList(lhs, rhs);
      }
    }

    super.visitOperation(type, operator, args);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private SingularAttribute<?, ?> getIdProperty(EntityType entity) {
    final Set<SingularAttribute> singularAttributes = entity.getSingularAttributes();
    for (final SingularAttribute singularAttribute : singularAttributes) {
      if (singularAttribute.isId()) {
        return singularAttribute;
      }
    }
    return null;
  }

  private void visitAnyInPath(
      Class<?> type, Operator operator, List<? extends Expression<?>> args) {
    super.visitOperation(
        type, operator == Ops.IN ? JPQLOps.MEMBER_OF : JPQLOps.NOT_MEMBER_OF, args);
  }

  @SuppressWarnings("unchecked")
  private List<? extends Expression<?>> normalizeNumericArgs(List<? extends Expression<?>> args) {
    // we do not yet let it produce these types
    // we verify the types with isAssignableFrom()
    @SuppressWarnings("unchecked")
    List<? extends Expression<? extends Number>> potentialArgs =
        (List<? extends Expression<? extends Number>>) args;
    var hasConstants = false;
    Class<? extends Number> numType = null;
    for (Expression<? extends Number> arg : potentialArgs) {
      if (Number.class.isAssignableFrom(arg.getType())) {
        if (arg instanceof Constant<?>) {
          hasConstants = true;
        } else {
          numType = arg.getType();
        }
      }
    }
    if (hasConstants && numType != null) {
      // now we do let the potentialArgs help us
      final List<Expression<?>> newArgs = new ArrayList<>(args.size());
      for (final Expression<? extends Number> arg : potentialArgs) {
        if (arg instanceof Constant<?>
            && Number.class.isAssignableFrom(arg.getType())
            && !arg.getType().equals(numType)) {
          final Number number = ((Constant<? extends Number>) arg).getConstant();
          newArgs.add(ConstantImpl.create(MathUtils.cast(number, numType)));
        } else {
          newArgs.add(arg);
        }
      }
      return newArgs;
    } else {
      // the types are all non-constants, or not Number expressions
      return potentialArgs;
    }
  }
}
