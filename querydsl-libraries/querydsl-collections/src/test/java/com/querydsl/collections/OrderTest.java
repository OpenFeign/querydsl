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

public class OrderTest extends AbstractQueryTest {

  @Test
  public void test() {
    query().from(cat, cats).orderBy(cat.name.asc()).select(cat.name).fetch();
    assertThat(last.res.toArray())
        .containsExactly(new Object[] {"Alex", "Bob", "Francis", "Kitty"});

    query().from(cat, cats).orderBy(cat.name.desc()).select(cat.name).fetch();
    assertThat(last.res.toArray())
        .containsExactly(new Object[] {"Kitty", "Francis", "Bob", "Alex"});

    query().from(cat, cats).orderBy(cat.name.substring(1).asc()).select(cat.name).fetch();
    assertThat(last.res.toArray())
        .containsExactly(new Object[] {"Kitty", "Alex", "Bob", "Francis"});

    query()
        .from(cat, cats)
        .from(otherCat, cats)
        .orderBy(cat.name.asc(), otherCat.name.desc())
        .select(cat.name, otherCat.name)
        .fetch();

    // TODO : more tests
  }

  @Test
  public void test2() {
    List<String> orderedNames = Arrays.asList("Alex", "Bob", "Francis", "Kitty");
    assertThat(query().from(cat, cats).orderBy(cat.name.asc()).select(cat.name).fetch())
        .isEqualTo(orderedNames);
    assertThat(query().from(cat, cats).orderBy(cat.name.asc()).distinct().select(cat.name).fetch())
        .isEqualTo(orderedNames);
  }

  @Test
  public void with_count() {
    CollQuery<?> q = new CollQuery<Void>();
    q.from(cat, cats);
    var size = q.distinct().fetchCount();
    assertThat(size > 0).isTrue();
    q.offset(0).limit(10);
    q.orderBy(cat.name.asc());
    assertThat(q.distinct().select(cat.name).fetch())
        .isEqualTo(Arrays.asList("Alex", "Bob", "Francis", "Kitty"));
  }

  @Test
  public void with_null() {
    var unknown = new Cat();
    var bob = new Cat("Bob");
    var alex = new Cat("Alex");
    List<Cat> cats = Arrays.asList(alex, unknown, bob);
    assertThat(query().from(cat, cats).orderBy(cat.name.asc()).select(cat).fetch())
        .isEqualTo(Arrays.asList(unknown, alex, bob));
    assertThat(query().from(cat, cats).orderBy(cat.name.desc()).select(cat).fetch())
        .isEqualTo(Arrays.asList(unknown, bob, alex));
  }

  @Test
  public void with_nulls_last() {
    var unknown = new Cat();
    var bob = new Cat("Bob");
    var alex = new Cat("Alex");
    List<Cat> cats = Arrays.asList(alex, unknown, bob);
    assertThat(
            query()
                .from(this.cat, cats)
                .orderBy(this.cat.name.desc().nullsLast())
                .select(this.cat)
                .fetch())
        .isEqualTo(Arrays.asList(bob, alex, unknown));
    assertThat(
            query()
                .from(this.cat, cats)
                .orderBy(this.cat.name.asc().nullsLast())
                .select(this.cat)
                .fetch())
        .isEqualTo(Arrays.asList(alex, bob, unknown));
  }
}
