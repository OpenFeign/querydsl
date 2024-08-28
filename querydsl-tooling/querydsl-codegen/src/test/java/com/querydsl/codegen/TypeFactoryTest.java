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
package com.querydsl.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import com.querydsl.codegen.utils.model.TypeExtends;
import com.querydsl.codegen.utils.model.Types;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.types.Expression;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.Test;

public class TypeFactoryTest {

  Expression<?> field;

  Expression<Object> field2;

  Expression<?> field3;

  List<? extends Expression<?>> field4;

  enum EnumExample {
    FIRST,
    SECOND
  }

  static class Entity<A> {

    List<? extends A> field;
  }

  static class ComparableEntity<T extends Comparable<? super T>> implements Serializable {

    private static final long serialVersionUID = 4781357420221474135L;
  }

  private TypeFactory factory = new TypeFactory();

  @Test
  public void innerClass_field() throws SecurityException, NoSuchFieldException {
    var field = Entity.class.getDeclaredField("field");
    var type = factory.get(field.getType(), field.getGenericType());
    assertThat(type.getParameters()).hasSize(1);
    assertThat(Types.OBJECT).isEqualTo(type.getParameters().get(0));
  }

  @Test
  public void parameters() {
    var type = factory.getEntityType(Examples.Complex.class);
    assertThat(type.getParameters()).hasSize(1);
    assertThat(type.getParameters().get(0).getClass()).isEqualTo(TypeExtends.class);
  }

  @Test
  public void map_field_parameters() throws SecurityException, NoSuchFieldException {
    var field = Examples.ComplexCollections.class.getDeclaredField("map2");
    var type = factory.get(field.getType(), field.getGenericType());
    assertThat(type.getParameters()).hasSize(2);
    var valueType = type.getParameters().get(1);
    assertThat(valueType.getParameters()).hasSize(1);
    assertThat(valueType.getParameters().get(0).getClass()).isEqualTo(TypeExtends.class);
  }

  @Test
  public void orderBys() throws SecurityException, NoSuchFieldException {
    var field = Examples.OrderBys.class.getDeclaredField("orderBy");
    var type = factory.get(field.getType(), field.getGenericType());
    assertThat(type.getParameters()).hasSize(1);
  }

  @Test
  public void subEntity() {
    var type = factory.get(Examples.SubEntity.class);
    assertThat(type.getParameters()).isEmpty();
  }

  @Test
  public void abstractEntity_code() throws SecurityException, NoSuchFieldException {
    var field = EmbeddedTest.AbstractEntity.class.getDeclaredField("code");
    var type = factory.get(field.getType(), field.getGenericType());
    assertThat(type instanceof TypeExtends).isTrue();
    assertThat(((TypeExtends) type).getVarName()).isEqualTo("C");
  }

  @Test
  public void simpleTypes_classList5() throws SecurityException, NoSuchFieldException {
    var field = Examples.SimpleTypes.class.getDeclaredField("classList5");
    var type = factory.get(field.getType(), field.getGenericType());
    assertThat(type.getCategory()).isEqualTo(TypeCategory.LIST);
    var parameter = type.getParameters().get(0);
    assertThat(parameter.getClass()).isEqualTo(ClassType.class);
    assertThat(parameter.getParameters().get(0).getClass()).isEqualTo(TypeExtends.class);
  }

  @Test
  public void collection_of_collection() throws SecurityException, NoSuchFieldException {
    var field = Examples.GenericRelations.class.getDeclaredField("col3");
    var type = factory.get(field.getType(), field.getGenericType());
    assertThat(type.getParameters()).hasSize(1);
    var valueType = type.getParameters().get(0);
    assertThat(valueType.getParameters().get(0).getClass()).isEqualTo(TypeExtends.class);
  }

  @Test
  public void generics_wildCard() throws SecurityException, NoSuchFieldException {
    var field = getClass().getDeclaredField("field");
    var type = factory.get(field.getType(), field.getGenericType());
    assertThat(type.getParameters()).hasSize(1);
    assertThat(type.getParameters().get(0).getClass()).isEqualTo(TypeExtends.class);
    //        assertNull(type.getParameters().get(0));
  }

  @Test
  public void generics_object() throws SecurityException, NoSuchFieldException {
    var field = getClass().getDeclaredField("field2");
    var type = factory.get(field.getType(), field.getGenericType());
    assertThat(type.getParameters()).hasSize(1);
    assertThat(type.getParameters().get(0)).isEqualTo(Types.OBJECT);
  }

