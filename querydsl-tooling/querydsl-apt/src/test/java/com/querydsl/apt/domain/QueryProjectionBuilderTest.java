package com.querydsl.apt.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;

public class QueryProjectionBuilderTest {

  @Test
  public void builder_buildsQClassCorrectly() {
    var property = Expressions.stringPath("x");
    QQueryProjectionBuilderTestEntity dto =
        QQueryProjectionBuilderTestEntity.builderTest1().setProperty(property).build();

    assertNotNull(dto);
    assertEquals(QueryProjectionBuilderTestEntity.class, dto.getType());
  }

  @Test
  public void builder_buildsQClassCorrectly2() {
    var property = Expressions.stringPath("x");
    var intProperty = Expressions.numberPath(Integer.class, "y");
    QQueryProjectionBuilderTestEntity dto =
        QQueryProjectionBuilderTestEntity.builderTest2()
            .setProperty(property)
            .setIntProperty(intProperty)
            .build();

    assertNotNull(dto);
    assertEquals(QueryProjectionBuilderTestEntity.class, dto.getType());
  }
}
