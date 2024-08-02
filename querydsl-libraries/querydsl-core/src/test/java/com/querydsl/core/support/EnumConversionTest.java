package com.querydsl.core.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;

public class EnumConversionTest {

  public enum Color {
    GREEN,
    BLUE,
    RED,
    YELLOW,
    B,
    W
  }

  @Test
  public void nameForCharacter() {
    EnumPath<Color> color = Expressions.enumPath(Color.class, "path");
    var conv = new EnumConversion<Color>(color);
    assertThat(conv.newInstance('W')).isEqualTo(Color.W);
  }

  @Test
  public void name() {
    EnumPath<Color> color = Expressions.enumPath(Color.class, "path");
    var conv = new EnumConversion<Color>(color);
    assertThat(conv.newInstance("BLUE")).isEqualTo(Color.BLUE);
  }

  @Test
  public void ordinal() {
    EnumPath<Color> color = Expressions.enumPath(Color.class, "path");
    var conv = new EnumConversion<Color>(color);
    assertThat(conv.newInstance(2)).isEqualTo(Color.RED);
  }

  @Test(expected = IllegalArgumentException.class)
  public void illegal() {
    var string = Expressions.stringPath("path");
    var conv = new EnumConversion<String>(string);
    fail("EnumConversion successfully created for a non-enum type");
    conv.newInstance(0);
  }
}
