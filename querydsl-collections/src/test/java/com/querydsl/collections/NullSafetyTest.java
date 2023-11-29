package com.querydsl.collections;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import org.junit.Test;

public class NullSafetyTest extends AbstractQueryTest {

  @Test
  public void filters() {
    QCat cat = QCat.cat;
    CollQuery<Cat> query =
        CollQueryFactory.<Cat>from(cat, Arrays.<Cat>asList(new Cat(), new Cat("Bob")));
    assertEquals(1L, query.where(cat.name.eq("Bob")).fetchCount());
  }

  @Test
  public void joins() {
    Cat kitten1 = new Cat();
    Cat kitten2 = new Cat("Bob");
    Cat cat1 = new Cat();
    cat1.setKittens(Arrays.asList(kitten1, kitten2));
    Cat cat2 = new Cat();

    QCat cat = QCat.cat;
    QCat kitten = new QCat("kitten");
    CollQuery<Cat> query =
        CollQueryFactory.<Cat>from(cat, Arrays.<Cat>asList(cat1, cat2))
            .innerJoin(cat.kittens, kitten);
    assertEquals(1, query.where(kitten.name.eq("Bob")).fetchCount());
  }
}
