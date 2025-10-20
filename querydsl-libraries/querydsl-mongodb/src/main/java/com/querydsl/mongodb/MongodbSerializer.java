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
package com.querydsl.mongodb;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathType;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.Visitor;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import org.bson.BSONObject;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 * Serializes the given Querydsl query to a DBObject query for MongoDB
 *
 * @author laimw
 * @author sangyong choi
 */
public abstract class MongodbSerializer implements Visitor<Object, Void> {

  public static final String MONGO_EXPRESSION = "$expr";
  public static final String MONGO_EXPR_SYMBOL = "$";

  public Object handle(Expression<?> expression) {
    return expression.accept(this, null);
  }

  public Bson toSort(List<OrderSpecifier<?>> orderBys) {
    var sort = new BasicDBObject();
    for (OrderSpecifier<?> orderBy : orderBys) {
      Object key = orderBy.getTarget().accept(this, null);
      sort.append(key.toString(), orderBy.getOrder() == Order.ASC ? 1 : -1);
    }
    return sort;
  }

  @Override
  public Object visit(Constant<?> expr, Void context) {
    if (Enum.class.isAssignableFrom(expr.getType())) {
      @SuppressWarnings("unchecked") // Guarded by previous check
      Constant<? extends Enum<?>> expectedExpr = (Constant<? extends Enum<?>>) expr;
      return expectedExpr.getConstant().name();
    } else {
      return expr.getConstant();
    }
  }

  @Override
  public Object visit(TemplateExpression<?> expr, Void context) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(FactoryExpression<?> expr, Void context) {
    throw new UnsupportedOperationException();
  }

  protected String asDBKey(Operation<?> expr, int index) {
    return (String) asDBValue(expr, index);
  }

  protected Object asDBValue(Operation<?> expr, int index) {
    return expr.getArg(index).accept(this, null);
  }

  private String regexValue(Operation<?> expr, int index) {
    return Pattern.quote(expr.getArg(index).accept(this, null).toString());
  }

  protected DBObject asDBObject(String key, Object value) {
    return new BasicDBObject(key, value);
  }

  protected DBObject asDBObjectOrExpressionNotIn(Operation<?> expr, int exprIndex, int constIndex) {
    boolean rightConst = expr.getArg(constIndex) instanceof Constant<?>;
    boolean rightCollectionConst =
        rightConst && Collection.class.isAssignableFrom(expr.getArg(constIndex).getType());
    if (rightCollectionConst) {
      @SuppressWarnings("unchecked") // guarded by previous check
      Collection<?> values =
          ((Constant<? extends Collection<?>>) expr.getArg(constIndex)).getConstant();
      return asDBObject(asDBKey(expr, exprIndex), asDBObject("$nin", values.toArray()));
    } else if (rightConst) {
      Path<?> path = (Path<?>) expr.getArg(exprIndex);
      Constant<?> constant = (Constant<?>) expr.getArg(constIndex);
      return asDBObject(asDBKey(expr, exprIndex), asDBObject("$ne", convert(path, constant)));
    } else {
      Object leftField = MONGO_EXPR_SYMBOL + asDBKey(expr, constIndex);
      Object rightValue = expr.getArg(exprIndex).accept(this, null);
      List<BasicDBObject> conditions = new ArrayList<>();

      if (Collection.class.isAssignableFrom(rightValue.getClass())) {
        for (Object rv : (Collection<?>) rightValue) {
          conditions.add(
              new BasicDBObject(
                  "$ne", Arrays.asList(leftField, MONGO_EXPR_SYMBOL + rv.toString())));
        }
      } else {
        conditions.add(new BasicDBObject("$ne", Arrays.asList(leftField, rightValue)));
      }
      return new BasicDBObject(MONGO_EXPRESSION, new BasicDBObject("$and", conditions));
    }
  }

