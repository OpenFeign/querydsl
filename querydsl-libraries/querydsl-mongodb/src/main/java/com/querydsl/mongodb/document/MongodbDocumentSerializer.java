/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.mongodb.document;

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
import com.querydsl.mongodb.MongodbOps;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.bson.BsonJavaScript;
import org.bson.BsonRegularExpression;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Serializes the given Querydsl query to a Document query for MongoDB.
 *
 * @author Mark Paluch
 */
public abstract class MongodbDocumentSerializer implements Visitor<Object, Void> {

  public static final String MONGO_EXPRESSION = "$expr";
  public static final String MONGO_EXPR_SYMBOL = "$";

  public Object handle(Expression<?> expression) {
    return expression.accept(this, null);
  }

  public Document toSort(List<OrderSpecifier<?>> orderBys) {
    var sort = new Document();
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

  protected Document asDocument(String key, Object value) {
    return new Document(key, value);
  }

  protected Document asDocumentOrExpressionBetween(Operation<?> expr) {
    boolean arg1Const = expr.getArg(1) instanceof Constant<?>;
    boolean arg2Const = expr.getArg(2) instanceof Constant<?>;

    if (arg1Const && arg2Const) {
      Document range = new Document("$gte", asDBValue(expr, 1)).append("$lte", asDBValue(expr, 2));
      return new Document(asDBKey(expr, 0), range);
    } else {
      Object leftField = "$" + asDBKey(expr, 0);
      Object right1 = arg1Const ? asDBValue(expr, 1) : "$" + asDBKey(expr, 1);
      Object right2 = arg2Const ? asDBValue(expr, 2) : "$" + asDBKey(expr, 2);

      Document gteCondition = new Document("$gte", java.util.Arrays.asList(leftField, right1));
      Document lteCondition = new Document("$lte", java.util.Arrays.asList(leftField, right2));

      Document andConditions =
          new Document("$and", java.util.Arrays.asList(gteCondition, lteCondition));
      return new Document("$expr", andConditions);
    }
  }

  protected Document asDocumentOrExpression(String op, Operation<?> expr) {
    if (expr.getArg(1) instanceof Constant<?>) { // preferred if constant for better performance
      return asDocument(asDBKey(expr, 0), asDocument(op, asDBValue(expr, 1)));
    } else {
      return asExpression(op, asDBKey(expr, 0), asDBKey(expr, 1));
    }
  }

  protected Document asExpression(String op, String left, Object right) {
    Document inner = new Document(op, List.of(MONGO_EXPR_SYMBOL + left, MONGO_EXPR_SYMBOL + right));
    return new Document(MONGO_EXPRESSION, inner);
  }

  protected Document asDocumentOrExpressionNotIn(Operation<?> expr, int exprIndex, int constIndex) {
    boolean rightConst = expr.getArg(constIndex) instanceof Constant<?>;
    boolean rightCollectionConst =
        rightConst && Collection.class.isAssignableFrom(expr.getArg(constIndex).getType());
    if (rightCollectionConst) {
      @SuppressWarnings("unchecked")
      Collection<?> values =
          ((Constant<? extends Collection<?>>) expr.getArg(constIndex)).getConstant();
      return new Document(asDBKey(expr, exprIndex), new Document("$nin", values));
    } else if (rightConst) {
      Path<?> path = (Path<?>) expr.getArg(exprIndex);
      Constant<?> constant = (Constant<?>) expr.getArg(constIndex);
      return new Document(asDBKey(expr, exprIndex), new Document("$ne", convert(path, constant)));
    } else {
      Object leftField = MONGO_EXPR_SYMBOL + asDBKey(expr, exprIndex);
      Object rightValue = expr.getArg(constIndex).accept(this, null);
      List<Document> conditions = new ArrayList<>();
      if (Collection.class.isAssignableFrom(rightValue.getClass())) {
        for (Object rv : (Collection<?>) rightValue) {
          conditions.add(
              new Document("$ne", Arrays.asList(leftField, MONGO_EXPR_SYMBOL + rv.toString())));
        }
      } else {
        conditions.add(new Document("$ne", Arrays.asList(leftField, rightValue)));
      }
      return new Document(MONGO_EXPRESSION, new Document("$and", conditions));
    }
  }

  private enum STRING_MATCH_TYPE {
    STARTS_WITH,
    ENDS_WITH,
    EQUALS,
    CONTAINS,
    MATCHES;

    public Function<Object, List<?>> toRegex() {
      return s -> {
        switch (this) {
          case STARTS_WITH:
            return List.of("^", s);
          case ENDS_WITH:
            return List.of("", s, "\\$");
          case EQUALS:
            return List.of("^", s, "\\$");
          case CONTAINS:
            return List.of(".*", s, ".*");
          case MATCHES:
            return List.of(s);
          default:
            throw new IllegalArgumentException("Unknown match type: " + this);
        }
      };
    }
  }

  private Document createRegexMatch(
      Operation<?> expr, boolean ignoreCase, STRING_MATCH_TYPE startsWithOrEndsWith) {
    Object leftField = MONGO_EXPR_SYMBOL + asDBKey(expr, 0);
    Object rightField = MONGO_EXPR_SYMBOL + asDBKey(expr, 1);

    Document regexMatch = new Document();
    regexMatch.append("input", leftField);
    regexMatch.append(
        "regex", new Document("$concat", startsWithOrEndsWith.toRegex().apply(rightField)));

    if (ignoreCase) {
      regexMatch.append("options", "i");
    }

    Document exprObj = new Document("$regexMatch", regexMatch);
    return new Document(MONGO_EXPRESSION, exprObj);
  }

  protected Document asDocumentOrExpressionIn(Operation<?> expr, int exprIndex, int constIndex) {
    boolean rightConst = expr.getArg(constIndex) instanceof Constant<?>;
    boolean rightCollectionConst =
        rightConst && Collection.class.isAssignableFrom(expr.getArg(constIndex).getType());

    if (rightCollectionConst) {
      @SuppressWarnings("unchecked")
      Collection<?> values =
          ((Constant<? extends Collection<?>>) expr.getArg(constIndex)).getConstant();
      return new Document(asDBKey(expr, exprIndex), new Document("$in", values));
    } else if (rightConst) {
      Path<?> path = (Path<?>) expr.getArg(exprIndex);
      Constant<?> constant = (Constant<?>) expr.getArg(constIndex);
      Object convertedValue = convert(path, constant);
      return new Document(asDBKey(expr, exprIndex), convertedValue);
    } else {
      Object rightField = MONGO_EXPR_SYMBOL + asDBKey(expr, constIndex);
      Object leftValue = expr.getArg(exprIndex).accept(this, null);
      Object leftField;

      if (Collection.class.isAssignableFrom(leftValue.getClass())) {
        leftField =
            ((Collection<?>) leftValue)
                .stream().map(v -> MONGO_EXPR_SYMBOL + v.toString()).toList();
      } else {
        leftField = List.of(MONGO_EXPR_SYMBOL + asDBKey(expr, exprIndex));
      }

      Document inCondition = new Document("$in", Arrays.asList(rightField, leftField));
      return new Document(MONGO_EXPRESSION, inCondition);
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

  @SuppressWarnings("unchecked")
  @Override
  public Object visit(Operation<?> expr, Void context) {
    var op = expr.getOperator();
    if (op == Ops.EQ) {
      if (expr.getArg(0) instanceof Operation) {
        Operation<?> lhs = (Operation<?>) expr.getArg(0);
        if (lhs.getOperator() == Ops.COL_SIZE || lhs.getOperator() == Ops.ARRAY_SIZE) {
          return asDocument(asDBKey(lhs, 0), asDocument("$size", asDBValue(expr, 1)));
        } else {
          throw new UnsupportedOperationException("Illegal operation " + expr);
        }
      } else if (expr.getArg(0) instanceof Path) {
        Path<?> path = (Path<?>) expr.getArg(0);
        Object constant = expr.getArg(1);
        if (constant instanceof Constant<?> constantValue) {
          return asDocument(asDBKey(expr, 0), convert(path, constantValue));
        } else {
          Object rightField = MONGO_EXPR_SYMBOL + asDBKey(expr, 1);
          Object leftField = MONGO_EXPR_SYMBOL + asDBKey(expr, 0);
          Document eqCondition = new Document("$eq", Arrays.asList(leftField, rightField));
          return new Document(MONGO_EXPRESSION, eqCondition);
        }
      }
    } else if (op == Ops.STRING_IS_EMPTY) {
      return asDocument(asDBKey(expr, 0), "");

    } else if (op == Ops.AND) {
      var pendingDocuments = collectConnectorArgs("$and", expr);
      List<Map<Object, Object>> unmergeableDocuments = new ArrayList<>();
      List<Map<Object, Object>> generatedDocuments = new ArrayList<>();

      while (!pendingDocuments.isEmpty()) {

        var lhs = pendingDocuments.poll();

        for (Map<Object, Object> rhs : pendingDocuments) {
          Set<Object> lhs2 = new LinkedHashSet<>(lhs.keySet());
          lhs2.retainAll(rhs.keySet());
          if (lhs2.isEmpty()) {
            lhs.putAll(rhs);
          } else {
            unmergeableDocuments.add(rhs);
          }
        }

        generatedDocuments.add(lhs);
        pendingDocuments = new LinkedList<>(unmergeableDocuments);
        unmergeableDocuments = new LinkedList<>();
      }

      return generatedDocuments.size() == 1
          ? generatedDocuments.get(0)
          : asDocument("$and", generatedDocuments);
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
        var arg = (Document) handle(expr.getArg(0));
        return negate(arg);
      }

    } else if (op == Ops.OR) {
      return asDocument("$or", collectConnectorArgs("$or", expr));

    } else if (op == Ops.NE) {
      Path<?> path = (Path<?>) expr.getArg(0);
      Object constant = expr.getArg(1);
      if (constant instanceof Constant<?> constantValue) {
        return asDocument(asDBKey(expr, 0), asDocument("$ne", convert(path, constantValue)));
      } else {
        Object rightField = MONGO_EXPR_SYMBOL + asDBKey(expr, 1);
        Object leftField = MONGO_EXPR_SYMBOL + asDBKey(expr, 0);
        Document eqCondition = new Document("$ne", Arrays.asList(leftField, rightField));
        return new Document(MONGO_EXPRESSION, eqCondition);
      }

    } else if (op == Ops.STARTS_WITH) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDocument(asDBKey(expr, 0), new BsonRegularExpression("^" + regexValue(expr, 1)));
      } else {
        return createRegexMatch(expr, false, STRING_MATCH_TYPE.STARTS_WITH);
      }

    } else if (op == Ops.STARTS_WITH_IC) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDocument(
            asDBKey(expr, 0), new BsonRegularExpression("^" + regexValue(expr, 1), "i"));
      } else {
        return createRegexMatch(expr, true, STRING_MATCH_TYPE.STARTS_WITH);
      }

    } else if (op == Ops.ENDS_WITH) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDocument(asDBKey(expr, 0), new BsonRegularExpression(regexValue(expr, 1) + "$"));
      } else {
        return createRegexMatch(expr, false, STRING_MATCH_TYPE.ENDS_WITH);
      }

    } else if (op == Ops.ENDS_WITH_IC) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDocument(
            asDBKey(expr, 0), new BsonRegularExpression(regexValue(expr, 1) + "$", "i"));
      } else {
        return createRegexMatch(expr, true, STRING_MATCH_TYPE.ENDS_WITH);
      }

    } else if (op == Ops.EQ_IGNORE_CASE) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDocument(
            asDBKey(expr, 0), new BsonRegularExpression("^" + regexValue(expr, 1) + "$", "i"));
      } else {
        return createRegexMatch(expr, true, STRING_MATCH_TYPE.EQUALS);
      }
    } else if (op == Ops.STRING_CONTAINS) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDocument(
            asDBKey(expr, 0), new BsonRegularExpression(".*" + regexValue(expr, 1) + ".*"));
      } else {
        return createRegexMatch(expr, false, STRING_MATCH_TYPE.CONTAINS);
      }

    } else if (op == Ops.STRING_CONTAINS_IC) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDocument(
            asDBKey(expr, 0), new BsonRegularExpression(".*" + regexValue(expr, 1) + ".*", "i"));
      } else {
        return createRegexMatch(expr, true, STRING_MATCH_TYPE.CONTAINS);
      }

    } else if (op == Ops.MATCHES) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDocument(
            asDBKey(expr, 0), new BsonRegularExpression(asDBValue(expr, 1).toString()));
      } else {
        return createRegexMatch(expr, false, STRING_MATCH_TYPE.MATCHES);
      }

    } else if (op == Ops.MATCHES_IC) {
      if (expr.getArg(1) instanceof Constant<?>) {
        return asDocument(
            asDBKey(expr, 0), new BsonRegularExpression(asDBValue(expr, 1).toString(), "i"));
      } else {
        return createRegexMatch(expr, true, STRING_MATCH_TYPE.MATCHES);
      }
    } else if (op == Ops.LIKE) {

      if (expr.getArg(1) instanceof Constant<?>) {
        var regex = ExpressionUtils.likeToRegex((Expression) expr.getArg(1)).toString();
        return asDocument(asDBKey(expr, 0), new BsonRegularExpression(regex));
      } else {
        return createRegexMatch(expr, false, STRING_MATCH_TYPE.MATCHES);
      }

    } else if (op == Ops.LIKE_IC) {
      if (expr.getArg(1) instanceof Constant<?>) {
        var regex = ExpressionUtils.likeToRegex((Expression) expr.getArg(1)).toString();
        return asDocument(asDBKey(expr, 0), new BsonRegularExpression(regex, "i"));
      } else {
        return createRegexMatch(expr, true, STRING_MATCH_TYPE.MATCHES);
      }

    } else if (op == Ops.BETWEEN) {
      return asDocumentOrExpressionBetween(expr);

    } else if (op == Ops.IN) {
      var constIndex = 0;
      var exprIndex = 1;
      if (expr.getArg(1) instanceof Constant<?>) {
        constIndex = 1;
        exprIndex = 0;
      }
      return asDocumentOrExpressionIn(expr, exprIndex, constIndex);

    } else if (op == Ops.NOT_IN) {
      var constIndex = 0;
      var exprIndex = 1;
      if (expr.getArg(1) instanceof Constant<?>) {
        constIndex = 1;
        exprIndex = 0;
      }
      return asDocumentOrExpressionNotIn(expr, exprIndex, constIndex);

    } else if (op == Ops.COL_IS_EMPTY) {
      List<Object> list = new ArrayList<>(2);
      list.add(asDocument(asDBKey(expr, 0), new ArrayList<>()));
      list.add(asDocument(asDBKey(expr, 0), asDocument("$exists", false)));
      return asDocument("$or", list);

    } else if (op == Ops.LT) {
      return asDocumentOrExpression("$lt", expr);

    } else if (op == Ops.GT) {
      return asDocumentOrExpression("$gt", expr);

    } else if (op == Ops.LOE) {
      return asDocumentOrExpression("$lte", expr);

    } else if (op == Ops.GOE) {
      return asDocumentOrExpression("$gte", expr);

    } else if (op == Ops.IS_NULL) {
      return asDocument(asDBKey(expr, 0), asDocument("$exists", false));

    } else if (op == Ops.IS_NOT_NULL) {
      return asDocument(asDBKey(expr, 0), asDocument("$exists", true));

    } else if (op == Ops.CONTAINS_KEY) {
      Path<?> path = (Path<?>) expr.getArg(0);
      Expression<?> key = expr.getArg(1);
      return asDocument(visit(path, context) + "." + key.toString(), asDocument("$exists", true));

    } else if (op == MongodbOps.NEAR) {
      return asDocument(asDBKey(expr, 0), asDocument("$near", asDBValue(expr, 1)));

    } else if (op == MongodbOps.NEAR_SPHERE) {
      return asDocument(asDBKey(expr, 0), asDocument("$nearSphere", asDBValue(expr, 1)));

    } else if (op == MongodbOps.ELEM_MATCH) {
      return asDocument(asDBKey(expr, 0), asDocument("$elemMatch", asDBValue(expr, 1)));
    } else if (op == MongodbOps.NO_MATCH) {
      return new Document("$where", new BsonJavaScript("function() { return false }"));
    } else if (op == Ops.SET) {
      return asDbList(expr);
    }

    throw new UnsupportedOperationException("Illegal operation " + expr);
  }

  private Object negate(Document arg) {
    List<Object> list = new ArrayList<>();
    for (Map.Entry<String, Object> entry : arg.entrySet()) {
      if (entry.getKey().equals("$or")) {
        list.add(asDocument("$nor", entry.getValue()));

      } else if (entry.getKey().equals("$and")) {
        List<Object> list2 = new ArrayList<>();
        for (Object o : ((Collection) entry.getValue())) {
          list2.add(negate((Document) o));
        }
        list.add(asDocument("$or", list2));

      } else if (entry.getValue() instanceof Pattern
          || entry.getValue() instanceof BsonRegularExpression) {
        list.add(asDocument(entry.getKey(), asDocument("$not", entry.getValue())));

      } else if (entry.getValue() instanceof Document) {
        list.add(negate(entry.getKey(), (Document) entry.getValue()));

      } else {
        list.add(asDocument(entry.getKey(), asDocument("$ne", entry.getValue())));
      }
    }
    return list.size() == 1 ? list.get(0) : asDocument("$or", list);
  }

  private Object negate(String key, Document value) {
    if (value.size() == 1) {
      return asDocument(key, asDocument("$not", value));

    } else {
      List<Object> list2 = new ArrayList<>();
      for (Map.Entry<String, Object> entry2 : value.entrySet()) {
        list2.add(
            asDocument(key, asDocument("$not", asDocument(entry2.getKey(), entry2.getValue()))));
      }
      return asDocument("$or", list2);
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

  private Queue<Map<Object, Object>> collectConnectorArgs(String operator, Operation<?> operation) {

    Queue<Map<Object, Object>> pendingDocuments = new LinkedList<>();
    for (Expression<?> exp : operation.getArgs()) {
      var document = (Map<Object, Object>) handle(exp);
      if (document.keySet().size() == 1 && document.containsKey(operator)) {
        pendingDocuments.addAll((Collection<Map<Object, Object>>) document.get(operator));
      } else {
        pendingDocuments.add(document);
      }
    }
    return pendingDocuments;
  }
}
