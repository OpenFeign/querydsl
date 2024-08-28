package com.querydsl.jpa.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.Set;

@Entity
public class World {

  @Id Long id;

  @OneToMany Set<Mammal> mammals;
}
