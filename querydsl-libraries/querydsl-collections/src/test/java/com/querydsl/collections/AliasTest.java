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

import static com.querydsl.collections.CollQueryFactory.from;
import static com.querydsl.core.alias.Alias.$;
import static com.querydsl.core.alias.Alias.alias;
import static com.querydsl.core.alias.Alias.var;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.querydsl.core.alias.Alias;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;

public class AliasTest extends AbstractQueryTest {

  @Override
  @Before
  public void setUp() {
    myInts.add(1);
    myInts.add(2);
    myInts.add(3);
    myInts.add(4);

    Alias.resetAlias();
  }

  @Test
  public void aliasVariations1() {
    // 1st
    var cat = new QCat("cat");
    assertThat(from(cat, cats).where(cat.kittens.size().gt(0)).select(cat.name).fetch())
        .isEqualTo(Arrays.asList("Kitty", "Bob", "Alex", "Francis"));

    // 2nd
    var c = alias(Cat.class, "cat");
    assertThat(from(c, cats).where($(c.getKittens()).size().gt(0)).select($(c.getName())).fetch())
        .isEqualTo(Arrays.asList("Kitty", "Bob", "Alex", "Francis"));
  }

  @Test
  public void aliasVariations2() {
    // 1st
    var cat = new QCat("cat");
    assertThat(from(cat, cats).where(cat.name.matches("fri.*")).select(cat.name).fetch()).isEmpty();

    // 2nd
    var c = alias(Cat.class, "cat");
    assertThat(from(c, cats).where($(c.getName()).matches("fri.*")).select($(c.getName())).fetch())
        .isEqualTo(Collections.emptyList());
  }

  @Test
  public void alias3() {
    var cat = new QCat("cat");
    var other = new Cat();
    var c = alias(Cat.class, "cat");

    // 1
    from(c, cats).where($(c.getBirthdate()).gt(new Date())).select($(c)).iterate();

    // 2
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> from(c, cats).where($(c.getMate().getName().toUpperCase()).eq("MOE")));

    // 3
    assertThat($(c.getName())).isEqualTo(cat.name);

    // 4
    from(c, cats)
        .where($(c.getKittens().getFirst().getBodyWeight()).gt(12))
        .select($(c.getName()))
        .iterate();

    // 5
    from(c, cats).where($(c).eq(other)).select($(c)).iterate();

    // 6
    from(c, cats).where($(c.getKittens()).contains(other)).select($(c)).iterate();

    // 7
    from(c, cats).where($(c.getKittens().isEmpty())).select($(c)).iterate();

    // 8
    from(c, cats).where($(c.getName()).startsWith("B")).select($(c)).iterate();

    // 9
    from(c, cats).where($(c.getName()).upper().eq("MOE")).select($(c)).iterate();

    // 10
    assertThat($(c.getKittensByName())).isNotNull();
    assertThat($(c.getKittensByName().get("Kitty"))).isNotNull();
    from(c, cats).where($(c.getKittensByName().get("Kitty")).isNotNull()).select(cat).iterate();

    // 11
    //        try {
    //            from(cat, cats).where(cat.mate.alive).fetch(cat);
    //            fail("expected RuntimeException");
    //        } catch (RuntimeException e) {
    //            System.out.println(e.getMessage());
    //            assertEquals("null in cat.mate.alive", e.getMessage());
    //        }

    // 12
    // TestQuery query = query().from(cat, c1, c2).from(cat, c1, c2);
    // assertEquals(1, query.getMetadata().getJoins().size());

  }

  @Test
  public void various1() {
    var str = Expressions.stringPath("str");
    assertThat(from(str, "a", "ab", "cd", "de").where(str.startsWith("a")).select(str).fetch())
        .isEqualTo(Arrays.asList("a", "ab"));
  }

  @Test
  public void various2() {
    assertThat(from(var(), 1, 2, "abc", 5, 3).where(var().ne("abc")).select(var()).fetch())
        .isEqualTo(Arrays.asList(1, 2, 5, 3));
  }

  @Test
  public void various3() {
    NumberPath<Integer> num = Expressions.numberPath(Integer.class, "num");
    assertThat(from(num, 1, 2, 3, 4).where(num.lt(4)).select(num).fetch())
        .isEqualTo(Arrays.asList(1, 2, 3));
  }
}
