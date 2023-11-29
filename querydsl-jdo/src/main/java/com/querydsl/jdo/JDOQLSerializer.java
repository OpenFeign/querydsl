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
package com.querydsl.jdo;

import com.querydsl.core.JoinExpression;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.support.SerializerBase;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Param;
import com.querydsl.core.util.PrimitiveUtils;
import java.util.*;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;

/**
 * {@code JDOQLSerializer} serializes Querydsl queries and expressions into JDOQL strings
 *
 * @author tiwe
 */
public final class JDOQLSerializer extends SerializerBase<JDOQLSerializer> {

  private static final String COMMA = ", ";

  private static final String FROM = "\nFROM ";

  private static final String GROUP_BY = "\nGROUP BY ";

  private static final String HAVING = "\nHAVING ";

  private static final String ORDER_BY = "\nORDER BY ";

  private static final String PARAMETERS = "\nPARAMETERS ";

  private static final String RANGE = "\nRANGE ";

  private static final String SELECT = "SELECT ";

  private static final String SELECT_COUNT = "SELECT count(";

  private static final String SELECT_COUNT_THIS = "SELECT count(this)\n";

  private static final String SELECT_DISTINCT = "SELECT DISTINCT ";

  private static final String SELECT_UNIQUE = "SELECT UNIQUE ";

  private static final String THIS = "this";

  private static final String VARIABLES = "\nVARIABLES ";

  private static final String WHERE = "\nWHERE ";

  private static Comparator<Map.Entry<Object, String>> comparator =
      new Comparator<Map.Entry<Object, String>>() {
        @Override
        public int compare(Entry<Object, String> o1, Entry<Object, String> o2) {
          return o1.getValue().compareTo(o2.getValue());
        }
      };

  private final Expression<?> candidatePath;

  private final List<Object> constants = new ArrayList<Object>();

  private final Stack<Map<Object, String>> constantToLabel = new Stack<Map<Object, String>>();

  public JDOQLSerializer(JDOQLTemplates templates, Expression<?> candidate) {
    super(templates);
    this.candidatePath = candidate;
    this.constantToLabel.push(new LinkedHashMap<>());
  }

  public Expression<?> getCandidatePath() {
    return candidatePath;
  }

  @Override
  public List<Object> getConstants() {
    return constants;
  }

  @Override
  public Map<Object, String> getConstantToLabel() {
    return constantToLabel.peek();
  }

  public void serialize(QueryMetadata metadata, boolean forCountRow, boolean subQuery) {
    final Expression<?> select = metadata.getProjection();
    final List<JoinExpression> joins = metadata.getJoins();
    final Expression<?> source = joins.get(0).getTarget();
    final Predicate where = metadata.getWhere();
    final List<? extends Expression<?>> groupBy = metadata.getGroupBy();
    final Predicate having = metadata.getHaving();
    final List<OrderSpecifier<?>> orderBy = metadata.getOrderBy();

    constantToLabel.push(new LinkedHashMap<Object, String>());

    // select
    boolean skippedSelect = false;
    if (forCountRow) {
      if (joins.size() == 1 && !subQuery) {
        append(SELECT_COUNT_THIS);
      } else {
        append(SELECT_COUNT);
        handle(source);
        append(")");
      }

    } else if (select != null) {
      if (metadata.isDistinct()) {
        append(SELECT_DISTINCT);
      } else if (metadata.isUnique() && !subQuery) {
        append(SELECT_UNIQUE);
      } else {
        append(SELECT);
      }
      if (!select.equals(source) || metadata.isDistinct()) {
        handle(select);
      } else {
        skippedSelect = true;
      }
    }

    // from
    append(skippedSelect ? FROM.substring(1) : FROM);
    if (source instanceof Operation && subQuery) {
      handle(source);
    } else {
      append(source.getType().getName());
      if (!source.equals(candidatePath)) {
        append(" ").handle(source);
      }
    }

    // where
    if (where != null) {
      append(WHERE).handle(where);
    }

    // variables
    if (joins.size() > 1) {
      serializeVariables(joins);
    }

    int position = getLength();

    // group by
    if (!groupBy.isEmpty()) {
      append(GROUP_BY).handle(COMMA, groupBy);
    }

    // having
    if (having != null) {
      append(HAVING).handle(having);
    }

    // order by
    if (!orderBy.isEmpty() && !forCountRow) {
      append(ORDER_BY);
      boolean first = true;
      for (final OrderSpecifier<?> os : orderBy) {
        if (!first) {
          append(COMMA);
        }
        handle(os.getTarget());
        append(" " + os.getOrder());
        first = false;
      }
    }

    // range
    if (!forCountRow && metadata.getModifiers().isRestricting()) {
      Long limit = metadata.getModifiers().getLimit();
      Long offset = metadata.getModifiers().getOffset();
      serializeModifiers(limit, offset);
    }

    // parameters
    if (!getConstantToLabel().isEmpty()) {
      insert(position, serializeParameters(metadata.getParams()));
    }

    constantToLabel.pop();
  }

