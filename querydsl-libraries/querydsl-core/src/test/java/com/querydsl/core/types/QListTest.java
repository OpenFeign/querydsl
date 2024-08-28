package com.querydsl.core.types;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.Expressions;
import java.util.List;
import org.junit.Test;

public class QListTest {

  @Test
  public void newInstance() {
    var qList = new QList(Expressions.stringPath("a"), Expressions.stringPath("b"));
    List<?> list = qList.newInstance("a", null);
    assertThat(list).hasSize(2);
    assertThat(list.getFirst()).isEqualTo("a");
    assertThat(list.get(1)).isNull();
  }
}
