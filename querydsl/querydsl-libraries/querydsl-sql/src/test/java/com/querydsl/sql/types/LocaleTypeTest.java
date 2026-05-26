package com.querydsl.sql.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.Test;

public class LocaleTypeTest {

  @Test
  public void lang() {
    var l = Locale.of("en");
    assertThat(LocaleType.toLocale(l.toString())).isEqualTo(l);
  }

  @Test
  public void lang_country() {
    var l = Locale.of("en", "US");
    assertThat(LocaleType.toLocale(l.toString())).isEqualTo(l);
  }

  @Test
  public void lang_country_variant() {
    var l = Locale.of("en", "US", "X");
    assertThat(LocaleType.toLocale(l.toString())).isEqualTo(l);
  }
}
