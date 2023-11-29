package com.querydsl.apt.domain;

import jakarta.persistence.Entity;
import java.util.Set;

@Entity
public class Location {

  public Set<Path> paths;
}
