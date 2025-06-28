package com.querydsl.core.types;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/** Verifies that TemplateFactory re-uses parsed Template objects. */
public class TemplateFactoryCacheTest {

  private final TemplateFactory factory = new TemplateFactory('\\');

  @Test
  public void identicalTemplateString_isReturnedFromCache() {
    Template t1 = factory.create("{0}");
    Template t2 = factory.create("{0}");

    assertThat(t1).isSameAs(t2);
  }

  @Test
  public void distinctTemplateStrings_areDifferentInstances() {
    Template hello = factory.create("{0}");
    Template world = factory.create("{1}");

    assertThat(hello).isNotSameAs(world);
  }
}
