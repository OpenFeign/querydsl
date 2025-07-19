package com.querydsl.core.types.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.domain.QCat;
import org.junit.Test;

/** Ensures ListPath index caching works as intended. */
public class ListPathCachingTest {

  private static final QCat CAT = QCat.cat;

  @Test
  public void sameIndex_returnsSamePathInstance() {
    QCat first  = CAT.kittens(0);
    QCat second = CAT.kittens(0);

    assertThat(first).isSameAs(second);
  }

  @Test
  public void differentIndices_returnDistinctPathInstances() {
    QCat zero = CAT.kittens(0);
    QCat one  = CAT.kittens(1);

    assertThat(zero).isNotSameAs(one);
  }
}
