package com.querydsl.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BeanMapTest {

  private BeanMap beanMap;

  @Before
  public void setUp() {
    beanMap = new BeanMap(new Entity());
  }

  @Test
  public void size() {
    assertThat(beanMap.size()).isEqualTo(4);
  }

  @Test
  public void clear() {
    beanMap.clear();
    assertThat(beanMap.size()).isEqualTo(4);
  }

  @Test
  public void primitives() {
    beanMap.put("id", 5);
    assertThat(((Entity) beanMap.getBean()).getId()).isEqualTo(5);
  }

  @Test
  public void beanMap() {
    assertThat(new BeanMap().size()).isEqualTo(0);
  }

  @Test
  public void beanMapObject() {
    assertThat(new BeanMap(new Entity()).size()).isEqualTo(4);
  }

  @Test
  public void toString_() {
    assertThat(new BeanMap()).hasToString("BeanMap<null>");
  }

  @Test
  public void clone_() throws CloneNotSupportedException {
    assertThat(beanMap.clone()).isEqualTo(beanMap);
  }

  @Test
  public void putAllWriteable() {}

  @Test
  public void containsKeyString() {
    assertThat(beanMap.containsKey("id")).isTrue();
  }

  @Test
  public void containsValueObject() {}

  @Test
  public void getString() {
    beanMap.put("firstName", "John");
    assertThat(beanMap.get("firstName")).isEqualTo("John");
  }

  @Test
  public void keySet() {
    assertThat(beanMap.keySet())
        .isEqualTo(new HashSet<>(Arrays.asList("id", "class", "firstName", "lastName")));
  }

  @Test
  public void entrySet() {
    beanMap.put("firstName", "John");
    assertThat(beanMap.entrySet()).isNotEmpty();
  }

  @Test
  @Ignore
  public void values() {
    beanMap.put("firstName", "John");
    assertThat(beanMap.values()).isEqualTo(Arrays.asList(0, null, Entity.class, "John"));
  }

  @Test
  public void getType() {}

  @Test
  public void getBean() {
    assertThat(beanMap.getBean().getClass()).isEqualTo(Entity.class);
  }

  @Test
  public void setBean() {
    var entity = new Entity();
    beanMap.setBean(entity);
    assertThat(entity == beanMap.getBean()).isTrue();
  }
}
