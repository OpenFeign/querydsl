package com.querydsl.core;

import com.querydsl.core.domain.QAnimal;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SimpleDTOProjection;
import org.junit.Test;

import java.sql.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SimpleDTOProjectionTest {

    // DTO class for animal data
    public static class AnimalDTO {
        private boolean alive;
        private double bodyWeight;
        private Date dateField;
        private int id;
        private String name;
        private int toes;
        private int weight;

        // Constructor with all fields
        public AnimalDTO(boolean alive, double bodyWeight, Date dateField, int id, String name, int toes, int weight) {
            this.alive = alive;
            this.bodyWeight = bodyWeight;
            this.dateField = dateField;
            this.id = id;
            this.name = name;
            this.toes = toes;
            this.weight = weight;
        }

        // Default constructor
        public AnimalDTO() {
        }

        // Getters and setters
        public boolean isAlive() { return alive; }
        public void setAlive(boolean alive) { this.alive = alive; }

        public double getBodyWeight() { return bodyWeight; }
        public void setBodyWeight(double bodyWeight) { this.bodyWeight = bodyWeight; }

        public Date getDateField() { return dateField; }
        public void setDateField(Date dateField) { this.dateField = dateField; }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getToes() { return toes; }
        public void setToes(int toes) { this.toes = toes; }

        public int getWeight() { return weight; }
        public void setWeight(int weight) { this.weight = weight; }
    }

    @Test
    public void testSimpleDTOProjectionWithFields() {
        QAnimal animal = QAnimal.animal;

        // Create a Date object for testing
        Date testDate = new Date(System.currentTimeMillis());

        // 1. Create DTO using Projections.fields()
        AnimalDTO dtoFromProjections = Projections.fields(
                AnimalDTO.class,
                animal.alive,
                animal.bodyWeight,
                animal.dateField,
                animal.id,
                animal.name,
                animal.toes,
                animal.weight
        ).newInstance(
                true,      // alive
                65.5,      // bodyWeight
                testDate,  // dateField
                1,         // id
                "Lion",    // name
                4,         // toes
                150        // weight
        );

        // 2. Create DTO using SimpleDTOProjection.fields()
        AnimalDTO dtoFromSimpleDTOProjection = SimpleDTOProjection.fields(AnimalDTO.class, animal).newInstance(
                true,      // alive
                65.5,      // bodyWeight
                testDate,  // dateField
                1,         // id
                "Lion",    // name
                4,         // toes
                150        // weight
        );

        // 3. Compare the two DTOs
        assertNotNull(dtoFromProjections);
        assertNotNull(dtoFromSimpleDTOProjection);

        // Compare each field value
        assertEquals(dtoFromProjections.isAlive(), dtoFromSimpleDTOProjection.isAlive());
        assertEquals(dtoFromProjections.getBodyWeight(), dtoFromSimpleDTOProjection.getBodyWeight(), 0.001);
        assertEquals(dtoFromProjections.getDateField(), dtoFromSimpleDTOProjection.getDateField());
        assertEquals(dtoFromProjections.getId(), dtoFromSimpleDTOProjection.getId());
        assertEquals(dtoFromProjections.getName(), dtoFromSimpleDTOProjection.getName());
        assertEquals(dtoFromProjections.getToes(), dtoFromSimpleDTOProjection.getToes());
        assertEquals(dtoFromProjections.getWeight(), dtoFromSimpleDTOProjection.getWeight());
    }
}