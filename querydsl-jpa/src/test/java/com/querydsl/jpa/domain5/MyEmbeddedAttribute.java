package com.querydsl.jpa.domain5;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;

@Embeddable
public class MyEmbeddedAttribute {
  @ManyToOne private MyOtherEntity attributeWithInitProblem;
}
