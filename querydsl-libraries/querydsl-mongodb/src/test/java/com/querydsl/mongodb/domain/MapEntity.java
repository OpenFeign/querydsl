package com.querydsl.mongodb.domain;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import java.util.HashMap;
import java.util.Map;

@Entity
public class MapEntity extends AbstractEntity {

  @Embedded private Map<String, String> properties = new HashMap<>();

  public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }
}
