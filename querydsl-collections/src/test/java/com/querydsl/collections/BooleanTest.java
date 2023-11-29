package com.querydsl.collections;

import static com.querydsl.core.alias.Alias.$;
import static org.junit.Assert.assertEquals;

import com.querydsl.core.alias.Alias;
import java.util.Collections;
import org.junit.Test;

public class BooleanTest {

  public static class Entity {

    private boolean boolean1 = true;

    private Boolean boolean2 = Boolean.TRUE;

    public boolean isBoolean1() {
      return boolean1;
    }

    public Boolean getBoolean2() {
      return boolean2;
    }
  }

  @Test
  public void primitive_boolean() {
    Entity entity = Alias.alias(Entity.class);
    assertEquals(
        1,
        CollQueryFactory.from(entity, Collections.singleton(new Entity()))
            .where($(entity.isBoolean1()).eq(Boolean.TRUE))
            .fetchCount());
  }

  @Test
  public void object_boolean() {
    Entity entity = Alias.alias(Entity.class);
    assertEquals(
        1,
        CollQueryFactory.from(entity, Collections.singleton(new Entity()))
            .where($(entity.getBoolean2()).eq(Boolean.TRUE))
            .fetchCount());
  }
}
