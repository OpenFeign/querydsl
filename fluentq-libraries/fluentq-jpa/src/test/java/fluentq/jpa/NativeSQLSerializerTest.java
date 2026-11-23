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
package fluentq.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.core.DefaultQueryMetadata;
import fluentq.core.JoinType;
import fluentq.core.types.dsl.PathBuilder;
import fluentq.jpa.domain.sql.SAnimal_;
import fluentq.sql.Configuration;
import fluentq.sql.MySQLTemplates;
import jakarta.persistence.Column;
import org.junit.jupiter.api.Test;

public class NativeSQLSerializerTest {

  public static class Entity {
    @Column private String name;

    @Column(name = "first_name")
    private String firstName;
  }

  @Test
  public void in() {
    var conf = new Configuration(new MySQLTemplates());
    var serializer = new NativeSQLSerializer(conf, true);
    var md = new DefaultQueryMetadata();
    var cat = SAnimal_.animal_;
    md.addJoin(JoinType.DEFAULT, cat);
    md.addWhere(cat.name.in("X", "Y"));
    md.setProjection(cat.id);
    serializer.serialize(md, false);
    assertThat(serializer.toString())
        .isEqualTo(
            """
            select animal_.ID
            from animal_ animal_
            where animal_.NAME in (?1, ?2)\
            """);
  }

  @Test
  public void path_column() {
    var entity = new PathBuilder<>(Entity.class, "entity");
    var conf = new Configuration(new MySQLTemplates());
    var serializer = new NativeSQLSerializer(conf, true);
    serializer.handle(entity.get("name"));
    assertThat(serializer).hasToString("entity.name");
  }

  @Test
  public void path_column2() {
    var entity = new PathBuilder<>(Entity.class, "entity");
    var conf = new Configuration(new MySQLTemplates());
    var serializer = new NativeSQLSerializer(conf, true);
    serializer.handle(entity.get("firstName"));
    assertThat(serializer).hasToString("entity.first_name");
  }
}
