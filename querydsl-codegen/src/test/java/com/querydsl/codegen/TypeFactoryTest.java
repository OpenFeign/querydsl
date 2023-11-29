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

import static org.junit.Assert.*;

import com.querydsl.codegen.utils.model.*;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.types.Expression;
import java.io.Serializable;
import java.lang.reflect.Field;
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
    Field field = Entity.class.getDeclaredField("field");
    Type type = factory.get(field.getType(), field.getGenericType());
    assertEquals(1, type.getParameters().size());
    assertEquals(Types.OBJECT, type.getParameters().get(0));
  }

  @Test
  public void parameters() {
    EntityType type = factory.getEntityType(Examples.Complex.class);
    assertEquals(1, type.getParameters().size());
    assertEquals(TypeExtends.class, type.getParameters().get(0).getClass());
  }

  @Test
  public void map_field_parameters() throws SecurityException, NoSuchFieldException {
    Field field = Examples.ComplexCollections.class.getDeclaredField("map2");
    Type type = factory.get(field.getType(), field.getGenericType());
    assertEquals(2, type.getParameters().size());
    Type valueType = type.getParameters().get(1);
    assertEquals(1, valueType.getParameters().size());
    assertEquals(TypeExtends.class, valueType.getParameters().get(0).getClass());
  }

  @Test
  public void orderBys() throws SecurityException, NoSuchFieldException {
    Field field = Examples.OrderBys.class.getDeclaredField("orderBy");
    Type type = factory.get(field.getType(), field.getGenericType());
    assertEquals(1, type.getParameters().size());
  }

  @Test
  public void subEntity() {
    Type type = factory.get(Examples.SubEntity.class);
    assertEquals(0, type.getParameters().size());
  }

  @Test
  public void abstractEntity_code() throws SecurityException, NoSuchFieldException {
    Field field = EmbeddedTest.AbstractEntity.class.getDeclaredField("code");
    Type type = factory.get(field.getType(), field.getGenericType());
    assertTrue(type instanceof TypeExtends);
    assertEquals("C", ((TypeExtends) type).getVarName());
  }

  @Test
  public void simpleTypes_classList5() throws SecurityException, NoSuchFieldException {
    Field field = Examples.SimpleTypes.class.getDeclaredField("classList5");
    Type type = factory.get(field.getType(), field.getGenericType());
    assertEquals(TypeCategory.LIST, type.getCategory());
    Type parameter = type.getParameters().get(0);
    assertEquals(ClassType.class, parameter.getClass());
    assertEquals(TypeExtends.class, parameter.getParameters().get(0).getClass());
  }

  @Test
  public void collection_of_collection() throws SecurityException, NoSuchFieldException {
    Field field = Examples.GenericRelations.class.getDeclaredField("col3");
    Type type = factory.get(field.getType(), field.getGenericType());
    assertEquals(1, type.getParameters().size());
    Type valueType = type.getParameters().get(0);
    assertEquals(TypeExtends.class, valueType.getParameters().get(0).getClass());
  }

  @Test
  public void generics_wildCard() throws SecurityException, NoSuchFieldException {
    Field field = getClass().getDeclaredField("field");
    Type type = factory.get(field.getType(), field.getGenericType());
    assertEquals(1, type.getParameters().size());
    assertEquals(TypeExtends.class, type.getParameters().get(0).getClass());
    //        assertNull(type.getParameters().get(0));
  }

  @Test
  public void generics_object() throws SecurityException, NoSuchFieldException {
    Field field = getClass().getDeclaredField("field2");
    Type type = factory.get(field.getType(), field.getGenericType());
    assertEquals(1, type.getParameters().size());
    assertEquals(Types.OBJECT, type.getParameters().get(0));
  }

  @Test
  public void generics_typeVariable() {
    Type type = factory.getEntityType(Generic2Test.AbstractCollectionAttribute.class);
    assertEquals(TypeExtends.class, type.getParameters().get(0).getClass());
    TypeExtends t = (TypeExtends) type.getParameters().get(0);
    assertEquals("T", t.getVarName());
  }

  @Test
  public void generics_wildcard() throws SecurityException, NoSuchFieldException {
    Field field = DefaultQueryMetadata.class.getDeclaredField("exprInJoins");
    Type type = factory.get(field.getType(), field.getGenericType());
    assertEquals(TypeCategory.SET, type.getCategory());
    Type parameter = type.getParameters().get(0);
    assertEquals(Expression.class, parameter.getJavaClass());
    parameter = parameter.getParameters().get(0);
    assertEquals(TypeExtends.class, parameter.getClass());
    assertNull(((TypeExtends) parameter).getVarName());
  }

  @Test
  public void comparableEntity() {
    Type type = factory.getEntityType(ComparableEntity.class);
    // ComparableEntity<T extends Comparable<? super T>> implements Serializable
    assertEquals(1, type.getParameters().size());
    TypeExtends t = (TypeExtends) type.getParameters().get(0);
    assertEquals("T", t.getVarName());
    assertEquals(1, t.getParameters().size());
  }

  @Test
  public void rawField() throws SecurityException, NoSuchFieldException {
    Field field = getClass().getDeclaredField("field3");
    Type type = factory.get(field.getType(), field.getGenericType());
    assertEquals(1, type.getParameters().size());
    //        assertEquals(Types.OBJECT, type.getParameters().get(0));
  }

  @Test
  public void extends_() throws SecurityException, NoSuchFieldException {
    Field field = getClass().getDeclaredField("field4");
    Type type = factory.get(field.getType(), field.getGenericType());
    assertEquals(1, type.getParameters().size());
    //        assertEquals(Types.OBJECT, type.getParameters().get(0));
  }

  @Test
  public void className() {
    Type type = factory.get(EnumExample.class);
    assertEquals("com.querydsl.codegen.TypeFactoryTest.EnumExample", type.getFullName());
  }

  @Test
  public void blob() {
    Type blob = factory.get(Blob.class);
    assertEquals("Blob", blob.getSimpleName());
    assertEquals("java.sql.Blob", blob.getFullName());
    assertEquals("java.sql", blob.getPackageName());
  }

  @Test
  public void boolean_() {
    Type bo = factory.get(boolean.class);
    assertEquals(TypeCategory.BOOLEAN, bo.getCategory());
    assertEquals("Boolean", bo.getSimpleName());
    assertEquals("java.lang.Boolean", bo.getFullName());
    assertEquals("java.lang", bo.getPackageName());
  }

  @Test
  public void simpleType() {
    for (Class<?> cl :
        Arrays.<Class<?>>asList(
            Blob.class, Clob.class, Locale.class, Class.class, Serializable.class)) {
      assertEquals(
          "wrong type for " + cl.getName(), TypeCategory.SIMPLE, factory.get(cl).getCategory());
    }
  }

  @Test
  public void numberType() {
    for (Class<?> cl : Arrays.<Class<?>>asList(Byte.class, Integer.class)) {
      assertEquals(
          "wrong type for " + cl.getName(), TypeCategory.NUMERIC, factory.get(cl).getCategory());
    }
  }

  @Test
  public void enumType() {
    assertEquals(TypeCategory.ENUM, factory.get(EnumExample.class).getCategory());
  }

  @Test
  public void unknownAsEntity() {
    assertEquals(TypeCategory.SIMPLE, factory.get(TypeFactoryTest.class).getCategory());

    factory = new TypeFactory();
    factory.setUnknownAsEntity(true);
    assertEquals(TypeCategory.CUSTOM, factory.get(TypeFactoryTest.class).getCategory());
  }

  @Test
  public void arrayType() {
    assertEquals(Types.BYTE.asArrayType(), factory.get(Byte[].class));
    assertEquals(Types.BYTE_P.asArrayType(), factory.get(byte[].class));
  }
}
