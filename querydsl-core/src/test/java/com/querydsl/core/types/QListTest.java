package com.querydsl.core.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.querydsl.core.types.dsl.Expressions;
import java.util.List;
import org.junit.Test;

public class QListTest {

  @Test
  public void newInstance() {
    QList qList = new QList(Expressions.stringPath("a"), Expressions.stringPath("b"));
    List<?> list = qList.newInstance("a", null);
    assertEquals(2, list.size());
    assertEquals("a", list.getFirst());
    assertNull(list.get(1));
  }
}
