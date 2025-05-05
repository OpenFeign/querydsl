package com.querydsl.mongodb.domain;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import java.util.Locale;

@Entity
@Converters(LocaleConverter.class)
public class Country extends AbstractEntity {
  private String name;
  private Locale defaultLocale;

  Country() {}

  public Country(String name, Locale defaultLocale) {
    this.name = name;
    this.defaultLocale = defaultLocale;
  }

  public String getName() {
    return name;
  }

  public Locale getDefaultLocale() {
    return defaultLocale;
  }
}
