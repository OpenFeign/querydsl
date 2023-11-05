package com.querydsl.maven;

import com.querydsl.core.annotations.QueryEntity;
import java.util.Date;
import javax.jdo.annotations.PersistenceCapable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@PersistenceCapable
@javax.persistence.Entity
@QueryEntity
public class Entity {

  String property;

  @Temporal(TemporalType.TIMESTAMP)
  Date annotatedProperty;
}
