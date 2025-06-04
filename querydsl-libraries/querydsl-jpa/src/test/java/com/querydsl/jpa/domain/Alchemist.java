package com.querydsl.jpa.domain;

import jakarta.persistence.*;
import java.util.Set;

@Entity(name = "AlchemistEntity")
@Table(name = "alchemist_effects_test")
public class Alchemist {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String alchemistName;

  @Convert(converter = PotionEffectSetConverter.class)
  @Column(columnDefinition = "TEXT")
  private Set<PotionEffect> knownEffects;

  public Alchemist() {}

  public Alchemist(String alchemistName, Set<PotionEffect> knownEffects) {
    this.alchemistName = alchemistName;
    this.knownEffects = knownEffects;
  }

  public Integer getId() {
    return id;
  }

  public String getAlchemistName() {
    return alchemistName;
  }

  public Set<PotionEffect> getKnownEffects() {
    return knownEffects;
  }
}
