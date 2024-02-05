package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QueryDelegate;
import com.querydsl.core.types.Predicate;
import org.junit.Test;

public class QueryByExampleTest {

  @QueryDelegate(ExampleEntity.class)
  public static Predicate like(QExampleEntity qtype, ExampleEntity example) {
    return example.name != null ? qtype.name.eq(example.name) : null;
  }

  @Test
  public void name_not_set() {
    ExampleEntity entity = new ExampleEntity();
    Predicate qbe = QExampleEntity.exampleEntity.like(entity);
    assertThat(qbe).isNull();
  }

  @Test
  public void name_set() {
    ExampleEntity entity = new ExampleEntity();
    entity.name = "XXX";
    Predicate qbe = QExampleEntity.exampleEntity.like(entity);
    assertThat(qbe.toString()).isEqualTo("exampleEntity.name = XXX");
  }
}
