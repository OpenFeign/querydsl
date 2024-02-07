/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.core.types.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import org.jetbrains.annotations.Nullable;
import org.junit.Ignore;
import org.junit.Test;

public class BeanPathTest {

  public static class SubClass extends BeanPathTest {}

  public static class MyBeanPath extends BeanPath<BeanPathTest> {

    private static final long serialVersionUID = 6225684967115368814L;

    public MyBeanPath(PathMetadata metadata) {
      super(BeanPathTest.class, metadata);
    }

    public MyBeanPath(PathMetadata metadata, @Nullable PathInits inits) {
      super(BeanPathTest.class, metadata);
    }
  }

  private BeanPath<BeanPathTest> beanPath = new BeanPath<BeanPathTest>(BeanPathTest.class, "p");

  @Test
  public void as_path() {
    SimplePath<BeanPathTest> simplePath = new SimplePath<BeanPathTest>(BeanPathTest.class, "p");
    assertThat(beanPath.as(simplePath)).isNotNull();
  }

  @Test
  @Ignore
  public void as_class() {
    MyBeanPath otherPath = beanPath.as(MyBeanPath.class);
    assertThat(otherPath).isEqualTo(beanPath);
    assertThat(otherPath.getMetadata().isRoot()).isTrue();
  }

  @Test
  public void as_class_cached() {
    MyBeanPath otherPath = beanPath.as(MyBeanPath.class);
    //        assertEquals(beanPath, otherPath);
    assertThat(otherPath == beanPath.as(MyBeanPath.class)).isTrue();
  }

  @Test
  @Ignore
  public void as_class_with_inits() {
    beanPath =
        new BeanPath<BeanPathTest>(
            BeanPathTest.class, PathMetadataFactory.forVariable("p"), PathInits.DEFAULT);
    MyBeanPath otherPath = beanPath.as(MyBeanPath.class);
    assertThat(otherPath).isEqualTo(beanPath);
  }

  @Test
  public void as_class_with_inits_cached() {
    beanPath =
        new BeanPath<BeanPathTest>(
            BeanPathTest.class, PathMetadataFactory.forVariable("p"), PathInits.DEFAULT);
    MyBeanPath otherPath = beanPath.as(MyBeanPath.class);
    //        assertEquals(beanPath, otherPath);
    assertThat(otherPath == beanPath.as(MyBeanPath.class)).isTrue();
  }

  @Test
  public void createEnum() {
    assertThat(beanPath.createEnum("property", PropertyType.class)).isNotNull();
  }

  @Test
  public void instanceOf() {
    assertThat(beanPath.instanceOf(BeanPathTest.class)).isNotNull();
  }

  @Test
  public void instanceOfAny() {
    BooleanExpression pred1 =
        beanPath.instanceOf(BeanPathTest.class).or(beanPath.instanceOf(SubClass.class));
    BooleanExpression pred2 = beanPath.instanceOfAny(BeanPathTest.class, SubClass.class);
    assertThat(pred2).isEqualTo(pred1);
    assertThat(pred2.toString())
        .isEqualTo(
            """
            p instanceof class com.querydsl.core.types.dsl.BeanPathTest || \
            p instanceof class com.querydsl.core.types.dsl.BeanPathTest$SubClass\
            """);
  }
}
