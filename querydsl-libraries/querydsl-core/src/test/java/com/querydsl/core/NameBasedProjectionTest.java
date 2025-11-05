package com.querydsl.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.domain.QAnimal;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.NameBasedProjection;
import java.sql.Date;
import org.junit.Test;

public class NameBasedProjectionTest {

  public static class AnimalDTO {
    private boolean alive;
    private double bodyWeight;
    private Date dateField;
    private int id;
    private String name;
    private int toes;
    private int weight;

    // Constructor with all fields
    public AnimalDTO(
        boolean alive,
        double bodyWeight,
        Date dateField,
        int id,
        String name,
        int toes,
        int weight) {
      this.alive = alive;
      this.bodyWeight = bodyWeight;
      this.dateField = dateField;
      this.id = id;
      this.name = name;
      this.toes = toes;
      this.weight = weight;
    }

    // Default constructor
    public AnimalDTO() {}

    // Getters and setters
    public boolean isAlive() {
      return alive;
    }

    public void setAlive(boolean alive) {
      this.alive = alive;
    }

    public double getBodyWeight() {
      return bodyWeight;
    }

    public void setBodyWeight(double bodyWeight) {
      this.bodyWeight = bodyWeight;
    }

    public Date getDateField() {
      return dateField;
    }

    public void setDateField(Date dateField) {
      this.dateField = dateField;
    }

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getToes() {
      return toes;
    }

    public void setToes(int toes) {
      this.toes = toes;
    }

    public int getWeight() {
      return weight;
    }

    public void setWeight(int weight) {
      this.weight = weight;
    }
  }

  @Test
  public void testSimpleDTOProjectionWithFields() {
    QAnimal animal = QAnimal.animal;

    // Create a Date object for testing
    Date testDate = new Date(System.currentTimeMillis());

    // Create DTO using Projections.fields()
    AnimalDTO dtoFromProjections =
        Projections.fields(
                AnimalDTO.class,
                animal.alive,
                animal.bodyWeight,
                animal.dateField,
                animal.id,
                animal.name,
                animal.toes,
                animal.weight)
            .newInstance(
                true, // alive
                65.5, // bodyWeight
                testDate, // dateField
                1, // id
                "Lion", // name
                4, // toes
                150 // weight
                );

    // Create DTO using SimpleDTOProjection.fields()
    AnimalDTO dtoFromSimpleDTOProjection =
        NameBasedProjection.binder(AnimalDTO.class, animal)
            .newInstance(
                true, // alive
                65.5, // bodyWeight
                testDate, // dateField
                1, // id
                "Lion", // name
                4, // toes
                150 // weight
                );

    // Compare each field value
    assertThat(dtoFromSimpleDTOProjection)
        .isNotNull()
        .usingRecursiveComparison()
        .isEqualTo(dtoFromProjections);
  }
}
