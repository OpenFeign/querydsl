package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.TimePath;
import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import org.junit.Test;

public class TemporalTest {

  @Entity
  public static class MyEntity {

    @Temporal(value = TemporalType.DATE)
    private Date date;

    @Temporal(value = TemporalType.TIME)
    private Date time;
  }

  @Test
  public void test() {
    assertThat(QTemporalTest_MyEntity.myEntity.date.getClass()).isEqualTo(DatePath.class);
    assertThat(QTemporalTest_MyEntity.myEntity.time.getClass()).isEqualTo(TimePath.class);
  }
}
