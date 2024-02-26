package com.querydsl.jpa.domain5;

import com.querydsl.core.annotations.QueryInit;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class MyEntity extends MyMappedSuperclass {

  @Id private int id;

  @Embedded
  @QueryInit("*")
  private MyEmbeddedAttribute embeddedAttribute;
}
