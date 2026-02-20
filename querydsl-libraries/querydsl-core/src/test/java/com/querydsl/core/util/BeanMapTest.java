package com.querydsl.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BeanMapTest {

  private BeanMap beanMap;

  @BeforeEach
  void setUp() {
    beanMap = new BeanMap(new Entity());
  }

  @Test
  void size() {
    assertThat(beanMap.size()).isEqualTo(4);
  }

  @Test
  void clear() {
    beanMap.clear();
    assertThat(beanMap.size()).isEqualTo(4);
  }

  @Test
  void primitives() {
    beanMap.put("id", 5);
    assertThat(((Entity) beanMap.getBean()).getId()).isEqualTo(5);
  }

  @Test
  void beanMap() {
    assertThat(new BeanMap().size()).isZero();
  }

  @Test
  void beanMapObject() {
    assertThat(new BeanMap(new Entity()).size()).isEqualTo(4);
  }

  @Test
  void toString_() {
    assertThat(new BeanMap()).hasToString("BeanMap<null>");
  }

  @Test
  void clone_() throws Exception {
    assertThat(beanMap.clone()).isEqualTo(beanMap);
  }

  @Test
  void putAllWriteable() {}

  @Test
  void containsKeyString() {
    assertThat(beanMap).containsKey("id");
  }

  @Test
  void containsValueObject() {}

  @Test
  void getString() {
    beanMap.put("firstName", "John");
    assertThat(beanMap).containsEntry("firstName", "John");
  }

  @Test
  void keySet() {
    assertThat(beanMap.keySet())
        .hasSameElementsAs(new HashSet<>(Arrays.asList("id", "class", "firstName", "lastName")));
  }

  @Test
  void entrySet() {
    beanMap.put("firstName", "John");
    assertThat(beanMap.entrySet()).isNotEmpty();
  }

  @Test
  @Disabled
  void values() {
    beanMap.put("firstName", "John");
    assertThat(beanMap.values()).isEqualTo(Arrays.asList(0, null, Entity.class, "John"));
  }

  @Test
  void getType() {}

  @Test
  void getBean() {
    assertThat(beanMap.getBean().getClass()).isEqualTo(Entity.class);
  }

  @Test
  void setBean() {
    var entity = new Entity();
    beanMap.setBean(entity);
    assertThat(entity).isSameAs(beanMap.getBean());
  }
}
