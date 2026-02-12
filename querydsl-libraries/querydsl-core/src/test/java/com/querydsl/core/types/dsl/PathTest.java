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

import static com.querydsl.core.alias.Alias.$;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.alias.Alias;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryTransient;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.Templates;
import com.querydsl.core.types.ToStringVisitor;
import com.querydsl.core.util.Annotations;
import java.lang.reflect.Field;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

class PathTest {

  enum ExampleEnum {
    A,
    B
  }

  public static class Superclass {

    @Nullable
    public String getProperty4() {
      return null;
    }
  }

  @QueryEntity
  public static class Entity extends Superclass {

    @Nullable private String property1;

    private String property2;

    @QueryTransient private String property3;

    public String getProperty1() {
      return property1;
    }

    @NotNull
    public String getProperty2() {
      return property2;
    }

    @NotNull
    public String getProperty3() {
      return property3;
    }
  }

  @Test
  void getAnnotatedElement() {
    var entity = Alias.alias(Entity.class);
    var element = $(entity).getAnnotatedElement();

    // type
    assertThat(element).isEqualTo(Entity.class);
  }

  @Test
  void getAnnotatedElement_for_property() {
    var entity = Alias.alias(Entity.class);
    var property1 = $(entity.getProperty1()).getAnnotatedElement();
    var property2 = $(entity.getProperty2()).getAnnotatedElement();
    var property3 = $(entity.getProperty3()).getAnnotatedElement();
    var property4 = $(entity.getProperty4()).getAnnotatedElement();

    // property (field)
    assertThat(property1.getClass()).isEqualTo(Annotations.class);

    // property2 (method)
    assertThat(property2.getClass()).isEqualTo(Annotations.class);

    // property3 (both)
    assertThat(property3.getClass()).isEqualTo(Field.class);
    assertThat(property3.isAnnotationPresent(QueryTransient.class)).isTrue();
    assertThat(property3.getAnnotation(QueryTransient.class)).isNotNull();

    // property 4 (superclass)
    assertThat(property4.getClass()).isEqualTo(Annotations.class);
  }

  @SuppressWarnings("unchecked")
  @Test
  void equals() {
    assertThat(new StringPath("s")).isEqualTo(new StringPath("s"));
    assertThat(new BooleanPath("b")).isEqualTo(new BooleanPath("b"));
    assertThat(new NumberPath<>(Integer.class, "n"))
        .isEqualTo(new NumberPath<>(Integer.class, "n"));

    assertThat(ExpressionUtils.path(String.class, "p"))
        .isEqualTo(new ArrayPath(String[].class, "p"));
    assertThat(ExpressionUtils.path(Boolean.class, "p")).isEqualTo(new BooleanPath("p"));
    assertThat(ExpressionUtils.path(String.class, "p"))
        .isEqualTo(new ComparablePath(String.class, "p"));
    assertThat(ExpressionUtils.path(Date.class, "p")).isEqualTo(new DatePath(Date.class, "p"));
    assertThat(ExpressionUtils.path(Date.class, "p")).isEqualTo(new DateTimePath(Date.class, "p"));
    assertThat(ExpressionUtils.path(ExampleEnum.class, "p"))
        .isEqualTo(new EnumPath(ExampleEnum.class, "p"));
    assertThat(ExpressionUtils.path(Integer.class, "p"))
        .isEqualTo(new NumberPath(Integer.class, "p"));
    assertThat(ExpressionUtils.path(String.class, "p")).isEqualTo(new StringPath("p"));
    assertThat(ExpressionUtils.path(Time.class, "p")).isEqualTo(new TimePath(Time.class, "p"));
  }

  @SuppressWarnings("unchecked")
  @Test
  void various_properties() {
    Path<?> parent = ExpressionUtils.path(Object.class, "parent");
    List<Path<?>> paths = new ArrayList<>();
    paths.add(new ArrayPath(String[].class, parent, "p"));
    paths.add(new BeanPath(Object.class, parent, "p"));
    paths.add(new BooleanPath(parent, "p"));
    paths.add(new CollectionPath(String.class, StringPath.class, parent, "p"));
    paths.add(new ComparablePath(String.class, parent, "p"));
    paths.add(new DatePath(Date.class, parent, "p"));
    paths.add(new DateTimePath(Date.class, parent, "p"));
    paths.add(new EnumPath(ExampleEnum.class, parent, "p"));
    paths.add(new ListPath(String.class, StringPath.class, parent, "p"));
    paths.add(new MapPath(String.class, String.class, StringPath.class, parent, "p"));
    paths.add(new NumberPath(Integer.class, parent, "p"));
    paths.add(new SetPath(String.class, StringPath.class, parent, "p"));
    paths.add(new SimplePath(String.class, parent, "p"));
    paths.add(new StringPath(parent, "p"));
    paths.add(new TimePath(Time.class, parent, "p"));

    for (Path<?> path : paths) {
      Path other =
          ExpressionUtils.path(path.getType(), PathMetadataFactory.forProperty(parent, "p"));
      assertThat(path.accept(ToStringVisitor.DEFAULT, Templates.DEFAULT))
          .isEqualTo(path.toString());
      assertThat(other).hasSameHashCodeAs(path).isEqualTo(path);
      assertThat(path.getMetadata()).isNotNull();
      assertThat(path.getType()).isNotNull();
      assertThat(path.getRoot()).isEqualTo(parent);
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  void various() {
    List<Path<?>> paths = new ArrayList<>();
    paths.add(new ArrayPath(String[].class, "p"));
    paths.add(new BeanPath(Object.class, "p"));
    paths.add(new BooleanPath("p"));
    paths.add(new CollectionPath(String.class, StringPath.class, "p"));
    paths.add(new ComparablePath(String.class, "p"));
    paths.add(new DatePath(Date.class, "p"));
    paths.add(new DateTimePath(Date.class, "p"));
    paths.add(new EnumPath(ExampleEnum.class, "p"));
    paths.add(new ListPath(String.class, StringPath.class, "p"));
    paths.add(new MapPath(String.class, String.class, StringPath.class, "p"));
    paths.add(new NumberPath(Integer.class, "p"));
    paths.add(new SetPath(String.class, StringPath.class, "p"));
    paths.add(new SimplePath(String.class, "p"));
    paths.add(new StringPath("p"));
    paths.add(new TimePath(Time.class, "p"));

    for (Path<?> path : paths) {
      Path other = ExpressionUtils.path(path.getType(), "p");
      assertThat(path.accept(ToStringVisitor.DEFAULT, null)).isEqualTo(path.toString());
      assertThat(other).hasSameHashCodeAs(path).isEqualTo(path);
      assertThat(path.getMetadata()).isNotNull();
      assertThat(path.getType()).isNotNull();
      assertThat(path.getRoot()).isEqualTo(path);
    }
  }

  @Test
  void parent_path() {
    Path<Object> person = ExpressionUtils.path(Object.class, "person");
    Path<String> name = ExpressionUtils.path(String.class, person, "name");
    assertThat(name).hasToString("person.name");
  }
}
