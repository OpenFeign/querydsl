package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.TimePath;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
