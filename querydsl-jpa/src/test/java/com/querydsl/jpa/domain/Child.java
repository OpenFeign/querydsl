package com.querydsl.jpa.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity(name = "Child2")
public class Child {

  @Id int id;

  @ManyToOne Parent parent;
}
