package com.querydsl.apt.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

@MappedSuperclass
public abstract class AbstractSecurable<U, PK extends Serializable> {

  private static final long serialVersionUID = -6151927948714098845L;

  // ~ Meta-data
  @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  @JoinColumn(name = "SOURCE")
  private List<SecurableEntity> securityEntries;
}
