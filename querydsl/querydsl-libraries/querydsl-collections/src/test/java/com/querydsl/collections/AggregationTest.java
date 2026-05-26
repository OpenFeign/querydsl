package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;

public class AggregationTest extends AbstractQueryTest {

  private static final QCat cat = QCat.cat;

  private CollQuery<?> query;

  @Override
  @Before
  public void setUp() {
    var cat1 = new Cat();
    cat1.setWeight(2);
    var cat2 = new Cat();
    cat2.setWeight(3);
    var cat3 = new Cat();
    cat3.setWeight(4);
    var cat4 = new Cat();
    cat4.setWeight(5);
    query = CollQueryFactory.<Cat>from(cat, Arrays.asList(cat1, cat2, cat3, cat4));
  }

  @Test
  public void avg() {
    assertThat(query.select(cat.weight.avg()).fetchOne()).isCloseTo(3.5, within(0.0));
  }

  @Test
  public void count() {
    assertThat(query.select(cat.count()).fetchOne()).isEqualTo(Long.valueOf(4L));
  }

  @Test
  public void countDistinct() {
    assertThat(query.select(cat.countDistinct()).fetchOne()).isEqualTo(Long.valueOf(4L));
  }

  @Test
  public void max() {
    assertThat(query.select(cat.weight.max()).fetchOne()).isEqualTo(Integer.valueOf(5));
  }

  @Test
  public void min() {
    assertThat(query.select(cat.weight.min()).fetchOne()).isEqualTo(Integer.valueOf(2));
  }

  @SuppressWarnings("unchecked")
  @Test(expected = UnsupportedOperationException.class)
  public void min_and_max() {
    query.select(cat.weight.min(), cat.weight.max()).fetchOne();
  }

  @Test
  public void sum() {
    assertThat(query.select(cat.weight.sumAggregate()).fetchOne()).isEqualTo(Integer.valueOf(14));
  }
}
