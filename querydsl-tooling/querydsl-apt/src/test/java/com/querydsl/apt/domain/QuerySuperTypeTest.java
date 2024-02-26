package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QuerySupertype;
import java.util.Set;
import javax.jdo.annotations.PersistenceCapable;
import org.junit.Test;

public class QuerySuperTypeTest {

  @QuerySupertype
  public static class Supertype {}

  @PersistenceCapable
  public static class JdoEntity {
    Set<Supertype> references;
  }

  @Test
  public void jdoEntity() {
    assertThat(QQuerySuperTypeTest_JdoEntity.jdoEntity.references.any().getClass())
        .isEqualTo(QQuerySuperTypeTest_Supertype.class);
  }
}
