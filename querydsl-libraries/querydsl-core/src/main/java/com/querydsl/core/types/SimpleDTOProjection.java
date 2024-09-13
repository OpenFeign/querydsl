package com.querydsl.core.types;

import com.querydsl.core.types.dsl.EntityPathBase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SimpleDTOProjection<T> extends FactoryExpressionBase<T> {
    private final List<Expression<?>> expressions; // 일치하는 필드만 저장하기위한 리스트
    private final Constructor<? extends T> constructor; // DTO의 생성자를 불러오기위한 것

    public SimpleDTOProjection(Class<? extends T> type, EntityPathBase<?> entity) {
        super(type);
        this.expressions = generateExpressions(type, entity);
        this.constructor = findMatchingConstructor(type);
    }
    // Expression은 QueryDSL에서 필드를 이렇게 선언함 Q클래스 그래서 Q클래스의 배열타입이 Expression이다.
    private List<Expression<?>> generateExpressions(Class<? extends T> dtoType, EntityPathBase<?> entity) {
        List<Expression<?>> expressions = new ArrayList<>();
        for (Field field : dtoType.getDeclaredFields()) { //dtoType.getDeclaredFields() 해당 클래스에 직접 선언된 모든 필드(public, protected, default, private)를 반환
            String fieldName = field.getName();
            try {
                Field entityField = entity.getClass().getField(fieldName);
                if (entityField != null) {
                    Object value = entityField.get(entity);
                    if (value instanceof Expression<?>) {
                        expressions.add((Expression<?>) value);
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                // Ignore if the field is not present in the entity
            }
        }
        return expressions;
    }

    // 필드 이름이 일치하는 갯수만큼의 파라미터로 이루어진 생성자를 찾아서 리턴한다.
    private Constructor<? extends T> findMatchingConstructor(Class<? extends T> type) {
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            if (constructor.getParameterTypes().length == expressions.size()) {
                return (Constructor<? extends T>) constructor;
            }
        }
        throw new RuntimeException("No matching constructor found for " + type.getSimpleName());
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

    // 제네릭으로 호출 시점에 구체적인 타입 명
    public static <T> SimpleDTOProjection<T> fields(Class<? extends T> type, EntityPathBase<?> entity) {
        return new SimpleDTOProjection<>(type, entity);
    }
}