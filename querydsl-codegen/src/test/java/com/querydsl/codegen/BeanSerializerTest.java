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

import com.querydsl.codegen.utils.JavaWriter;
import com.querydsl.codegen.utils.StringUtils;
import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import com.querydsl.codegen.utils.model.Types;
import com.querydsl.core.annotations.Generated;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;

public class BeanSerializerTest {

  private Type typeModel;

  private EntityType type;

  private final Writer writer = new StringWriter();

  @Before
  public void setUp() {
    typeModel =
        new SimpleType(
            TypeCategory.ENTITY,
            "com.querydsl.DomainClass",
            "com.querydsl",
            "DomainClass",
            false,
            false);
    type = new EntityType(typeModel);
  }

  @Test
  public void annotations() throws IOException {
    type.addAnnotation(new QueryEntityImpl());

    var serializer = new BeanSerializer();
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    var str = writer.toString();

    assertThat(str).contains("import com.querydsl.core.annotations.QueryEntity;");
    assertThat(str).contains("@QueryEntity");
  }

  @Test
  public void annotated_property() throws IOException {
    var property = new Property(type, "entityField", type);
    property.addAnnotation(new QueryEntityImpl());
    type.addProperty(property);

    var serializer = new BeanSerializer();
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    var str = writer.toString();

    assertThat(str).contains("import com.querydsl.core.annotations.QueryEntity;");
    assertThat(str).contains("@QueryEntity");
  }

  @Test
  public void annotated_property_not_serialized() throws IOException {
    var property = new Property(type, "entityField", type);
    property.addAnnotation(new QueryEntityImpl());
    type.addProperty(property);

    var serializer = new BeanSerializer(false);
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    var str = writer.toString();

    assertThat(str.contains("import com.querydsl.core.annotations.QueryEntity;")).isFalse();
    assertThat(str.contains("@QueryEntity")).isFalse();
  }

  @Test
  public void capitalization() throws IOException {
    // property
    type.addProperty(new Property(type, "cId", type));

    var serializer = new BeanSerializer();
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    assertThat(writer.toString()).contains("public DomainClass getcId() {");
  }

  @Test
  public void interfaces() throws IOException {
    var serializer = new BeanSerializer();
    serializer.addInterface(new ClassType(Serializable.class));
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    assertThat(writer.toString()).contains("public class DomainClass implements Serializable {");
  }

  @Test
  public void interfaces2() throws IOException {
    var serializer = new BeanSerializer();
    serializer.addInterface(Serializable.class);
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    assertThat(writer.toString()).contains("public class DomainClass implements Serializable {");
  }

  @Test
  public void toString_() throws IOException {
    // property
    type.addProperty(new Property(type, "entityField", type));
    type.addProperty(new Property(type, "collection", new SimpleType(Types.COLLECTION, typeModel)));
    type.addProperty(new Property(type, "listField", new SimpleType(Types.LIST, typeModel)));
    type.addProperty(new Property(type, "setField", new SimpleType(Types.SET, typeModel)));
    type.addProperty(
        new Property(type, "arrayField", new ClassType(TypeCategory.ARRAY, String[].class)));
    type.addProperty(
        new Property(type, "mapField", new SimpleType(Types.MAP, typeModel, typeModel)));

    var serializer = new BeanSerializer();
    serializer.setAddToString(true);
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    assertThat(String.valueOf(writer))
        .containsIgnoringNewLines("    @Override\n" + "    public String toString()");
  }

  @Test
  public void fullConstructor() throws IOException {
    // property
    type.addProperty(new Property(type, "entityField", type));
    type.addProperty(new Property(type, "collection", new SimpleType(Types.COLLECTION, typeModel)));
    type.addProperty(new Property(type, "listField", new SimpleType(Types.LIST, typeModel)));
    type.addProperty(new Property(type, "setField", new SimpleType(Types.SET, typeModel)));
    type.addProperty(
        new Property(type, "arrayField", new ClassType(TypeCategory.ARRAY, String[].class)));
    type.addProperty(
        new Property(type, "mapField", new SimpleType(Types.MAP, typeModel, typeModel)));

    var serializer = new BeanSerializer();
    serializer.setAddFullConstructor(true);
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    // System.out.println(writer.toString());
  }

  @Test
  public void properties() throws IOException {
    // property
    type.addProperty(new Property(type, "entityField", type));
    type.addProperty(new Property(type, "collection", new SimpleType(Types.COLLECTION, typeModel)));
    type.addProperty(new Property(type, "listField", new SimpleType(Types.LIST, typeModel)));
    type.addProperty(new Property(type, "setField", new SimpleType(Types.SET, typeModel)));
    type.addProperty(
        new Property(type, "arrayField", new ClassType(TypeCategory.ARRAY, String[].class)));
    type.addProperty(
        new Property(type, "mapField", new SimpleType(Types.MAP, typeModel, typeModel)));

    for (Class<?> cl :
        Arrays.<Class<?>>asList(
            Boolean.class,
            Comparable.class,
            Integer.class,
            Date.class,
            java.sql.Date.class,
            java.sql.Time.class)) {
      Type classType = new ClassType(TypeCategory.get(cl.getName()), cl);
      type.addProperty(new Property(type, StringUtils.uncapitalize(cl.getSimpleName()), classType));
    }

    var serializer = new BeanSerializer();
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    var str = writer.toString();
    // System.err.println(str);
    for (String prop :
        Arrays.asList(
            "String[] arrayField;",
            "Boolean boolean$;",
            "Collection<DomainClass> collection;",
            "Comparable comparable;",
            "java.util.Date date;",
            "DomainClass entityField;",
            "Integer integer;",
            "List<DomainClass> listField;",
            "Map<DomainClass, DomainClass> mapField;",
            "Set<DomainClass> setField;",
            "java.sql.Time time;")) {
      assertThat(str.contains(prop)).as(prop + " was not contained").isTrue();
    }
  }

  @Test
  public void defaultsGeneratedAnnotation() throws IOException {
    Serializer serializer = new BeanSerializer();
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    var generatedSource = String.valueOf(writer);
    assertThat(generatedSource)
        .contains("import %s;".formatted(GeneratedAnnotationResolver.resolveDefault().getName()));
    assertThat(generatedSource)
        .containsIgnoringNewLines(
            "@Generated(\"com.querydsl.codegen.BeanSerializer\")\npublic class");
  }

  @Test
  public void customGeneratedAnnotation() throws IOException {
    Serializer serializer =
        new BeanSerializer(BeanSerializer.DEFAULT_JAVADOC_SUFFIX, Generated.class);
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    var generatedSource = String.valueOf(writer);
    assertThat(generatedSource).contains("import com.querydsl.core.annotations.Generated;");
    assertThat(generatedSource)
        .containsIgnoringNewLines(
            "@Generated(\"com.querydsl.codegen.BeanSerializer\")\npublic class");
  }
}
