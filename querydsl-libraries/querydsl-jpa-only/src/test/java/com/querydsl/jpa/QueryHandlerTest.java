package com.querydsl.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class QueryHandlerTest {

  @Test
  public void types() {
    assertThat(EclipseLinkTemplates.DEFAULT.getQueryHandler().getClass())
        .isEqualTo(EclipseLinkHandler.class);
    assertThat(HQLTemplates.DEFAULT.getQueryHandler().getClass()).isEqualTo(HibernateHandler.class);
    assertThat(JPQLTemplates.DEFAULT.getQueryHandler().getClass())
        .isEqualTo(DefaultQueryHandler.class);
  }
}
