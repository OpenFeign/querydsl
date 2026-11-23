package com.querydsl.core.types;

import com.querydsl.core.JoinExpression;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public final class ExpressionAdapter {

  @SuppressWarnings("unchecked")
  public static <T> fluentq.core.types.Expression<T> adapt(Expression<T> expr) {
    if (expr == null) {
      return null;
    }
    if (expr instanceof fluentq.core.types.Expression) {
      return (fluentq.core.types.Expression<T>) expr;
    }
    if (expr instanceof Path) {
      return (fluentq.core.types.Expression<T>) new PathAdapter<>((Path<T>) expr);
    }
    if (expr instanceof Constant) {
      return (fluentq.core.types.Expression<T>) new ConstantAdapter<>((Constant<T>) expr);
    }
    if (expr instanceof Operation) {
      return (fluentq.core.types.Expression<T>) new OperationAdapter<>((Operation<T>) expr);
    }
    if (expr instanceof TemplateExpression) {
      return (fluentq.core.types.Expression<T>)
          new TemplateExpressionAdapter<>((TemplateExpression<T>) expr);
    }
    if (expr instanceof FactoryExpression) {
      return (fluentq.core.types.Expression<T>)
          new FactoryExpressionAdapter<>((FactoryExpression<T>) expr);
    }
    if (expr instanceof SubQueryExpression) {
      return (fluentq.core.types.Expression<T>)
          new SubQueryExpressionAdapter<>((SubQueryExpression<T>) expr);
    }
    if (expr instanceof ParamExpression) {
      return (fluentq.core.types.Expression<T>)
          new ParamExpressionAdapter<>((ParamExpression<T>) expr);
    }

    // Fallback generic expression wrapper
    return new GenericExpressionAdapter<>(expr);
  }

  public static Object adaptObject(Object arg) {
    if (arg instanceof Expression) {
      return adapt((Expression<?>) arg);
    }
    return arg;
  }

  private static class AdaptedOperator implements fluentq.core.types.Operator {
    private final String name;
    private final Class<?> type;

    AdaptedOperator(String name, Class<?> type) {
      this.name = name;
      this.type = type;
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public Class<?> getType() {
      return type;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!(obj instanceof fluentq.core.types.Operator)) return false;
      return name.equals(((fluentq.core.types.Operator) obj).name());
    }

    @Override
    public int hashCode() {
      return name.hashCode();
    }
  }

  private static fluentq.core.QueryModifiers adaptModifiers(QueryModifiers modifiers) {
    if (modifiers == null) return null;
    return new fluentq.core.QueryModifiers(modifiers.getLimit(), modifiers.getOffset());
  }

  private static fluentq.core.JoinType adaptJoinType(JoinType type) {
    if (type == null) return null;
    return fluentq.core.JoinType.valueOf(type.name());
  }

  private static fluentq.core.JoinFlag.Position adaptJoinFlagPosition(JoinFlag.Position pos) {
    if (pos == null) return null;
    return fluentq.core.JoinFlag.Position.valueOf(pos.name());
  }

  private static fluentq.core.QueryFlag.Position adaptQueryFlagPosition(QueryFlag.Position pos) {
    if (pos == null) return null;
    return fluentq.core.QueryFlag.Position.valueOf(pos.name());
  }

  private static fluentq.core.types.OrderSpecifier<?> adaptOrderSpecifier(OrderSpecifier<?> spec) {
    if (spec == null) return null;
    return new fluentq.core.types.OrderSpecifier<>(
        adaptOrder(spec.getOrder()),
        adapt(spec.getTarget()),
        adaptNullHandling(spec.getNullHandling()));
  }

  private static fluentq.core.types.Order adaptOrder(Order order) {
    if (order == null) return null;
    return fluentq.core.types.Order.valueOf(order.name());
  }

  private static fluentq.core.types.OrderSpecifier.NullHandling adaptNullHandling(
      OrderSpecifier.NullHandling nh) {
    if (nh == null) return null;
    return fluentq.core.types.OrderSpecifier.NullHandling.valueOf(nh.name());
  }

  @SuppressWarnings("unchecked")
  public static fluentq.core.QueryMetadata adaptQueryMetadata(QueryMetadata meta) {
    if (meta == null) return null;
    fluentq.core.DefaultQueryMetadata result = new fluentq.core.DefaultQueryMetadata();

    if (meta.getWhere() != null) {
      result.addWhere((fluentq.core.types.Predicate) adapt(meta.getWhere()));
    }
    for (Expression<?> g : meta.getGroupBy()) {
      result.addGroupBy(adapt(g));
    }
    if (meta.getHaving() != null) {
      result.addHaving((fluentq.core.types.Predicate) adapt(meta.getHaving()));
    }
    for (OrderSpecifier<?> o : meta.getOrderBy()) {
      result.addOrderBy(adaptOrderSpecifier(o));
    }
    if (meta.getProjection() != null) {
      result.setProjection(adapt(meta.getProjection()));
    }
    result.setModifiers(adaptModifiers(meta.getModifiers()));

    for (JoinExpression join : meta.getJoins()) {
      result.addJoin(adaptJoinType(join.getType()), adapt(join.getTarget()));
      for (JoinFlag flag : join.getFlags()) {
        result.addJoinFlag(
            new fluentq.core.JoinFlag(
                adapt(flag.getFlag()), adaptJoinFlagPosition(flag.getPosition())));
      }
    }
    for (QueryFlag flag : meta.getFlags()) {
      result.addFlag(
          new fluentq.core.QueryFlag(
              adaptQueryFlagPosition(flag.getPosition()), adapt(flag.getFlag())));
    }

    for (Map.Entry<ParamExpression<?>, ?> entry : meta.getParams().entrySet()) {
      result.setParam((fluentq.core.types.ParamExpression) adapt(entry.getKey()), entry.getValue());
    }

    result.setDistinct(meta.isDistinct());
    result.setUnique(meta.isUnique());

    return result;
  }

  private static class GenericExpressionAdapter<T> implements fluentq.core.types.Expression<T> {
    protected final Expression<T> delegate;

    GenericExpressionAdapter(Expression<T> delegate) {
      this.delegate = delegate;
    }

    @Override
    public Class<? extends T> getType() {
      return delegate.getType();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
      if (v instanceof Visitor) {
        return delegate.accept((Visitor<R, C>) v, context);
      }
      throw new UnsupportedOperationException(
          "Unknown expression type: " + delegate.getClass().getName());
    }

    @Override
    public String toString() {
      return delegate.toString();
    }
  }

  private static class PathAdapter<T> extends GenericExpressionAdapter<T>
      implements fluentq.core.types.Path<T> {
    private final Path<T> path;

    PathAdapter(Path<T> path) {
      super(path);
      this.path = path;
    }

    @Override
    public fluentq.core.types.PathMetadata getMetadata() {
      PathMetadata meta = path.getMetadata();
      if (meta == null) return null;
      return new fluentq.core.types.PathMetadata(
          (fluentq.core.types.Path<?>) adapt(meta.getParent()),
          meta.getElement(),
          adaptPathType(meta.getPathType()));
    }

    @Override
    public AnnotatedElement getAnnotatedElement() {
      return path.getAnnotatedElement();
    }

    @Override
    public fluentq.core.types.Path<?> getRoot() {
      return (fluentq.core.types.Path<?>) adapt(path.getRoot());
    }

    @Override
    public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
      return v.visit(this, context);
    }
  }

  private static class ConstantAdapter<T> extends GenericExpressionAdapter<T>
      implements fluentq.core.types.Constant<T> {
    private final Constant<T> constant;

    ConstantAdapter(Constant<T> constant) {
      super(constant);
      this.constant = constant;
    }

    @Override
    public T getConstant() {
      return constant.getConstant();
    }

    @Override
    public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
      return v.visit(this, context);
    }
  }

  private static class OperationAdapter<T> extends GenericExpressionAdapter<T>
      implements fluentq.core.types.Operation<T> {
    private final Operation<T> op;

    OperationAdapter(Operation<T> op) {
      super(op);
      this.op = op;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<fluentq.core.types.Expression<?>> getArgs() {
      List<fluentq.core.types.Expression<?>> result = new ArrayList<>();
      for (Expression<?> arg : op.getArgs()) {
        result.add((fluentq.core.types.Expression<?>) adapt((Expression) arg));
      }
      return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public fluentq.core.types.Expression<?> getArg(int index) {
      return adapt((Expression) op.getArg(index));
    }

    @Override
    public fluentq.core.types.Operator getOperator() {
      Operator operator = op.getOperator();
      if (operator == null) return null;
      return new AdaptedOperator(operator.name(), operator.getType());
    }

    @Override
    public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
      return v.visit(this, context);
    }
  }

  private static class TemplateExpressionAdapter<T> extends GenericExpressionAdapter<T>
      implements fluentq.core.types.TemplateExpression<T> {
    private final TemplateExpression<T> template;

    TemplateExpressionAdapter(TemplateExpression<T> template) {
      super(template);
      this.template = template;
    }

    @Override
    public List<?> getArgs() {
      List<Object> result = new ArrayList<>();
      for (Object arg : template.getArgs()) {
        result.add(adaptObject(arg));
      }
      return result;
    }

    @Override
    public Object getArg(int index) {
      return adaptObject(template.getArg(index));
    }

    @Override
    public fluentq.core.types.Template getTemplate() {
      Template t = template.getTemplate();
      if (t == null) return null;
      return fluentq.core.types.TemplateFactory.DEFAULT.create(t.toString());
    }

    @Override
    public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
      return v.visit(this, context);
    }
  }

  private static class FactoryExpressionAdapter<T> extends GenericExpressionAdapter<T>
      implements fluentq.core.types.FactoryExpression<T> {
    private final FactoryExpression<T> factory;

    FactoryExpressionAdapter(FactoryExpression<T> factory) {
      super(factory);
      this.factory = factory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<fluentq.core.types.Expression<?>> getArgs() {
      List<fluentq.core.types.Expression<?>> result = new ArrayList<>();
      for (Expression<?> arg : factory.getArgs()) {
        result.add((fluentq.core.types.Expression<?>) adapt((Expression) arg));
      }
      return result;
    }

    @Override
    @Nullable
    public T newInstance(Object... args) {
      return factory.newInstance(args);
    }

    @Override
    public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
      return v.visit(this, context);
    }
  }

  private static class SubQueryExpressionAdapter<T> extends GenericExpressionAdapter<T>
      implements fluentq.core.types.SubQueryExpression<T> {
    private final SubQueryExpression<T> sub;

    SubQueryExpressionAdapter(SubQueryExpression<T> sub) {
      super(sub);
      this.sub = sub;
    }

    @Override
    public fluentq.core.QueryMetadata getMetadata() {
      return adaptQueryMetadata(sub.getMetadata());
    }

    @Override
    public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
      return v.visit(this, context);
    }
  }

  private static class ParamExpressionAdapter<T> extends GenericExpressionAdapter<T>
      implements fluentq.core.types.ParamExpression<T> {
    private final ParamExpression<T> param;

    ParamExpressionAdapter(ParamExpression<T> param) {
      super(param);
      this.param = param;
    }

    @Override
    public String getName() {
      return param.getName();
    }

    @Override
    public boolean isAnon() {
      return param.isAnon();
    }

    @Override
    public String getNotSetMessage() {
      return param.getNotSetMessage();
    }

    @Override
    public <R, C> R accept(fluentq.core.types.Visitor<R, C> v, @Nullable C context) {
      return v.visit(this, context);
    }
  }

  private static fluentq.core.types.PathType adaptPathType(PathType type) {
    if (type == null) return null;
    try {
      return fluentq.core.types.PathType.valueOf(type.name());
    } catch (Exception e) {
      return null;
    }
  }
}
