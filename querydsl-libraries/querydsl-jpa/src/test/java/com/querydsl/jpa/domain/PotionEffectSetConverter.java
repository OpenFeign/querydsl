package com.querydsl.jpa.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class PotionEffectSetConverter
    implements AttributeConverter<Set<PotionEffect>, String>, Serializable {
  private static final long serialVersionUID = 4L;
  private static final String DELIMITER = ";";

  @Override
  public String convertToDatabaseColumn(Set<PotionEffect> attribute) {
    if (attribute == null || attribute.isEmpty()) {
      return null;
    }
    return attribute.stream().map(PotionEffect::name).collect(Collectors.joining(DELIMITER));
  }

  @Override
  public Set<PotionEffect> convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.trim().isEmpty()) {
      return Collections.emptySet();
    }
    String[] effectNames = dbData.split(DELIMITER);
    Set<PotionEffect> effects = EnumSet.noneOf(PotionEffect.class);
    for (String effectName : effectNames) {
      try {
        if (!effectName.trim().isEmpty()) {
          effects.add(PotionEffect.valueOf(effectName.trim()));
        }
      } catch (IllegalArgumentException e) {
        System.err.println("Warning: Could not convert '" + effectName + "' to PotionEffect enum.");
      }
    }
    return effects;
  }
}
