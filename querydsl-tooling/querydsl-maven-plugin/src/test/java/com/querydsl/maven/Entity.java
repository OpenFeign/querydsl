package com.querydsl.maven;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
@jakarta.persistence.Entity
@QueryEntity
public class Entity {

  String property;

  @Temporal(TemporalType.TIMESTAMP)
  Date annotatedProperty;
}
