package com.querydsl.core.types;

import com.querydsl.core.testutil.Serialization;
import com.querydsl.core.types.dsl.Expressions;
import java.io.IOException;
import org.junit.jupiter.api.Test;

class ExpressionSerializationTest {

  @Test
  void serialize() throws Exception {
    var e = new QTuple(Expressions.stringPath("x"), Expressions.numberPath(Integer.class, "y"));
    serialize(e);
    serialize(e.newInstance("a", 1));
  }

  private void serialize(Object obj) throws IOException, ClassNotFoundException {
    var obj2 = Serialization.serialize(obj);
    obj2.hashCode();
  }
}
