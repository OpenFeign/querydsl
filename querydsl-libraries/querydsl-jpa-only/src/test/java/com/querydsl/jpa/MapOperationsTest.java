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

import static com.querydsl.jpa.Constants.show;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQuery;
import org.junit.Test;

public class MapOperationsTest extends AbstractQueryTest {

  @Test
  public void map_with_groupBy() {
    assertThat(
            new JPAQuery<Void>()
                .from(show)
                .select(show.acts.get("A"))
                .groupBy(show.acts.get("A"))
                .toString())
        .isEqualTo(
            """
            select show_acts_0
            from Show show
              left join show.acts as show_acts_0 on key(show_acts_0) = ?1
            group by show_acts_0\
            """);
  }
}
