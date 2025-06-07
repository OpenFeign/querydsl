package com.querydsl.jpa.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
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

  @ElementCollection
  @CollectionTable(name = "alchemist_spells", joinColumns = @JoinColumn(name = "alchemist_id"))
  @Column(name = "spell_name")
  private List<String> learnedSpells = new ArrayList<>();

  public Alchemist() {}

  public Alchemist(
      String alchemistName, Set<PotionEffect> knownEffects, List<String> learnedSpells) {
    this.alchemistName = alchemistName;
    this.knownEffects = knownEffects;
    this.learnedSpells = learnedSpells;
  }

  public Integer getId() {
    return id;
  }

  public String getAlchemistName() {
    return alchemistName;
  }

  public List<String> getLearnedSpells() {
    return learnedSpells;
  }

  public Set<PotionEffect> getKnownEffects() {
    return knownEffects;
  }
}
