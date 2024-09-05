package com.querydsl.jpa.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.Set;

@Entity(name = "Parent2")
public class Parent {

  @Id int id;

  @OneToMany(mappedBy = "parent")
  Set<Child> children;
}
