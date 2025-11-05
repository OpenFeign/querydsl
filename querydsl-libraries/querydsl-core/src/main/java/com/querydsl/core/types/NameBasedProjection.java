package com.querydsl.core.types;

import com.querydsl.core.types.dsl.EntityPathBase;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameBasedProjection<T> extends FactoryExpressionBase<T> {

  private final List<Expression<?>> expressions;
  private final Constructor<? extends T> constructor;

  @SafeVarargs
  public NameBasedProjection(Class<? extends T> type, EntityPathBase<?>... entities) {
    super(type);
    Map<String, Expression<?>> exprByName = collectExpressions(entities);
    this.constructor = findMatchingConstructor(type, exprByName);
    this.expressions = buildArgsForConstructor(this.constructor, exprByName);
  }

  private Map<String, Expression<?>> collectExpressions(EntityPathBase<?>... entities) {
      Map<String, Expression<?>> map = new HashMap<>();
      for (EntityPathBase<?> entity : entities) {
          Class<?> clazz = entity.getClass();
          for (Field f : clazz.getFields()){
              String name = f.getName();
              if(!map.containsKey(name)){
                  try{
                      Object val = f.get(entity);
                      if(val instanceof Expression){
                          map.put(name, (Expression<?>) val);
                      }
                  }catch (IllegalAccessException e){

                  }
              }
          }
      }
      return map;
  }



  private Constructor<? extends T> findMatchingConstructor(
            Class<? extends T> type,
            Map<String, Expression<?>> exprByName
    ) {
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
                if(fallback == null || ctor.getParameterCount() > fallback.getParameterCount()){
                    fallback = ctor;
                }
            }
        }

        if(fallback != null){
            fallback.setAccessible(true);
            return (Constructor<? extends T>) fallback;
        }

        throw new RuntimeException("No constructor in " + type.getSimpleName()
                + " can be satisfied by entities: " + exprByName.keySet());
  }

  private List<Expression<?>> buildArgsForConstructor(
            Constructor<? extends T> ctor,
            Map<String, Expression<?>> exprByName
    ) {
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

  public static <T> NameBasedProjection<T> binder(
      Class<? extends T> type, EntityPathBase<?> entity) {
    return new NameBasedProjection<>(type, entity);
  }
}
