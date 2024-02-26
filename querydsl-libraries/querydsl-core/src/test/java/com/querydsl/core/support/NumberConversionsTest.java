package com.querydsl.core.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QTuple;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.Test;

public class NumberConversionsTest {

  public enum Color {
    GREEN,
    BLUE,
    RED,
    YELLOW,
    BLACK,
    WHITE
  }

  @Test
  public void name() {
    EnumPath<Color> color = Expressions.enumPath(Color.class, "path");
    QTuple qTuple = Projections.tuple(color);
    NumberConversions<Tuple> conversions = new NumberConversions<Tuple>(qTuple);
    assertThat(conversions.newInstance("BLUE").get(color)).isEqualTo(Color.BLUE);
  }

  @Test
  public void ordinal() {
    EnumPath<Color> color = Expressions.enumPath(Color.class, "path");
    QTuple qTuple = Projections.tuple(color);
    NumberConversions<Tuple> conversions = new NumberConversions<Tuple>(qTuple);
    assertThat(conversions.newInstance(2).get(color)).isEqualTo(Color.RED);
  }

  @Test
  public void safe_number_conversion() {
    StringPath strPath = Expressions.stringPath("strPath");
    NumberPath<Integer> intPath = Expressions.numberPath(Integer.class, "intPath");
    QTuple qTuple = Projections.tuple(strPath, intPath);
    NumberConversions<Tuple> conversions = new NumberConversions<Tuple>(qTuple);
    assertThat(conversions.newInstance(1, 2)).isNotNull();
  }

  @Test
  public void number_conversion() {
    StringPath strPath = Expressions.stringPath("strPath");
    NumberPath<Integer> intPath = Expressions.numberPath(Integer.class, "intPath");
    QTuple qTuple = Projections.tuple(strPath, intPath);
    NumberConversions<Tuple> conversions = new NumberConversions<Tuple>(qTuple);
    Tuple tuple = conversions.newInstance("a", 3L);
    assertThat(tuple.get(strPath)).isEqualTo("a");
    assertThat(tuple.get(intPath)).isEqualTo(Integer.valueOf(3));
  }
}