  protected DBObject asDBObjectOrExpressionIn(Operation<?> expr, int exprIndex, int constIndex) {
    boolean rightConst = expr.getArg(constIndex) instanceof Constant<?>;
    boolean rightCollectionConst =
        rightConst && Collection.class.isAssignableFrom(expr.getArg(constIndex).getType());
    if (rightCollectionConst) {
      @SuppressWarnings("unchecked")
      Collection<?> values =
          ((Constant<? extends Collection<?>>) expr.getArg(constIndex)).getConstant();
      return asDBObject(asDBKey(expr, exprIndex), new BasicDBObject("$in", values.toArray()));
    } else if (rightConst) {
      Path<?> path = (Path<?>) expr.getArg(exprIndex);
      Constant<?> constant = (Constant<?>) expr.getArg(constIndex);
      return asDBObject(asDBKey(expr, exprIndex), convert(path, constant));
    } else {
      Object rightField = MONGO_EXPR_SYMBOL + asDBKey(expr, constIndex);
      Object valueField;
      Object leftValue = expr.getArg(exprIndex).accept(this, null);
      if (Collection.class.isAssignableFrom(leftValue.getClass())) {
        valueField =
            ((Collection<?>) leftValue)
                .stream().map(v -> MONGO_EXPR_SYMBOL + v.toString()).toList();
      } else {
        valueField = List.of(MONGO_EXPR_SYMBOL + asDBKey(expr, exprIndex));
      }
      BasicDBObject inCondition = new BasicDBObject("$in", Arrays.asList(rightField, valueField));
      return new BasicDBObject(MONGO_EXPRESSION, inCondition);
    }
  }

  private Object asDbList(Operation<?> expr) {
    List<Expression<?>> values = (expr.getArgs().stream().toList());
    return values.stream()
        .map(v -> v.accept(this, null))
        .flatMap(
            o -> {
              if (o instanceof Collection<?> coll) {
                return coll.stream();
              } else {
                return Arrays.stream(new Object[] {o});
              }
            })
        .toList();
  }

  protected DBObject asDBObjectOrExpressionBetween(Operation<?> expr) {
    boolean arg1Const = expr.getArg(1) instanceof Constant<?>;
    boolean arg2Const = expr.getArg(2) instanceof Constant<?>;

    if (arg1Const && arg2Const) {
      BasicDBObject range =
          new BasicDBObject("$gte", asDBValue(expr, 1)).append("$lte", asDBValue(expr, 2));
      return asDBObject(asDBKey(expr, 0), range);
    } else {
      Object leftField = MONGO_EXPR_SYMBOL + asDBKey(expr, 0);
      Object right1 = arg1Const ? asDBValue(expr, 1) : MONGO_EXPR_SYMBOL + asDBKey(expr, 1);
      Object right2 = arg2Const ? asDBValue(expr, 2) : MONGO_EXPR_SYMBOL + asDBKey(expr, 2);
      BasicDBObject gteCondition =
          new BasicDBObject("$gte", java.util.Arrays.asList(leftField, right1));
      BasicDBObject lteCondition =
          new BasicDBObject("$lte", java.util.Arrays.asList(leftField, right2));

      BasicDBObject inner =
          new BasicDBObject("$and", java.util.Arrays.asList(gteCondition, lteCondition));
      return new BasicDBObject(MONGO_EXPRESSION, inner);
    }
  }

  protected DBObject asDBObjectOrExpression(String op, Operation<?> expr) {
    if (expr.getArg(1) instanceof Constant<?>) { // preferred if constant for better performance
      return asDBObject(asDBKey(expr, 0), asDBObject(op, asDBValue(expr, 1)));
    } else {
      return asExpression(op, asDBKey(expr, 0), asDBKey(expr, 1));
    }
  }

  protected BasicDBObject asExpression(String op, String left, Object right) {
    BasicDBObject inner =
        new BasicDBObject(
            op, java.util.Arrays.asList(MONGO_EXPR_SYMBOL + left, MONGO_EXPR_SYMBOL + right));
    return new BasicDBObject(MONGO_EXPRESSION, inner);
  }

  private enum STRING_MATCH_TYPE {
    STARTS_WITH,
    ENDS_WITH,
    EQUALS,
    CONTAINS,
    MATCHES;

    public Function<Object, List<?>> toRegex() {
      return s ->
          switch (this) {
            case STARTS_WITH -> List.of("^", s);
            case ENDS_WITH -> List.of("", s, "\\$");
            case EQUALS -> List.of("^", s, "\\$");
            case CONTAINS -> List.of(".*", s, ".*");
            case MATCHES -> List.of(s);
          };
    }
  }

