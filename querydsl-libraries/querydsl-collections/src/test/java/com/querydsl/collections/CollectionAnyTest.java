package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class CollectionAnyTest extends AbstractQueryTest {

  @Test
  public void any_null() {
    var a = new Cat("a");
    a.setKittens(null);

    assertThat(
            CollQueryFactory.<Cat>from(cat, Collections.<Cat>singletonList(a))
                .where(cat.kittens.any().name.startsWith("a"))
                .fetchCount())
        .isZero();
  }

  @Test
  public void any_in_projection() {
    var a = new Cat("a");
    var aa = new Cat("aa");
    var ab = new Cat("ab");
    var ac = new Cat("ac");
    a.setKittens(Arrays.asList(aa, ab, ac));

    var b = new Cat("b");
    var ba = new Cat("ba");
    var bb = new Cat("bb");
    b.setKittens(Arrays.asList(ba, bb));

    var cat = QCat.cat;
    var kittens =
        CollQueryFactory.<Cat>from(cat, Arrays.<Cat>asList(a, b))
            .<Cat>select(cat.kittens.any())
            .fetch();
    assertThat(kittens).isEqualTo(Arrays.asList(aa, ab, ac, ba, bb));
  }

  @Test
  public void any_in_projection2() {
    var a = new Cat("a");
    var aa = new Cat("aa");
    var ab = new Cat("ab");
    var ac = new Cat("ac");
    a.setKittens(Arrays.asList(aa, ab, ac));

    var b = new Cat("b");
    var ba = new Cat("ba");
    var bb = new Cat("bb");
    b.setKittens(Arrays.asList(ba, bb));

    var cat = QCat.cat;
    var kittens =
        CollQueryFactory.<Cat>from(cat, Arrays.<Cat>asList(a, b))
            .select(cat.kittens.any().name)
            .fetch();
    assertThat(kittens).isEqualTo(Arrays.asList("aa", "ab", "ac", "ba", "bb"));
  }

  @Test
  public void any_in_where_and_projection() {
    var a = new Cat("a");
    var aa = new Cat("aa");
    var ab = new Cat("ab");
    var ac = new Cat("ac");
    a.setKittens(Arrays.asList(aa, ab, ac));

    var b = new Cat("b");
    var ba = new Cat("ba");
    var bb = new Cat("bb");
    b.setKittens(Arrays.asList(ba, bb));

    var cat = QCat.cat;
    var kittens =
        CollQueryFactory.<Cat>from(cat, Arrays.<Cat>asList(a, b))
            .where(cat.kittens.any().name.startsWith("a"))
            .select(cat.kittens.any())
            .fetch();

    assertThat(kittens).isEqualTo(Arrays.asList(aa, ab, ac));
  }

  @Test
  public void any_in_where_and_projection2() {
    var a = new Cat("a");
    var aa = new Cat("aa");
    var ab = new Cat("ab");
    var ac = new Cat("ac");
    a.setKittens(Arrays.asList(aa, ab, ac));

    var b = new Cat("b");
    var ba = new Cat("ba");
    var bb = new Cat("bb");
    b.setKittens(Arrays.asList(ba, bb));

    var cat = QCat.cat;
    var kittens =
        CollQueryFactory.<Cat>from(cat, Arrays.<Cat>asList(a, b))
            .where(cat.kittens.any().name.startsWith("a"))
            .select(cat.kittens.any().name)
            .fetch();

    assertThat(kittens).isEqualTo(Arrays.asList("aa", "ab", "ac"));
  }
}
