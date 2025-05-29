package com.querydsl.jpa.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.math.BigDecimal;

@Converter(autoApply = true)
public class MoneyConverter implements AttributeConverter<Money, BigDecimal> {

  @Override
  public BigDecimal convertToDatabaseColumn(Money attribute) {
    return attribute != null ? attribute.getValue() : null;
  }

  @Override
  public Money convertToEntityAttribute(BigDecimal dbData) {
    return dbData != null ? new Money(dbData) : null;
  }
}
