package com.querydsl.jpa.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import java.util.Collection;

@Entity
public class Human extends Mammal {

  @ElementCollection Collection<Integer> hairs;
}
