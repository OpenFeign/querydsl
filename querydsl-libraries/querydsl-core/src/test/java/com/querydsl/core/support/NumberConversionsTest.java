package com.querydsl.core.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.jupiter.api.Test;

class NumberConversionsTest {

  public enum Color {
    GREEN,
    BLUE,
    RED,
    YELLOW,
    BLACK,
    WHITE
  }

  @Test
  void name() {
    var color = Expressions.enumPath(Color.class, "path");
    var qTuple = Projections.tuple(color);
    var conversions = new NumberConversions<Tuple>(qTuple);
    assertThat(conversions.newInstance("BLUE").get(color)).isEqualTo(Color.BLUE);
  }

  @Test
  void ordinal() {
    var color = Expressions.enumPath(Color.class, "path");
    var qTuple = Projections.tuple(color);
    var conversions = new NumberConversions<Tuple>(qTuple);
    assertThat(conversions.newInstance(2).get(color)).isEqualTo(Color.RED);
  }

  @Test
  void safe_number_conversion() {
    var strPath = Expressions.stringPath("strPath");
    var intPath = Expressions.numberPath(Integer.class, "intPath");
    var qTuple = Projections.tuple(strPath, intPath);
    var conversions = new NumberConversions<Tuple>(qTuple);
    assertThat(conversions.newInstance(1, 2)).isNotNull();
  }

  @Test
  void number_conversion() {
    var strPath = Expressions.stringPath("strPath");
    var intPath = Expressions.numberPath(Integer.class, "intPath");
    var qTuple = Projections.tuple(strPath, intPath);
    var conversions = new NumberConversions<Tuple>(qTuple);
    var tuple = conversions.newInstance("a", 3L);
    assertThat(tuple.get(strPath)).isEqualTo("a");
    assertThat(tuple.get(intPath)).isEqualTo(Integer.valueOf(3));
  }
}
