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
package com.querydsl.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinType;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.domain.sql.SAnimal;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.MySQLTemplates;
import jakarta.persistence.Column;
import org.junit.Test;

public class NativeSQLSerializerTest {

  public static class Entity {
    @Column private String name;

    @Column(name = "first_name")
    private String firstName;
  }

  @Test
  public void in() {
    Configuration conf = new Configuration(new MySQLTemplates());
    NativeSQLSerializer serializer = new NativeSQLSerializer(conf, true);
    DefaultQueryMetadata md = new DefaultQueryMetadata();
    SAnimal cat = SAnimal.animal_;
    md.addJoin(JoinType.DEFAULT, cat);
    md.addWhere(cat.name.in("X", "Y"));
    md.setProjection(cat.id);
    serializer.serialize(md, false);
    assertThat(serializer.toString())
        .isEqualTo(
            """
            select animal_.id
            from animal_ animal_
            where animal_.name in (?1, ?2)\
            """);
  }

  @Test
  public void path_column() {
    PathBuilder<Entity> entity = new PathBuilder<Entity>(Entity.class, "entity");
    Configuration conf = new Configuration(new MySQLTemplates());
    NativeSQLSerializer serializer = new NativeSQLSerializer(conf, true);
    serializer.handle(entity.get("name"));
    assertThat(serializer.toString()).isEqualTo("entity.name");
  }

  @Test
  public void path_column2() {
    PathBuilder<Entity> entity = new PathBuilder<Entity>(Entity.class, "entity");
    Configuration conf = new Configuration(new MySQLTemplates());
    NativeSQLSerializer serializer = new NativeSQLSerializer(conf, true);
    serializer.handle(entity.get("firstName"));
    assertThat(serializer.toString()).isEqualTo("entity.first_name");
  }
}
