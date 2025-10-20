package com.querydsl.example.ksp

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class BearSpeciesConverter : AttributeConverter<BearSpecies, String> {
    
    override fun convertToDatabaseColumn(attribute: BearSpecies?): String? {
        return attribute?.name?.lowercase()
    }
    
    override fun convertToEntityAttribute(dbData: String?): BearSpecies? {
        return dbData?.let { 
            try {
                BearSpecies.valueOf(it.uppercase())
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
