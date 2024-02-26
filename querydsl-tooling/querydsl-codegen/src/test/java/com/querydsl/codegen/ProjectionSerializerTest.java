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
import com.querydsl.codegen.utils.model.Constructor;
import com.querydsl.codegen.utils.model.Parameter;
import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import com.querydsl.codegen.utils.model.Types;
import com.querydsl.core.annotations.Generated;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import org.junit.Test;

public class ProjectionSerializerTest {

  @Test
  public void constructors() throws IOException {
    Type typeModel =
        new SimpleType(
            TypeCategory.ENTITY,
            "com.querydsl.DomainClass",
            "com.querydsl",
            "DomainClass",
            false,
            false);
    EntityType type = new EntityType(typeModel);

    // constructor
    Parameter firstName = new Parameter("firstName", Types.STRING);
    Parameter lastName = new Parameter("lastName", Types.STRING);
    Parameter age = new Parameter("age", Types.INTEGER);
    type.addConstructor(new Constructor(Arrays.asList(firstName, lastName, age)));

    Writer writer = new StringWriter();
    ProjectionSerializer serializer = new DefaultProjectionSerializer(new JavaTypeMappings());
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    assertThat(writer.toString()).contains("Expression<String> firstName");
    assertThat(writer.toString()).contains("Expression<String> lastName");
    assertThat(writer.toString()).contains("Expression<Integer> age");
  }

  @Test
  public void defaultGeneratedAnnotation() throws IOException {
    Type typeModel =
        new SimpleType(
            TypeCategory.ENTITY,
            "com.querydsl.DomainClass",
            "com.querydsl",
            "DomainClass",
            false,
            false);
    EntityType type = new EntityType(typeModel);

    Writer writer = new StringWriter();
    ProjectionSerializer serializer = new DefaultProjectionSerializer(new JavaTypeMappings());
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    String generatedSource = writer.toString();
    assertThat(generatedSource)
        .contains("import %s;".formatted(GeneratedAnnotationResolver.resolveDefault().getName()));
    assertThat(generatedSource)
        .containsIgnoringNewLines(
            "@Generated(\"com.querydsl.codegen.DefaultProjectionSerializer\")\npublic class");
  }

  @Test
  public void customGeneratedAnnotation() throws IOException {
    Type typeModel =
        new SimpleType(
            TypeCategory.ENTITY,
            "com.querydsl.DomainClass",
            "com.querydsl",
            "DomainClass",
            false,
            false);
    EntityType type = new EntityType(typeModel);

    Writer writer = new StringWriter();
    ProjectionSerializer serializer =
        new DefaultProjectionSerializer(new JavaTypeMappings(), Generated.class);
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    String generatedSource = writer.toString();
    assertThat(generatedSource).contains("import com.querydsl.core.annotations.Generated");
    assertThat(generatedSource)
        .containsIgnoringNewLines(
            "@Generated(\"com.querydsl.codegen.DefaultProjectionSerializer\")\npublic class");
  }
}
