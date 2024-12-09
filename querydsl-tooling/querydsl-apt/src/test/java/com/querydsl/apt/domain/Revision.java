package com.querydsl.apt.domain;

import jakarta.persistence.Entity;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionMapping;

@Entity
@RevisionEntity
public class Revision extends RevisionMapping {

  private static final long serialVersionUID = 4587663183059799464L;
}
