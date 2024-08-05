package com.querydsl.apt.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import org.junit.Test;

public class Properties2Test {

  public abstract static class BaseX<T extends Serializable> implements Serializable {

    public abstract T getId();
  }

  @SuppressWarnings("serial")
  @Entity
  @Table(name = "X")
  public static class ConcreteX extends BaseX<String> {

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @Override
    public String getId() {
      return name;
    }
  }

  @Test(expected = NoSuchFieldException.class)
  public void test() throws NoSuchFieldException {
    QProperties2Test_ConcreteX.class.getDeclaredField("id");
  }
}
