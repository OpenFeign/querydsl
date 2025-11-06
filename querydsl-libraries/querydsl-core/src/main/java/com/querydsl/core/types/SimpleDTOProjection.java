package com.querydsl.core.types;

import com.querydsl.core.types.dsl.EntityPathBase;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleDTOProjection<T> extends FactoryExpressionBase<T> {

  private final List<Expression<?>> expressions;
  private final Constructor<? extends T> constructor;

  @SafeVarargs
  public SimpleDTOProjection(Class<? extends T> type, EntityPathBase<?>... entities) {
    super(type);
    Map<String, Expression<?>> exprByName = collectExpressions(entities);
    this.constructor = findMatchingConstructor(type, exprByName);
    this.expressions = buildArgsForConstructor(this.constructor, exprByName);
  }

  private Map<String, Expression<?>> collectExpressions(EntityPathBase<?>... entities) {
    Map<String, Expression<?>> map = new HashMap<>();
    for (EntityPathBase<?> entity : entities) {
      Class<?> clazz = entity.getClass();
      for (Field f : clazz.getFields()) {
        String name = f.getName();
        if (!map.containsKey(name)) {
          try {
            Object val = f.get(entity);
            if (val instanceof Expression) {
              map.put(name, (Expression<?>) val);
            }
          } catch (IllegalAccessException e) {

          }
        }
      }
    }
    return map;
  }

  private Constructor<? extends T> findMatchingConstructor(
      Class<? extends T> type, Map<String, Expression<?>> exprByName) {
    Constructor<?>[] ctors = type.getDeclaredConstructors();

    for (Constructor<?> ctor : ctors) {
      Parameter[] params = ctor.getParameters();
      if (params.length == 0) continue;
      boolean allMatch = true;
      for (Parameter p : params) {
        String paramName = p.getName();
        if (!exprByName.containsKey(paramName)) {
          allMatch = false;
          break;
        }
      }
      if (allMatch) {
        ctor.setAccessible(true);
        return (Constructor<? extends T>) ctor;
      }
    }

    Constructor<?> fallback = null;
    for (Constructor<?> ctor : ctors) {
      int paramCount = ctor.getParameterCount();
      if (paramCount > 0 && paramCount <= exprByName.size()) {
        if (fallback == null || ctor.getParameterCount() > fallback.getParameterCount()) {
          fallback = ctor;
        }
      }
    }

    if (fallback != null) {
      fallback.setAccessible(true);
      return (Constructor<? extends T>) fallback;
    }

    throw new RuntimeException(
        "No constructor in "
            + type.getSimpleName()
            + " can be satisfied by entities: "
            + exprByName.keySet());
  }

  private List<Expression<?>> buildArgsForConstructor(
      Constructor<? extends T> ctor, Map<String, Expression<?>> exprByName) {
    List<Expression<?>> list = new ArrayList<>();

    Parameter[] params = ctor.getParameters();

    boolean unnamed = params.length > 0 && params[0].getName().startsWith("arg");
    Field[] fields = unnamed ? ctor.getDeclaringClass().getDeclaredFields() : new Field[0];

    for (int i = 0; i < params.length; i++) {
      String name = unnamed ? fields[i].getName() : params[i].getName();
      Expression<?> expr = exprByName.get(name);
      if (expr == null) {
        throw new RuntimeException("No expression for parameter: " + name);
      }
      list.add(expr);
    }
    return list;
  }

  @Override
  public T newInstance(Object... args) {
    try {
      return constructor.newInstance(args);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create instance of " + getType().getSimpleName(), e);
    }
  }

  @Override
  public List<Expression<?>> getArgs() {
    return expressions;
  }

  @Override
  public <R, C> R accept(Visitor<R, C> v, C context) {
    return null;
  }

  /**
   * Creates a {@link SimpleDTOProjection} instance for the given DTO class and entity.
   *
   * <p>Author: <b>Mouon (munhyunjune)</b>
   *
   * <h3>Purpose</h3>
   *
   * Simplifies QueryDSL projection by automatically binding entity fields to DTO constructor or
   * field names. This eliminates the need to manually specify every field in {@code
   * Projections.fields()} or {@code Projections.constructor()}, significantly reducing boilerplate
   * code.
   *
   * <h3>Usage Example</h3>
   *
   * <pre>{@code
   * QAnimal animal = QAnimal.animal;
   *
   * // Instead of writing:
   * Projections.fields(AnimalDTO.class, animal.id, animal.name, animal.weight)
   *
   * // You can simply write:
   * AnimalDTO dto = NameBasedProjection.binder(AnimalDTO.class, animal)
   *     .newInstance(1, "Lion", 120);
   * }</pre>
   *
   * <h3>Parameters</h3>
   *
   * <ul>
   *   <li><b>type</b> — The DTO class type to project into.
   *   <li><b>entity</b> — The QueryDSL {@link EntityPathBase} whose fields will be automatically
   *       mapped.
   * </ul>
   *
   * <h3>Return</h3>
   *
   * A new {@link SimpleDTOProjection} instance capable of instantiating the given DTO type using
   * the entity's expressions.
   *
   * <h3>Matching Logic</h3>
   *
   * <ul>
   *   <li>DTO constructor parameters are matched by <b>name</b> (not by order).
   *   <li>Internally, {@code Map<String, Expression<?>> exprByName} is used to pair entity field
   *       names with corresponding constructor parameter names.
   *   <li>If compiled without {@code -parameters}, parameter names default to {@code arg0, arg1,
   *       ...}, so the class fields’ order will be used as a fallback.
   * </ul>
   *
   * <h3>Caution</h3>
   *
   * <ul>
   *   <li>The DTO must have either:
   *       <ul>
   *         <li>a constructor whose parameter names match entity field names, or
   *         <li>accessible fields whose names match entity field names.
   *       </ul>
   *   <li>If there is no matching field or constructor parameter for an entity expression, a {@link
   *       RuntimeException} will be thrown.
   *   <li>When using multiple entities (e.g. {@code new NameBasedProjection<>(Dto.class, user,
   *       order)}), duplicate field names are resolved by the <b>first</b> entity provided.
   * </ul>
   *
   * @param type the DTO class type to bind expressions to
   * @param entity the QueryDSL entity path whose field expressions are used
   * @param <T> the generic DTO type
   * @return a new {@link SimpleDTOProjection} ready to create DTO instances
   */
  public static <T> SimpleDTOProjection<T> binder(
      Class<? extends T> type, EntityPathBase<?> entity) {
    return new SimpleDTOProjection<>(type, entity);
  }
}
