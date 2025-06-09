package com.querydsl.core.types.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Visitor;
import java.util.List;
import java.util.function.Function;

/**
 * A {@link FactoryExpression} implementation that wraps a single source {@link Expression} and
 * converts its result into a custom target type.
 *
 * <p>Typical usage: convert a {@link java.math.BigDecimal} sum result from a JPA query into a
 * domain-specific value object such as {@code Money}.
 *
 * @param <S> the source expression type (e.g. {@link java.math.BigDecimal})
 * @param <T> the target type to convert to (e.g. a custom {@code Money} class)
 * @author chadongmin
 * @see com.querydsl.core.types.FactoryExpression
 * @since 6.11
 */
public class TypeWrapper<S, T> implements FactoryExpression<T> {
  private final Class<T> valueClass;
  private final Function<S, T> factory;
  private final List<Expression<?>> args;

  /**
   * Create a new TypeWrapper.
   *
   * @param arg the source expression whose value will be converted
   * @param valueClass the target type class
   * @param factory a function that maps the source value to the target type
   */
  public TypeWrapper(Expression<S> arg, Class<T> valueClass, Function<S, T> factory) {
    this.valueClass = valueClass;
    this.factory = factory;
    this.args = List.of(arg);
  }

  @Override
  public <R, C> R accept(Visitor<R, C> v, C context) {
    return v.visit(this, context);
  }

  @Override
  public Class<? extends T> getType() {
    return valueClass;
  }

  @Override
  public List<Expression<?>> getArgs() {
    return args;
  }

  @Override
  public T newInstance(Object... args) {
    @SuppressWarnings("unchecked")
    S arg = (S) args[0];
    return factory.apply(arg);
  }
}
