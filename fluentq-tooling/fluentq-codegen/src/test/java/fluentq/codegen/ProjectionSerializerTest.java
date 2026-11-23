/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.codegen.utils.JavaWriter;
import fluentq.codegen.utils.model.Constructor;
import fluentq.codegen.utils.model.Parameter;
import fluentq.codegen.utils.model.SimpleType;
import fluentq.codegen.utils.model.Type;
import fluentq.codegen.utils.model.TypeCategory;
import fluentq.codegen.utils.model.Types;
import fluentq.core.annotations.Generated;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class ProjectionSerializerTest {

  @Test
  public void constructors() throws IOException {
    Type typeModel =
        new SimpleType(
            TypeCategory.ENTITY, "fluentq.DomainClass", "fluentq", "DomainClass", false, false);
    var type = new EntityType(typeModel);

    // constructor
    var firstName = new Parameter("firstName", Types.STRING);
    var lastName = new Parameter("lastName", Types.STRING);
    var age = new Parameter("age", Types.INTEGER);
    type.addConstructor(new Constructor(Arrays.asList(firstName, lastName, age)));
    type.addConstructor(new Constructor(Arrays.asList(firstName, lastName)));

    Writer writer = new StringWriter();
    ProjectionSerializer serializer = new DefaultProjectionSerializer(new JavaTypeMappings());
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    assertThat(writer.toString())
        .contains(
            """
        public Path(Expression<String> firstName, Expression<String> lastName) {
            super(fluentq.DomainClass.class, new Class<?>[]{String.class, String.class}, firstName, lastName);
        }

        public Path(Expression<String> firstName, Expression<String> lastName, Expression<Integer> age) {
            super(fluentq.DomainClass.class, new Class<?>[]{String.class, String.class, int.class}, firstName, lastName, age);
        }
    """);
  }

  @Test
  public void defaultGeneratedAnnotation() throws IOException {
    Type typeModel =
        new SimpleType(
            TypeCategory.ENTITY, "fluentq.DomainClass", "fluentq", "DomainClass", false, false);
    var type = new EntityType(typeModel);

    Writer writer = new StringWriter();
    ProjectionSerializer serializer = new DefaultProjectionSerializer(new JavaTypeMappings());
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    var generatedSource = writer.toString();
    assertThat(generatedSource)
        .contains("import %s;".formatted(GeneratedAnnotationResolver.resolveDefault().getName()));
    assertThat(generatedSource)
        .containsIgnoringNewLines(
            "@Generated(\"fluentq.codegen.DefaultProjectionSerializer\")\npublic class");
  }

  @Test
  public void customGeneratedAnnotation() throws IOException {
    Type typeModel =
        new SimpleType(
            TypeCategory.ENTITY, "fluentq.DomainClass", "fluentq", "DomainClass", false, false);
    var type = new EntityType(typeModel);

    Writer writer = new StringWriter();
    ProjectionSerializer serializer =
        new DefaultProjectionSerializer(new JavaTypeMappings(), Generated.class);
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    var generatedSource = writer.toString();
    assertThat(generatedSource).contains("import fluentq.core.annotations.Generated");
    assertThat(generatedSource)
        .containsIgnoringNewLines(
            "@Generated(\"fluentq.codegen.DefaultProjectionSerializer\")\npublic class");
  }
}
