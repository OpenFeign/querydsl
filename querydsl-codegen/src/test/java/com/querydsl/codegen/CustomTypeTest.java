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
import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.TypeCategory;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import org.junit.Test;

public class CustomTypeTest {

  private final QueryTypeFactory queryTypeFactory = new QueryTypeFactoryImpl("Q", "", "");

  private final TypeMappings typeMappings = new JavaTypeMappings();

  private final EntitySerializer serializer =
      new DefaultEntitySerializer(typeMappings, Collections.<String>emptySet());

  private final StringWriter writer = new StringWriter();

  @Test
  public void customType() throws IOException {
    SimpleType type = new SimpleType(TypeCategory.ENTITY, "Entity", "", "Entity", false, false);
    EntityType entityType = new EntityType(type);
    entityType.addProperty(new Property(entityType, "property", new ClassType(Double[].class)));
    typeMappings.register(new ClassType(Double[].class), new ClassType(Point.class));
    typeMappings.register(entityType, queryTypeFactory.create(entityType));
    assertThat(typeMappings.isRegistered(entityType.getProperties().iterator().next().getType()))
        .isTrue();

    serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    assertThat(writer.toString())
        .contains(
            """
            public final com.querydsl.codegen.Point property = \
            new com.querydsl.codegen.Point(forProperty("property"));\
            """);
  }
}
