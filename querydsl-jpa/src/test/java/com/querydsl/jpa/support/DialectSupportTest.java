package com.querydsl.jpa.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Template;
import com.querydsl.sql.HSQLDBTemplates;
import org.junit.Test;

public class DialectSupportTest {

  @Test
  public void convert() {
    Template trim = HSQLDBTemplates.DEFAULT.getTemplate(Ops.TRIM);
    assertThat(DialectSupport.convert(trim)).isEqualTo("trim(both from ?1)");
    Template concat = HSQLDBTemplates.DEFAULT.getTemplate(Ops.CONCAT);
    assertThat(DialectSupport.convert(concat)).isEqualTo("?1 || ?2");
  }
}
