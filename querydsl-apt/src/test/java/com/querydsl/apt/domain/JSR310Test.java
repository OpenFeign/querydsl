package com.querydsl.apt.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Date;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.junit.Test;

public class JSR310Test {

  @MappedSuperclass
  @Access(AccessType.FIELD)
  public abstract static class BaseEntity {

    public abstract Long getId();

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    public boolean isNew() {
      return null == getId();
    }

    public Instant getCreatedDate() {
      return null == createdDate ? null : createdDate.toInstant();
    }

    public void setCreatedDate(Instant creationDate) {
      this.createdDate = null == creationDate ? null : Date.from(creationDate);
    }
  }

  @Test
  public void test() {
    assertEquals(Date.class, QJSR310Test_BaseEntity.baseEntity.createdDate.getType());
  }
}