  private BasicDBObject createRegexMatch(
      Operation<?> expr, boolean ignoreCase, STRING_MATCH_TYPE startsWithOrEndsWith) {
    Object leftField = MONGO_EXPR_SYMBOL + asDBKey(expr, 0);
    Object rightField = MONGO_EXPR_SYMBOL + asDBKey(expr, 1);

    BasicDBObject regexMatch = new BasicDBObject();
    regexMatch.put("input", leftField);
    regexMatch.put(
        "regex", new BasicDBObject("$concat", startsWithOrEndsWith.toRegex().apply(rightField)));
    if (ignoreCase) {
      regexMatch.put("options", "i");
    }
    BasicDBObject exprObj = new BasicDBObject("$regexMatch", regexMatch);
    return new BasicDBObject(MONGO_EXPRESSION, exprObj);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object visit(Operation<?> expr, Void context) {
    var op = expr.getOperator();
    if (op == Ops.EQ) {
      if (expr.getArg(0) instanceof Operation<?> lhs) {
        if (lhs.getOperator() == Ops.COL_SIZE || lhs.getOperator() == Ops.ARRAY_SIZE) {
          return asDBObject(asDBKey(lhs, 0), asDBObject("$size", asDBValue(expr, 1)));
        } else {
          throw new UnsupportedOperationException("Illegal operation " + expr);
        }
      } else if (expr.getArg(0) instanceof Path<?> path) {
        Object constant = expr.getArg(1);
        if (constant instanceof Constant<?> constantValue) {
          return asDBObject(asDBKey(expr, 0), convert(path, constantValue));
        } else {
          Object rightField = MONGO_EXPR_SYMBOL + asDBKey(expr, 1);
          Object leftField = MONGO_EXPR_SYMBOL + asDBKey(expr, 0);
          BasicDBObject eqCondition =
              new BasicDBObject("$eq", Arrays.asList(leftField, rightField));
          return new BasicDBObject(MONGO_EXPRESSION, eqCondition);
        }
      }
    } else if (op == Ops.STRING_IS_EMPTY) {
      return asDBObject(asDBKey(expr, 0), "");

    } else if (op == Ops.AND) {
      var lhs = (BSONObject) handle(expr.getArg(0));
      var rhs = (BSONObject) handle(expr.getArg(1));
      if (lhs.keySet().stream().noneMatch(rhs.keySet()::contains)) {
        lhs.putAll(rhs);
        return lhs;
      } else {
        var list = new BasicDBList();
        list.add(handle(expr.getArg(0)));
        list.add(handle(expr.getArg(1)));
        return asDBObject("$and", list);
      }

    } else if (op == Ops.NOT) {
      // Handle the not's child
      Operation<?> subOperation = (Operation<?>) expr.getArg(0);
      var subOp = subOperation.getOperator();
      if (subOp == Ops.IN) {
        return visit(
            ExpressionUtils.operation(
                Boolean.class, Ops.NOT_IN, subOperation.getArg(0), subOperation.getArg(1)),
            context);
      } else {
        var arg = (BasicDBObject) handle(expr.getArg(0));
        return negate(arg);
      }

    } else if (op == Ops.OR) {
      var list = new BasicDBList();
      list.add(handle(expr.getArg(0)));
      list.add(handle(expr.getArg(1)));
      return asDBObject("$or", list);

    } else if (op == Ops.NE) {
      Path<?> path = (Path<?>) expr.getArg(0);
      Object constant = expr.getArg(1);
      if (constant instanceof Constant<?> constantValue) {
        return asDBObject(asDBKey(expr, 0), asDBObject("$ne", convert(path, constantValue)));
      } else {
        Object rightField = MONGO_EXPR_SYMBOL + asDBKey(expr, 1);
        Object leftField = MONGO_EXPR_SYMBOL + asDBKey(expr, 0);
        BasicDBObject eqCondition = new BasicDBObject("$ne", Arrays.asList(leftField, rightField));
        return new BasicDBObject(MONGO_EXPRESSION, eqCondition);
      }

    } else if (op == Ops.STARTS_WITH) {

      if (expr.getArg(1) instanceof Constant<?>) {
        return asDBObject(asDBKey(expr, 0), Pattern.compile("^" + regexValue(expr, 1)));
      } else {
        return createRegexMatch(expr, false, STRING_MATCH_TYPE.STARTS_WITH);
      }

    } else if (op == Ops.STARTS_WITH_IC) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDBObject(
            asDBKey(expr, 0), Pattern.compile("^" + regexValue(expr, 1), Pattern.CASE_INSENSITIVE));
      } else {
        return createRegexMatch(expr, true, STRING_MATCH_TYPE.STARTS_WITH);
      }

    } else if (op == Ops.ENDS_WITH) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDBObject(asDBKey(expr, 0), Pattern.compile(regexValue(expr, 1) + "$"));
      } else {
        return createRegexMatch(expr, false, STRING_MATCH_TYPE.ENDS_WITH);
      }

    } else if (op == Ops.ENDS_WITH_IC) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDBObject(
            asDBKey(expr, 0), Pattern.compile(regexValue(expr, 1) + "$", Pattern.CASE_INSENSITIVE));
      } else {
        return createRegexMatch(expr, true, STRING_MATCH_TYPE.ENDS_WITH);
      }
    } else if (op == Ops.EQ_IGNORE_CASE) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDBObject(
            asDBKey(expr, 0),
            Pattern.compile("^" + regexValue(expr, 1) + "$", Pattern.CASE_INSENSITIVE));

      } else {
        return createRegexMatch(expr, true, STRING_MATCH_TYPE.EQUALS);
      }

    } else if (op == Ops.STRING_CONTAINS) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDBObject(asDBKey(expr, 0), Pattern.compile(".*" + regexValue(expr, 1) + ".*"));
      } else {
        return createRegexMatch(expr, false, STRING_MATCH_TYPE.CONTAINS);
      }

    } else if (op == Ops.STRING_CONTAINS_IC) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDBObject(
            asDBKey(expr, 0),
            Pattern.compile(".*" + regexValue(expr, 1) + ".*", Pattern.CASE_INSENSITIVE));
      } else {
        return createRegexMatch(expr, true, STRING_MATCH_TYPE.CONTAINS);
      }
    } else if (op == Ops.MATCHES) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDBObject(asDBKey(expr, 0), Pattern.compile(asDBValue(expr, 1).toString()));
      } else {
        return createRegexMatch(expr, false, STRING_MATCH_TYPE.MATCHES);
      }

    } else if (op == Ops.MATCHES_IC) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDBObject(
            asDBKey(expr, 0),
            Pattern.compile(asDBValue(expr, 1).toString(), Pattern.CASE_INSENSITIVE));
      } else {
        return createRegexMatch(expr, true, STRING_MATCH_TYPE.MATCHES);
      }

    } else if (op == Ops.LIKE) {
      if (expr.getArg(1) instanceof Constant<?>) {
        var regex = ExpressionUtils.likeToRegex((Expression) expr.getArg(1)).toString();
        return asDBObject(asDBKey(expr, 0), Pattern.compile(regex));
      } else {
        return createRegexMatch(expr, false, STRING_MATCH_TYPE.MATCHES);
      }

    } else if (op == Ops.LIKE_IC) {
      if (expr.getArg(1) instanceof Constant<?>) {
        var regex = ExpressionUtils.likeToRegex((Expression) expr.getArg(1)).toString();
        return asDBObject(asDBKey(expr, 0), Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
      } else {
        return createRegexMatch(expr, true, STRING_MATCH_TYPE.MATCHES);
      }

    } else if (op == Ops.BETWEEN) {
      return asDBObjectOrExpressionBetween(expr);

    } else if (op == Ops.IN) {
      var constIndex = 0;
      var exprIndex = 1;
      if (expr.getArg(1) instanceof Constant<?>) {
        constIndex = 1;
        exprIndex = 0;
      }
      return asDBObjectOrExpressionIn(expr, exprIndex, constIndex);
    } else if (op == Ops.NOT_IN) {
      var constIndex = 0;
      var exprIndex = 1;
      if (expr.getArg(1) instanceof Constant<?>) {
        constIndex = 1;
        exprIndex = 0;
      }
      return asDBObjectOrExpressionNotIn(expr, exprIndex, constIndex);

    } else if (op == Ops.COL_IS_EMPTY) {
      var list = new BasicDBList();
      list.add(asDBObject(asDBKey(expr, 0), new BasicDBList()));
      list.add(asDBObject(asDBKey(expr, 0), asDBObject("$exists", false)));
      return asDBObject("$or", list);

    } else if (op == Ops.LT) {
      return asDBObjectOrExpression("$lt", expr);

    } else if (op == Ops.GT) {
      return asDBObjectOrExpression("$gt", expr);

    } else if (op == Ops.LOE) {
      return asDBObjectOrExpression("$lte", expr);
    } else if (op == Ops.GOE) {
      return asDBObjectOrExpression("$gte", expr);

    } else if (op == Ops.IS_NULL) {
      return asDBObject(asDBKey(expr, 0), asDBObject("$exists", false));

    } else if (op == Ops.IS_NOT_NULL) {
      return asDBObject(asDBKey(expr, 0), asDBObject("$exists", true));

    } else if (op == Ops.CONTAINS_KEY) {
      Path<?> path = (Path<?>) expr.getArg(0);
      Expression<?> key = expr.getArg(1);
      return asDBObject(visit(path, context) + "." + key.toString(), asDBObject("$exists", true));

    } else if (op == MongodbOps.NEAR) {
      return asDBObject(asDBKey(expr, 0), asDBObject("$near", asDBValue(expr, 1)));

    } else if (op == MongodbOps.GEO_WITHIN_BOX) {
      return asDBObject(
          asDBKey(expr, 0),
          asDBObject(
              "$geoWithin",
              asDBObject("$box", Arrays.asList(asDBValue(expr, 1), asDBValue(expr, 2)))));

    } else if (op == MongodbOps.NEAR_SPHERE) {
      return asDBObject(asDBKey(expr, 0), asDBObject("$nearSphere", asDBValue(expr, 1)));

    } else if (op == MongodbOps.GEO_INTERSECTS) {
      return asDBObject(
          asDBKey(expr, 0),
          asDBObject("$geoIntersects", asDBObject("$geometry", asDBValue(expr, 1))));
    } else if (op == MongodbOps.ALL) {
      return asDBObject(asDBKey(expr, 0), asDBObject("$all", asDBValue(expr, 1)));
    } else if (op == MongodbOps.ELEM_MATCH) {
      return asDBObject(asDBKey(expr, 0), asDBObject("$elemMatch", asDBValue(expr, 1)));
    } else if (op == Ops.SET) {
      return asDbList(expr);
    } else if (op == Ops.LIST) {
      return asDbList(expr);
    }
    throw new UnsupportedOperationException("Illegal operation " + expr);
  }

  private Object negate(BasicDBObject arg) {
    var list = new BasicDBList();
    for (Map.Entry<String, Object> entry : arg.entrySet()) {
      if (entry.getKey().equals("$or")) {
        list.add(asDBObject("$nor", entry.getValue()));

      } else if (entry.getKey().equals("$and")) {
        var list2 = new BasicDBList();
        for (Object o : ((BasicDBList) entry.getValue())) {
          list2.add(negate((BasicDBObject) o));
        }
        list.add(asDBObject("$or", list2));

      } else if (entry.getValue() instanceof Pattern) {
        list.add(asDBObject(entry.getKey(), asDBObject("$not", entry.getValue())));

      } else if (entry.getValue() instanceof BasicDBObject) {
        list.add(negate(entry.getKey(), (BasicDBObject) entry.getValue()));

      } else {
        list.add(asDBObject(entry.getKey(), asDBObject("$ne", entry.getValue())));
      }
    }
    return list.size() == 1 ? list.get(0) : asDBObject("$or", list);
  }

  private Object negate(String key, BasicDBObject value) {
    if (value.size() == 1) {
      return asDBObject(key, asDBObject("$not", value));

    } else {
      var list2 = new BasicDBList();
      for (Map.Entry<String, Object> entry2 : value.entrySet()) {
        list2.add(
            asDBObject(key, asDBObject("$not", asDBObject(entry2.getKey(), entry2.getValue()))));
      }
      return asDBObject("$or", list2);
    }
  }

  protected Object convert(Path<?> property, Constant<?> constant) {
    if (isReference(property)) {
      return asReference(constant.getConstant());
    } else if (isId(property)) {
      if (isReference(property.getMetadata().getParent())) {
        return asReferenceKey(property.getMetadata().getParent().getType(), constant.getConstant());
      } else if (constant.getType().equals(String.class) && isImplicitObjectIdConversion()) {
        var id = (String) constant.getConstant();
        return ObjectId.isValid(id) ? new ObjectId(id) : id;
      }
    }
    return visit(constant, null);
  }

  protected boolean isImplicitObjectIdConversion() {
    return true;
  }

  protected DBRef asReferenceKey(Class<?> entity, Object id) {
    // TODO override in subclass
    throw new UnsupportedOperationException();
  }

  protected abstract DBRef asReference(Object constant);

  protected abstract boolean isReference(Path<?> arg);

  protected boolean isId(Path<?> arg) {
    // TODO override in subclass
    return false;
  }

  @Override
  public String visit(Path<?> expr, Void context) {
    var metadata = expr.getMetadata();
    if (metadata.getParent() != null) {
      Path<?> parent = metadata.getParent();
      if (parent.getMetadata().getPathType() == PathType.DELEGATE) {
        parent = parent.getMetadata().getParent();
      }
      if (metadata.getPathType() == PathType.COLLECTION_ANY) {
        return visit(parent, context);
      } else if (parent.getMetadata().getPathType() != PathType.VARIABLE) {
        var rv = getKeyForPath(expr, metadata);
        var parentStr = visit(parent, context);
        return rv != null ? parentStr + "." + rv : parentStr;
      }
    }
    return getKeyForPath(expr, metadata);
  }

  protected String getKeyForPath(Path<?> expr, PathMetadata metadata) {
    return metadata.getElement().toString();
  }

  @Override
  public Object visit(SubQueryExpression<?> expr, Void context) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object visit(ParamExpression<?> expr, Void context) {
    throw new UnsupportedOperationException();
  }
}
