package com.querydsl.mongodb.domain;

import dev.morphia.converters.SimpleValueConverter;
import dev.morphia.converters.TypeConverter;
import dev.morphia.mapping.MappedField;
import dev.morphia.mapping.MappingException;
import java.util.Locale;

public class LocaleConverter extends TypeConverter implements SimpleValueConverter {

  public LocaleConverter() {
    super(Locale.class);
  }

  @Override
  public final Object encode(Object value, MappedField optionalExtraInfo) throws MappingException {
    if (value == null) {
      return null;
    }
    if (!(value instanceof Locale)) {
      throw new MappingException("Unable to convert " + value.getClass().getName());
    }
    return ((Locale) value).toLanguageTag();
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Locale decode(Class targetClass, Object fromDBObject, MappedField optionalExtraInfo)
      throws MappingException {
    if (fromDBObject == null) {
      return null;
    }
    if (fromDBObject instanceof String string) {
      return Locale.forLanguageTag(string);
    }
    throw new MappingException("Unable to convert " + fromDBObject.getClass().getName());
  }
}
