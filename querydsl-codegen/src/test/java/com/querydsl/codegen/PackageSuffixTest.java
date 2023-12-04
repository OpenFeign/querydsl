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
import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.TypeCategory;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import org.junit.Test;

public class PackageSuffixTest {

  private final QueryTypeFactory queryTypeFactory = new QueryTypeFactoryImpl("Q", "", ".query");

  private final TypeMappings typeMappings = new JavaTypeMappings();

  private final EntitySerializer serializer =
      new DefaultEntitySerializer(typeMappings, Collections.<String>emptySet());

  private final StringWriter writer = new StringWriter();

  @Test
  public void correct_imports() throws IOException {
    SimpleType type =
        new SimpleType(TypeCategory.ENTITY, "test.Entity", "test", "Entity", false, false);
    EntityType entityType = new EntityType(type);
    typeMappings.register(entityType, queryTypeFactory.create(entityType));

    serializer.serialize(entityType, SimpleSerializerConfig.DEFAULT, new JavaWriter(writer));
    assertThat(writer.toString()).contains("import test.Entity;");
    assertThat(writer.toString()).contains("public class QEntity extends EntityPathBase<Entity> {");
  }
}
