package com.querydsl.jpa.domain5;

import jakarta.persistence.*;

@Embeddable
public class MyEmbeddedAttribute {
  @ManyToOne private MyOtherEntity attributeWithInitProblem;
}
