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
package fluentq.collections;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.core.DefaultQueryMetadata;
import fluentq.core.JoinType;
import fluentq.core.QueryMetadata;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class QueryMetadataTest extends AbstractQueryTest {

  @Test
  public void reusage() {
    QueryMetadata metadata = new DefaultQueryMetadata();
    metadata.addJoin(JoinType.DEFAULT, cat);
    metadata.addWhere(cat.name.startsWith("A"));

    CollQuery<?> query = new CollQuery<Void>(metadata);
    query.bind(cat, cats);
    assertThat(query.select(cat).fetch()).isEqualTo(Collections.singletonList(c3));
  }
}
