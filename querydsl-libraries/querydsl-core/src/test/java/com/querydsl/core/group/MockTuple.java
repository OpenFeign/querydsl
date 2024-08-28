package com.querydsl.core.group;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import java.util.Arrays;

public class MockTuple implements Tuple {

  private final Object[] a;

  public MockTuple(Object[] a) {
    this.a = a;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(int index, Class<T> type) {
    return (T) a[index];
  }

  @Override
  public <T> T get(Expression<T> expr) {
    return null;
  }

  @Override
  public int size() {
    return a.length;
  }

  @Override
  public Object[] toArray() {
    return a;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj instanceof Tuple tuple) {
      return Arrays.equals(a, tuple.toArray());
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(a);
  }

  @Override
  public String toString() {
    return Arrays.toString(a);
  }
}
