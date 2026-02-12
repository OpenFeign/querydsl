package com.querydsl.core.support;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.EntityPathBase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class QueryMixinPerformanceTest {

  public static final int iterations = 2000000;

  @Test
  void normal() { // 1791
    EntityPath<DummyEntity> entity = new EntityPathBase<>(DummyEntity.class, "entity");
    var other =
        new EntityPathBase<>(DummyEntity.class, PathMetadataFactory.forProperty(entity, "other"));

    var start = System.currentTimeMillis();
    for (var i = 0; i < iterations; i++) {
      QueryMixin<?> mixin = new QueryMixin<Void>();
      mixin.from(entity);
      mixin.where(other.eq(new DummyEntity()));
      mixin.setProjection(entity);
    }
    System.err.println(System.currentTimeMillis() - start);
  }

  @Test
  void array_arguments() { // 2260
    EntityPath<DummyEntity> entity = new EntityPathBase<>(DummyEntity.class, "entity");
    var entities = new EntityPath[] {entity};
    var other =
        new EntityPathBase<>(DummyEntity.class, PathMetadataFactory.forProperty(entity, "other"));

    var start = System.currentTimeMillis();
    for (var i = 0; i < iterations; i++) {
      QueryMixin<?> mixin = new QueryMixin<Void>();
      mixin.from(entities);
      mixin.where(other.eq(new DummyEntity()));
      mixin.setProjection(entity);
    }
    System.err.println(System.currentTimeMillis() - start);
  }
}
