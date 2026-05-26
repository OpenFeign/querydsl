package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.MappedSuperclass;
import org.junit.Test;

public class Properties4Test extends AbstractTest {

  @MappedSuperclass
  public abstract static class Naming {

    public abstract boolean is8FRecord();
  }

  @Test
  public void test() {
    assertThat(QProperties4Test_Naming.naming._8FRecord.getMetadata().getName())
        .isEqualTo("8FRecord");
  }
}
