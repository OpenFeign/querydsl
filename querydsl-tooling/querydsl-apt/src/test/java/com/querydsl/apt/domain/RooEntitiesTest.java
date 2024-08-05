package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class RooEntitiesTest {

  @Test
  public void rooJpaEntity() {
    assertThat(QRooEntities_MyEntity.myEntity).isNotNull();
  }

  @Test
  public void rooJpaActiveRecord() {
    assertThat(QRooEntities_MyEntity2.myEntity2).isNotNull();
  }
}
