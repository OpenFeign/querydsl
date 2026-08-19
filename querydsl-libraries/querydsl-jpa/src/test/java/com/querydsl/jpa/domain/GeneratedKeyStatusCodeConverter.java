package com.querydsl.jpa.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * AttributeConverter used by {@link GeneratedKeyEntity#getStatusConverted()} to verify that the
 * native INSERT path fails fast when an enum column is mapped with {@code @Convert} (custom
 * converters cannot be honored on the native path that bypasses JPA).
 *
 * <p>Kept as a top-level class so EclipseLink can discover the converter type via reflection during
 * persistence-unit predeployment.
 */
@Converter
public class GeneratedKeyStatusCodeConverter
    implements AttributeConverter<GeneratedKeyEntity.Status, String> {

  @Override
  public String convertToDatabaseColumn(GeneratedKeyEntity.Status attribute) {
    return attribute == null ? null : "code_" + attribute.name();
  }

  @Override
  public GeneratedKeyEntity.Status convertToEntityAttribute(String dbData) {
    if (dbData == null || !dbData.startsWith("code_")) {
      return null;
    }
    return GeneratedKeyEntity.Status.valueOf(dbData.substring("code_".length()));
  }
}
