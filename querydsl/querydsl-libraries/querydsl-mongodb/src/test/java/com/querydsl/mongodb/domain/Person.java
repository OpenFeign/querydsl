package com.querydsl.mongodb.domain;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;

@Entity
class Person {
  @Id public ObjectId id;

  public String name;

  // manual reference to an address
  public ObjectId addressId;
}
