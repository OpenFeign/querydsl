package com.querydsl.core.types;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.Expressions;
import java.util.List;
import org.junit.jupiter.api.Test;

class QListTest {

  @Test
  void newInstance() {
    var qList = new QList(Expressions.stringPath("a"), Expressions.stringPath("b"));
    List<?> list = qList.newInstance("a", null);
    assertThat(list).hasSize(2);
    assertThat(list).first().isEqualTo("a");
    assertThat(list.get(1)).isNull();
  }
}
