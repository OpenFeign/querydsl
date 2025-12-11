package com.querydsl.example.ksp

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.math.BigDecimal

@Converter
class MoneyConverter : AttributeConverter<Money, BigDecimal> {
    
    override fun convertToDatabaseColumn(attribute: Money?): BigDecimal? {
        return attribute?.value
    }
    
    override fun convertToEntityAttribute(dbData: BigDecimal?): Money? {
        return dbData?.let { Money(it) }
    }
}

