package com.querydsl.jpa.domain;

import jakarta.persistence.Entity;
import java.io.Serial;

@Entity
public class Novel extends Book {

  @Serial private static final long serialVersionUID = 4711598115423737544L;
}
