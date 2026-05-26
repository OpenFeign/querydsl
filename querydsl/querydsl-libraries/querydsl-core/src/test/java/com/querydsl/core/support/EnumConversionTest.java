package com.querydsl.core.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.jupiter.api.Test;

class EnumConversionTest {

  public enum Color {
    GREEN,
    BLUE,
    RED,
    YELLOW,
    B,
    W
  }

  @Test
  void nameForCharacter() {
    EnumPath<Color> color = Expressions.enumPath(Color.class, "path");
    var conv = new EnumConversion<Color>(color);
    assertThat(conv.newInstance('W')).isEqualTo(Color.W);
  }

  @Test
  void name() {
    EnumPath<Color> color = Expressions.enumPath(Color.class, "path");
    var conv = new EnumConversion<Color>(color);
    assertThat(conv.newInstance("BLUE")).isEqualTo(Color.BLUE);
  }

  @Test
  void ordinal() {
    EnumPath<Color> color = Expressions.enumPath(Color.class, "path");
    var conv = new EnumConversion<Color>(color);
    assertThat(conv.newInstance(2)).isEqualTo(Color.RED);
  }

  @Test
  void illegal() {
    var string = Expressions.stringPath("path");
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new EnumConversion<String>(string));
  }
}
