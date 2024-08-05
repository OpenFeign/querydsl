package com.querydsl.apt.domain;

import jakarta.persistence.Entity;
import java.util.AbstractSet;
import java.util.Iterator;

public class CustomCollection {

  @Entity
  public static class MyCustomCollection<T> extends AbstractSet<T> {

    @Override
    public Iterator<T> iterator() {
      return null;
    }

    @Override
    public int size() {
      return 0;
    }
  }

  @Entity
  public static class MyCustomCollection2<T> extends AbstractSet<T> {

    @Override
    public Iterator<T> iterator() {
      return null;
    }

    @Override
    public int size() {
      return 0;
    }
  }

  @Entity
  public static class MyEntity {

    MyCustomCollection<String> strings;

    MyCustomCollection2 strings2;
  }
}
