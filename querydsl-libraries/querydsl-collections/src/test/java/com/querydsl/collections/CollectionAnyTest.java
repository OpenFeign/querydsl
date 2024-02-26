package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class CollectionAnyTest extends AbstractQueryTest {

  @Test
  public void any_null() {
    Cat a = new Cat("a");
    a.setKittens(null);

    assertThat(
            CollQueryFactory.<Cat>from(cat, Collections.<Cat>singletonList(a))
                .where(cat.kittens.any().name.startsWith("a"))
                .fetchCount())
        .isZero();
  }

  @Test
  public void any_in_projection() {
    Cat a = new Cat("a");
    Cat aa = new Cat("aa");
    Cat ab = new Cat("ab");
    Cat ac = new Cat("ac");
    a.setKittens(Arrays.asList(aa, ab, ac));

    Cat b = new Cat("b");
    Cat ba = new Cat("ba");
    Cat bb = new Cat("bb");
    b.setKittens(Arrays.asList(ba, bb));

    QCat cat = QCat.cat;
    List<Cat> kittens =
        CollQueryFactory.<Cat>from(cat, Arrays.<Cat>asList(a, b))
            .<Cat>select(cat.kittens.any())
            .fetch();
    assertThat(kittens).isEqualTo(Arrays.asList(aa, ab, ac, ba, bb));
  }

  @Test
  public void any_in_projection2() {
    Cat a = new Cat("a");
    Cat aa = new Cat("aa");
    Cat ab = new Cat("ab");
    Cat ac = new Cat("ac");
    a.setKittens(Arrays.asList(aa, ab, ac));

    Cat b = new Cat("b");
    Cat ba = new Cat("ba");
    Cat bb = new Cat("bb");
    b.setKittens(Arrays.asList(ba, bb));

    QCat cat = QCat.cat;
    List<String> kittens =
        CollQueryFactory.<Cat>from(cat, Arrays.<Cat>asList(a, b))
            .select(cat.kittens.any().name)
            .fetch();
    assertThat(kittens).isEqualTo(Arrays.asList("aa", "ab", "ac", "ba", "bb"));
  }

  @Test
  public void any_in_where_and_projection() {
    Cat a = new Cat("a");
    Cat aa = new Cat("aa");
    Cat ab = new Cat("ab");
    Cat ac = new Cat("ac");
    a.setKittens(Arrays.asList(aa, ab, ac));

    Cat b = new Cat("b");
    Cat ba = new Cat("ba");
    Cat bb = new Cat("bb");
    b.setKittens(Arrays.asList(ba, bb));

    QCat cat = QCat.cat;
    List<Cat> kittens =
        CollQueryFactory.<Cat>from(cat, Arrays.<Cat>asList(a, b))
            .where(cat.kittens.any().name.startsWith("a"))
            .select(cat.kittens.any())
            .fetch();

    assertThat(kittens).isEqualTo(Arrays.asList(aa, ab, ac));
  }

  @Test
  public void any_in_where_and_projection2() {
    Cat a = new Cat("a");
    Cat aa = new Cat("aa");
    Cat ab = new Cat("ab");
    Cat ac = new Cat("ac");
    a.setKittens(Arrays.asList(aa, ab, ac));

    Cat b = new Cat("b");
    Cat ba = new Cat("ba");
    Cat bb = new Cat("bb");
    b.setKittens(Arrays.asList(ba, bb));

    QCat cat = QCat.cat;
    List<String> kittens =
        CollQueryFactory.<Cat>from(cat, Arrays.<Cat>asList(a, b))
            .where(cat.kittens.any().name.startsWith("a"))
            .select(cat.kittens.any().name)
            .fetch();

    assertThat(kittens).isEqualTo(Arrays.asList("aa", "ab", "ac"));
  }
}
