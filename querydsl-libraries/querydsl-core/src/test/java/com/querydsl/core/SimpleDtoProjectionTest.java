package com.querydsl.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.domain.QAnimal;
import com.querydsl.core.types.SimpleDtoProjection;
import com.querydsl.core.types.Projections;
import java.sql.Date;
import org.junit.Test;

public class SimpleDtoProjectionTest {

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

    // Create DTO using NameBasedProjection.binder
    AnimalDTO dtoFromSimpleDTOProjection =
        SimpleDtoProjection.binder(AnimalDTO.class, animal)
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

  /**
   * DTO class that merges Animal and Cat entity fields. Used only for testing multi-entity
   * NameBasedProjection behavior.
   */
  public static class AnimalCatDTO {
    private boolean alive;
    private double bodyWeight;
    private Date dateField;
    private int id;
    private String name;
    private int toes;
    private int weight;
    private int breed; // Field from Cat entity

    // Constructor with all fields
    public AnimalCatDTO(
        boolean alive,
        double bodyWeight,
        Date dateField,
        int id,
        String name,
        int toes,
        int weight,
        int breed) {
      this.alive = alive;
      this.bodyWeight = bodyWeight;
      this.dateField = dateField;
      this.id = id;
      this.name = name;
      this.toes = toes;
      this.weight = weight;
      this.breed = breed;
    }

    // Default constructor
    public AnimalCatDTO() {}

    // Getters
    public boolean isAlive() {
      return alive;
    }

    public double getBodyWeight() {
      return bodyWeight;
    }

    public Date getDateField() {
      return dateField;
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public int getToes() {
      return toes;
    }

    public int getWeight() {
      return weight;
    }

    public int getBreed() {
      return breed;
    }

    @Override
    public String toString() {
      return "AnimalCatDTO{"
          + "alive="
          + alive
          + ", bodyWeight="
          + bodyWeight
          + ", dateField="
          + dateField
          + ", id="
          + id
          + ", name='"
          + name
          + '\''
          + ", toes="
          + toes
          + ", weight="
          + weight
          + ", breed="
          + breed
          + '}';
    }
  }

  /**
   * Test combining multiple entities (QAnimal and QCat) into one DTO using NameBasedProjection.
   * This verifies that: - Multiple EntityPathBase arguments are supported. - Fields from both
   * entities are mapped correctly. - When duplicate field names exist, the first entity's field
   * (QAnimal) takes precedence.
   */
  @Test
  public void testMultiEntityProjectionWithAnimalAndCat() {
    QAnimal animal = QAnimal.animal;
    com.querydsl.core.domain.QCat cat = com.querydsl.core.domain.QCat.cat;

    // Create a Date object for testing
    Date testDate = new Date(System.currentTimeMillis());

    // Create a DTO that combines fields from both Animal and Cat entities
    AnimalCatDTO dto =
        new SimpleDtoProjection<>(AnimalCatDTO.class, animal, cat)
            .newInstance(
                true, // alive (from Animal)
                55.2, // bodyWeight (from Animal)
                testDate, // dateField (from Animal)
                10, // id (from Animal)
                "Kitty", // name (from Animal)
                4, // toes (from Animal)
                7, // weight (from Animal)
                2 // breed (from Cat)
                );

    // Verify that all fields are correctly mapped
    assertThat(dto).isNotNull();
    assertThat(dto.isAlive()).isTrue();
    assertThat(dto.getBodyWeight()).isEqualTo(55.2);
    assertThat(dto.getDateField()).isEqualTo(testDate);
    assertThat(dto.getId()).isEqualTo(10);
    assertThat(dto.getName()).isEqualTo("Kitty");
    assertThat(dto.getToes()).isEqualTo(4);
    assertThat(dto.getWeight()).isEqualTo(7);
    assertThat(dto.getBreed()).isEqualTo(2);
  }
}
