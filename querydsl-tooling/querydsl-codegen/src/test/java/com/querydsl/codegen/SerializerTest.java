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

import com.querydsl.codegen.utils.JavaWriter;
import com.querydsl.codegen.utils.StringUtils;
import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.Constructor;
import com.querydsl.codegen.utils.model.Parameter;
import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import com.querydsl.codegen.utils.model.TypeExtends;
import com.querydsl.codegen.utils.model.TypeSuper;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class SerializerTest {

  private EntityType type;

  private Writer writer = new StringWriter();

  private TypeMappings typeMappings = new JavaTypeMappings();

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    // type
    Type typeModel =
        new SimpleType(
            TypeCategory.ENTITY,
            "com.querydsl.DomainClass",
            "com.querydsl",
            "DomainClass",
            false,
            false);
    type = new EntityType(typeModel);

    // property
    type.addProperty(new Property(type, "entityField", type));
    type.addProperty(
        new Property(
            type,
            "collection",
            new ClassType(TypeCategory.COLLECTION, Collection.class, typeModel)));
    type.addProperty(
        new Property(type, "listField", new ClassType(TypeCategory.LIST, List.class, typeModel)));
    type.addProperty(
        new Property(type, "setField", new ClassType(TypeCategory.SET, Set.class, typeModel)));
    type.addProperty(
        new Property(
            type, "arrayField", new ClassType(TypeCategory.ARRAY, String[].class, typeModel)));
    type.addProperty(
        new Property(
            type, "mapField", new ClassType(TypeCategory.MAP, List.class, typeModel, typeModel)));
    type.addProperty(
        new Property(
            type,
            "superTypeField",
            new TypeExtends(new ClassType(TypeCategory.MAP, List.class, typeModel, typeModel))));
    type.addProperty(
        new Property(
            type,
            "extendsTypeField",
            new TypeSuper(new ClassType(TypeCategory.MAP, List.class, typeModel, typeModel))));

    for (Class<?> cl :
        Arrays.asList(
            Boolean.class,
            Comparable.class,
            Integer.class,
            Date.class,
            java.sql.Date.class,
            java.sql.Time.class)) {
      Type classType = new ClassType(TypeCategory.get(cl.getName()), cl);
      type.addProperty(new Property(type, StringUtils.uncapitalize(cl.getSimpleName()), classType));
    }

    // constructor
    var firstName = new Parameter("firstName", new ClassType(TypeCategory.STRING, String.class));
    var lastName = new Parameter("lastName", new ClassType(TypeCategory.STRING, String.class));
    type.addConstructor(new Constructor(Arrays.asList(firstName, lastName)));
  }

  @Test
  public void entitySerializer() throws Exception {
    new DefaultEntitySerializer(typeMappings, Collections.<String>emptyList())
        .serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
  }

  @Test
  public void entitySerializer2() throws Exception {
    new DefaultEntitySerializer(typeMappings, Collections.<String>emptyList())
        .serialize(
            type, new SimpleSerializerConfig(true, true, true, true, ""), new JavaWriter(writer));
  }

  @Test
  public void embeddableSerializer() throws Exception {
    new DefaultEmbeddableSerializer(typeMappings, Collections.<String>emptyList())
        .serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
  }

  @Test
  public void supertypeSerializer() throws IOException {
    new DefaultSupertypeSerializer(typeMappings, Collections.<String>emptyList())
        .serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
  }

  @Test
  public void projectionSerializer() throws IOException {
    new DefaultProjectionSerializer(typeMappings)
        .serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
  }
}
