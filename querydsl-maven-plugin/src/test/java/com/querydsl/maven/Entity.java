package com.querydsl.maven;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import com.querydsl.core.annotations.QueryEntity;

@PersistenceCapable
@jakarta.persistence.Entity
@QueryEntity
public class Entity {

    String property;

    @Temporal(TemporalType.TIMESTAMP)
    Date annotatedProperty;
}
