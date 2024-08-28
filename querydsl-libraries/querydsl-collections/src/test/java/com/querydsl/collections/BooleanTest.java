package com.querydsl.collections;

import static com.querydsl.core.alias.Alias.$;
import static org.assertj.core.api.Assertions.assertThat;

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
    var entity = Alias.alias(Entity.class);
    assertThat(
            CollQueryFactory.from(entity, Collections.singleton(new Entity()))
                .where($(entity.isBoolean1()).eq(Boolean.TRUE))
                .fetchCount())
        .isEqualTo(1);
  }

  @Test
  public void object_boolean() {
    var entity = Alias.alias(Entity.class);
    assertThat(
            CollQueryFactory.from(entity, Collections.singleton(new Entity()))
                .where($(entity.getBoolean2()).eq(Boolean.TRUE))
                .fetchCount())
        .isEqualTo(1);
  }
}
