package com.querydsl.jpa.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Entity1 {

  public Entity1() {}

  public Entity1(int id) {
    this.id = id;
  }

  @Id public int id;

  public String property;
}