  private void serializeModifiers(@Nullable Long limit, @Nullable Long offset) {
    append(RANGE);
    if (offset != null) {
      append(String.valueOf(offset));
      if (limit != null) {
        append(COMMA);
        append(String.valueOf(offset + limit));
      }
    } else {
      append("0, ").append(String.valueOf(limit));
    }
  }

  private String serializeParameters(Map<ParamExpression<?>, Object> params) {
    final StringBuilder b = new StringBuilder();
    b.append(PARAMETERS);
    boolean first = true;
    final Collection<Map.Entry<Object, String>> entries = getConstantToLabel().entrySet();
    for (Map.Entry<Object, String> entry : entries) {
      if (!first) {
        b.append(COMMA);
      }
      if (entry.getKey() instanceof Param) {
        Object constant = params.get(entry.getKey());
        if (constant == null) {
          throw new ParamNotSetException((Param<?>) entry.getKey());
        }
        constants.add(constant);
        b.append(((Param<?>) entry.getKey()).getType().getName());
      } else {
        constants.add(entry.getKey());
        b.append(entry.getKey().getClass().getName());
      }
      b.append(" ").append(entry.getValue());
      first = false;
    }
    return b.toString();
  }

  private void serializeVariables(List<JoinExpression> joins) {
    append(VARIABLES);
    for (int i = 1; i < joins.size(); i++) {
      final JoinExpression je = joins.get(i);
      if (i > 1) {
        append("; ");
      }

      // type specifier
      if (je.getTarget() instanceof EntityPath) {
        final EntityPath<?> pe = (EntityPath<?>) je.getTarget();
        if (pe.getMetadata().getParent() == null) {
          append(pe.getType().getName()).append(" ");
        }
      }
      handle(je.getTarget());
    }
  }

  @Override
  public Void visit(Path<?> path, Void context) {
    if (path.equals(candidatePath)) {
      append(THIS);
    } else {
      super.visit(path, context);
    }
    return null;
  }

  @Override
  public Void visit(SubQueryExpression<?> query, Void context) {
    append("(");
    serialize(query.getMetadata(), false, true);
    append(")");
    return null;
  }

  @Override
  protected void visitOperation(
      Class<?> type, Operator operator, List<? extends Expression<?>> args) {
    if (operator == Ops.INSTANCE_OF) {
      handle(args.get(0)).append(" instanceof ");
      @SuppressWarnings("unchecked") // This is the expected type for instanceOf
      Constant<Class<?>> rightArg = (Constant<Class<?>>) args.get(1);
      append(rightArg.getConstant().getName());

    } else if (operator == Ops.LIKE
        || operator == Ops.LIKE_ESCAPE
        || operator == Ops.LIKE_IC
        || operator == Ops.LIKE_ESCAPE_IC) {
      @SuppressWarnings("unchecked") // This is the expected type for like
      Expression<String> string = (Expression<String>) args.get(0);
      @SuppressWarnings("unchecked") // This is the expected type for like
      Expression<String> regex =
          ExpressionUtils.likeToRegex((Expression<String>) args.get(1), false);
      if (operator == Ops.LIKE_IC || operator == Ops.LIKE_ESCAPE_IC) {
        string = ExpressionUtils.toLower(string);
        regex = ExpressionUtils.toLower(regex);
      }
      super.visitOperation(type, Ops.MATCHES, Arrays.asList(string, regex));

      // exists
    } else if (operator == Ops.EXISTS && args.get(0) instanceof SubQueryExpression) {
      final SubQueryExpression subQuery = (SubQueryExpression) args.get(0);
      append("(");
      serialize(subQuery.getMetadata(), true, true);
      append(") > 0");

      // not exists
    } else if (operator == Ops.NOT
        && args.get(0) instanceof Operation
        && ((Operation) args.get(0)).getOperator().equals(Ops.EXISTS)) {
      final SubQueryExpression subQuery = (SubQueryExpression) ((Operation) args.get(0)).getArg(0);
      append("(");
      serialize(subQuery.getMetadata(), true, true);
      append(") == 0");

    } else if (operator == Ops.NUMCAST) {
      @SuppressWarnings("unchecked") // This is the expected type for castToNum
      Constant<Class<?>> rightArg = (Constant<Class<?>>) args.get(1);
      Class<?> clazz = rightArg.getConstant();
      if (Number.class.isAssignableFrom(clazz) && PrimitiveUtils.isWrapperType(clazz)) {
        clazz = PrimitiveUtils.unwrap(clazz);
      }
      append("(" + clazz.getSimpleName() + ")").handle(args.get(0));

    } else if (operator == Ops.ALIAS) {
      if (args.get(1) instanceof Path && !((Path<?>) args.get(1)).getMetadata().isRoot()) {
        Path<?> path = (Path<?>) args.get(1);
        args =
            Arrays.asList(
                args.get(0), ExpressionUtils.path(path.getType(), path.getMetadata().getName()));
      }
      super.visitOperation(type, operator, args);

    } else {
      super.visitOperation(type, operator, args);
    }
  }
}
