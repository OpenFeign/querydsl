package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.Instant;
import java.util.Date;
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
    assertThat(QJSR310Test_BaseEntity.baseEntity.createdDate.getType()).isEqualTo(Date.class);
  }
}
