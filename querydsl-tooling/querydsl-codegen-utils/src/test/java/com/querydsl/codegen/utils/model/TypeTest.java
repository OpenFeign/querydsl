/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.querydsl.codegen.utils.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class TypeTest {

  private Set<String> packages = Collections.singleton("java.lang");

  private Set<String> classes = Collections.emptySet();

  private ClassType locale = new ClassType(TypeCategory.SIMPLE, Locale.class);

  private Type string = Types.STRING;

  private Type string2 = new SimpleType(string);

  private Type locale2 = new SimpleType(locale);

  private Type stringList = new ClassType(TypeCategory.LIST, List.class, Types.STRING);

  private Type stringList2 = new SimpleType(Types.LIST, Types.STRING);

  private Type stringMap = new ClassType(TypeCategory.MAP, Map.class, Types.STRING, Types.STRING);

  private Type stringMap2 = new SimpleType(Types.MAP, Types.STRING, Types.STRING);

  @Test
  public void arrayType() {
    assertThat(Types.OBJECTS.getGenericName(true)).hasToString("Object[]");
  }

  @Test
  public void Equals() {
    assertThat(locale2).isEqualTo(locale);
    assertThat(locale).isEqualTo(locale2);
    assertThat(stringList2).isEqualTo(stringList);
    assertThat(stringList).isEqualTo(stringList2);
  }

  @Test
  public void Hashcode() {
    assertThat(locale2.hashCode()).isEqualTo(locale.hashCode());
    assertThat(stringList2.hashCode()).isEqualTo(stringList.hashCode());
  }

  @Test
  public void GetGenericNameBoolean() {
    assertThat(locale.getGenericName(true)).isEqualTo("java.util.Locale");
    assertThat(locale2.getGenericName(true)).isEqualTo("java.util.Locale");
    assertThat(stringList.getGenericName(true)).isEqualTo("java.util.List<String>");
    assertThat(stringList2.getGenericName(true)).isEqualTo("java.util.List<String>");
    assertThat(stringMap.getGenericName(true)).isEqualTo("java.util.Map<String, String>");
    assertThat(stringMap2.getGenericName(true)).isEqualTo("java.util.Map<String, String>");

    assertThat(string.getGenericName(true)).isEqualTo("String");
    assertThat(string2.getGenericName(true)).isEqualTo("String");
  }

  @Test
  public void GetRawName() {
    assertThat(locale.getRawName(packages, classes)).isEqualTo("java.util.Locale");
    assertThat(locale2.getRawName(packages, classes)).isEqualTo("java.util.Locale");
    assertThat(stringList.getRawName(packages, classes)).isEqualTo("java.util.List");
    assertThat(stringList2.getRawName(packages, classes)).isEqualTo("java.util.List");

    assertThat(string.getRawName(packages, classes)).isEqualTo("String");
    assertThat(string2.getRawName(packages, classes)).isEqualTo("String");
  }

  @Test
  public void GetGenericNameBooleanSetOfStringSetOfString() {
    assertThat(locale.getGenericName(true, packages, classes)).isEqualTo("java.util.Locale");
    assertThat(locale2.getGenericName(true, packages, classes)).isEqualTo("java.util.Locale");
    assertThat(stringList.getGenericName(true, packages, classes))
        .isEqualTo("java.util.List<String>");
    assertThat(stringList2.getGenericName(true, packages, classes))
        .isEqualTo("java.util.List<String>");
  }

  @Test
  public void GetFullName() {
    assertThat(locale.getFullName()).isEqualTo("java.util.Locale");
    assertThat(locale2.getFullName()).isEqualTo("java.util.Locale");
    assertThat(stringList.getFullName()).isEqualTo("java.util.List");
    assertThat(stringList2.getFullName()).isEqualTo("java.util.List");
  }

  @Test
  public void GetPackageName() {
    assertThat(locale.getPackageName()).isEqualTo("java.util");
    assertThat(locale2.getPackageName()).isEqualTo("java.util");
    assertThat(stringList.getPackageName()).isEqualTo("java.util");
    assertThat(stringList2.getPackageName()).isEqualTo("java.util");
  }

  @Test
  public void GetParameters() {
    assertThat(locale.getParameters()).isEqualTo(Collections.emptyList());
    assertThat(locale2.getParameters()).isEqualTo(Collections.emptyList());
    assertThat(stringList.getParameters()).isEqualTo(Collections.singletonList(Types.STRING));
    assertThat(stringList2.getParameters()).isEqualTo(Collections.singletonList(Types.STRING));
  }

  @Test
  public void GetSimpleName() {
    assertThat(locale.getSimpleName()).isEqualTo("Locale");
    assertThat(locale2.getSimpleName()).isEqualTo("Locale");
    assertThat(stringList.getSimpleName()).isEqualTo("List");
    assertThat(stringList2.getSimpleName()).isEqualTo("List");
  }

  @Test
  public void GetJavaClass() {
    assertThat(locale.getJavaClass()).isEqualTo(Locale.class);
  }

  @Test
  public void IsFinal() {
    assertThat(locale.isFinal()).isTrue();
    assertThat(locale2.isFinal()).isTrue();
    assertThat(stringList.isFinal()).isFalse();

    assertThat(Types.STRING.isFinal()).isTrue();
    assertThat(Types.LONG.isFinal()).isTrue();
  }

  @Test
  public void IsPrimitive() {
    assertThat(locale.isPrimitive()).isFalse();
    assertThat(locale2.isPrimitive()).isFalse();
    assertThat(stringList.isPrimitive()).isFalse();
    assertThat(stringList2.isPrimitive()).isFalse();
  }

  @Test
  public void GetCategory() {
    assertThat(locale.getCategory()).isEqualTo(TypeCategory.SIMPLE);
    assertThat(locale2.getCategory()).isEqualTo(TypeCategory.SIMPLE);
    assertThat(stringList.getCategory()).isEqualTo(TypeCategory.LIST);
    assertThat(stringList2.getCategory()).isEqualTo(TypeCategory.LIST);
  }

  @Test
  public void As() {
    assertThat(stringList.as(TypeCategory.SIMPLE).getCategory()).isEqualTo(TypeCategory.SIMPLE);
    assertThat(stringList2.as(TypeCategory.SIMPLE).getCategory()).isEqualTo(TypeCategory.SIMPLE);
  }

  //    @Test
  //    public void GetPrimitiveName() {
  //        assertNull(locale.getPrimitiveName());
  //        assertNull(locale2.getPrimitiveName());
  //        assertNull(stringList.getPrimitiveName());
  //        assertNull(stringList2.getPrimitiveName());
  //    }

  @Test
  public void ToString() {
    assertThat(locale).hasToString("java.util.Locale");
    assertThat(locale2).hasToString("java.util.Locale");
    assertThat(stringList).hasToString("java.util.List<String>");
    assertThat(stringList2).hasToString("java.util.List<String>");
  }

  @Test
  public void AsArrayType() {
    assertThat(locale.asArrayType().getFullName()).isEqualTo("java.util.Locale[]");
    assertThat(locale.asArrayType().getCategory()).isEqualTo(TypeCategory.ARRAY);
    assertThat(locale2.asArrayType().getFullName()).isEqualTo("java.util.Locale[]");
    assertThat(locale2.asArrayType().getCategory()).isEqualTo(TypeCategory.ARRAY);
    assertThat(stringList.asArrayType().getFullName()).isEqualTo("java.util.List[]");
    assertThat(stringList2.asArrayType().getFullName()).isEqualTo("java.util.List[]");
  }
}