  @Test
  public void generics_typeVariable() {
    Type type = factory.getEntityType(Generic2Test.AbstractCollectionAttribute.class);
    assertThat(type.getParameters().get(0).getClass()).isEqualTo(TypeExtends.class);
    var t = (TypeExtends) type.getParameters().get(0);
    assertThat(t.getVarName()).isEqualTo("T");
  }

  @Test
  public void generics_wildcard() throws SecurityException, NoSuchFieldException {
    var field = DefaultQueryMetadata.class.getDeclaredField("exprInJoins");
    var type = factory.get(field.getType(), field.getGenericType());
    assertThat(type.getCategory()).isEqualTo(TypeCategory.SET);
    var parameter = type.getParameters().get(0);
    assertThat(parameter.getJavaClass()).isEqualTo(Expression.class);
    parameter = parameter.getParameters().get(0);
    assertThat(parameter.getClass()).isEqualTo(TypeExtends.class);
    assertThat(((TypeExtends) parameter).getVarName()).isNull();
  }

  @Test
  public void comparableEntity() {
    Type type = factory.getEntityType(ComparableEntity.class);
    // ComparableEntity<T extends Comparable<? super T>> implements Serializable
    assertThat(type.getParameters()).hasSize(1);
    var t = (TypeExtends) type.getParameters().get(0);
    assertThat(t.getVarName()).isEqualTo("T");
    assertThat(t.getParameters()).hasSize(1);
  }

  @Test
  public void rawField() throws SecurityException, NoSuchFieldException {
    var field = getClass().getDeclaredField("field3");
    var type = factory.get(field.getType(), field.getGenericType());
    assertThat(type.getParameters()).hasSize(1);
    //        assertEquals(Types.OBJECT, type.getParameters().get(0));
  }

  @Test
  public void extends_() throws SecurityException, NoSuchFieldException {
    var field = getClass().getDeclaredField("field4");
    var type = factory.get(field.getType(), field.getGenericType());
    assertThat(type.getParameters()).hasSize(1);
    //        assertEquals(Types.OBJECT, type.getParameters().get(0));
  }

  @Test
  public void className() {
    var type = factory.get(EnumExample.class);
    assertThat(type.getFullName()).isEqualTo("com.querydsl.codegen.TypeFactoryTest.EnumExample");
  }

  @Test
  public void blob() {
    var blob = factory.get(Blob.class);
    assertThat(blob.getSimpleName()).isEqualTo("Blob");
    assertThat(blob.getFullName()).isEqualTo("java.sql.Blob");
    assertThat(blob.getPackageName()).isEqualTo("java.sql");
  }

  @Test
  public void boolean_() {
    var bo = factory.get(boolean.class);
    assertThat(bo.getCategory()).isEqualTo(TypeCategory.BOOLEAN);
    assertThat(bo.getSimpleName()).isEqualTo("Boolean");
    assertThat(bo.getFullName()).isEqualTo("java.lang.Boolean");
    assertThat(bo.getPackageName()).isEqualTo("java.lang");
  }

  @Test
  public void simpleType() {
    for (Class<?> cl :
        Arrays.<Class<?>>asList(
            Blob.class, Clob.class, Locale.class, Class.class, Serializable.class)) {
      assertThat(factory.get(cl).getCategory())
          .as("wrong type for " + cl.getName())
          .isEqualTo(TypeCategory.SIMPLE);
    }
  }

  @Test
  public void numberType() {
    for (Class<?> cl : Arrays.<Class<?>>asList(Byte.class, Integer.class)) {
      assertThat(factory.get(cl).getCategory())
          .as("wrong type for " + cl.getName())
          .isEqualTo(TypeCategory.NUMERIC);
    }
  }

  @Test
  public void enumType() {
    assertThat(factory.get(EnumExample.class).getCategory()).isEqualTo(TypeCategory.ENUM);
  }

  @Test
  public void unknownAsEntity() {
    assertThat(factory.get(TypeFactoryTest.class).getCategory()).isEqualTo(TypeCategory.SIMPLE);

    factory = new TypeFactory();
    factory.setUnknownAsEntity(true);
    assertThat(factory.get(TypeFactoryTest.class).getCategory()).isEqualTo(TypeCategory.CUSTOM);
  }

  @Test
  public void arrayType() {
    assertThat(factory.get(Byte[].class)).isEqualTo(Types.BYTE.asArrayType());
    assertThat(factory.get(byte[].class)).isEqualTo(Types.BYTE_P.asArrayType());
  }
}
