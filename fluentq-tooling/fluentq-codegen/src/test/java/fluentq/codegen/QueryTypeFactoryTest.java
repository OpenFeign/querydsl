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

import fluentq.codegen.utils.model.ClassType;
import fluentq.codegen.utils.model.Type;
import org.junit.Test;

public class QueryTypeFactoryTest {

  private Type type = new ClassType(Point.class);

  @Test
  public void prefix_only() {
    QueryTypeFactory factory = new QueryTypeFactoryImpl("Q", "", "");
    assertThat(factory.create(type).getFullName()).isEqualTo("fluentq.codegen.QPoint");
  }

  @Test
  public void prefix_and_suffix() {
    QueryTypeFactory factory = new QueryTypeFactoryImpl("Q", "Type", "");
    assertThat(factory.create(type).getFullName()).isEqualTo("fluentq.codegen.QPointType");
  }

  @Test
  public void suffix_only() {
    QueryTypeFactory factory = new QueryTypeFactoryImpl("", "Type", "");
    assertThat(factory.create(type).getFullName()).isEqualTo("fluentq.codegen.PointType");
  }

  @Test
  public void prefix_and_package_suffix() {
    QueryTypeFactory factory = new QueryTypeFactoryImpl("Q", "", ".query");
    assertThat(factory.create(type).getFullName()).isEqualTo("fluentq.codegen.query.QPoint");
  }
}
