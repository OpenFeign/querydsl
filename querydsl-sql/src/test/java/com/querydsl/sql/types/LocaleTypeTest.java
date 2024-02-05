package com.querydsl.sql.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.Test;

public class LocaleTypeTest {

  @Test
  public void lang() {
    Locale l = Locale.of("en");
    assertThat(LocaleType.toLocale(l.toString())).isEqualTo(l);
  }

  @Test
  public void lang_country() {
    Locale l = Locale.of("en", "US");
    assertThat(LocaleType.toLocale(l.toString())).isEqualTo(l);
  }

  @Test
  public void lang_country_variant() {
    Locale l = Locale.of("en", "US", "X");
    assertThat(LocaleType.toLocale(l.toString())).isEqualTo(l);
  }
}
