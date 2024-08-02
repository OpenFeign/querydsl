package com.querydsl.apt.domain;

import com.querydsl.core.annotations.PropertyType;
import com.querydsl.core.annotations.QueryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class AbstractProperties3Test {

  @MappedSuperclass
  public static class BaseEntity {}

  @Entity
  public static class Compound extends BaseEntity {

    String name;
  }

  @Entity
  @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
  public abstract static class Containable extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "containable_seq_gen")
    @SequenceGenerator(name = "containable_seq_gen", sequenceName = "seq_containable")
    @Column(name = "id")
    Long id;

    @QueryType(PropertyType.ENTITY)
    public abstract Compound getCompound();
  }

  @MappedSuperclass
  @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
  @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
  public abstract static class CompoundContainer extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "compound_container_seq_gen")
    @SequenceGenerator(
        name = "compound_container_seq_gen",
        sequenceName = "seq_compound_container",
        allocationSize = 1000)
    @Column(name = "compound_container_id")
    Long id;

    @JoinColumn(name = "containable_id")
    @OneToOne(fetch = FetchType.EAGER)
    Containable containable;
  }

  @Test
  public void test() {
    QAbstractProperties3Test_CompoundContainer.compoundContainer.containable.compound.name
        .isNotNull();
  }
}
