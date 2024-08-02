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
package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class CollUpdateClauseTest {

  @Test
  public void execute() {
    var cat = QCat.cat;
    List<Cat> cats =
        Arrays.asList(new Cat("Ann"), new Cat("Bob"), new Cat("John"), new Cat("Carl"));

    var updateClause = new CollUpdateClause<>(cat, cats);
    updateClause.where(cat.name.eq("Bob"));
    updateClause.set(cat.name, "Bobby");
    assertThat(updateClause.execute()).isEqualTo(1);

    assertThat(cats.get(1).getName()).isEqualTo("Bobby");
  }
}
