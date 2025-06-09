package com.querydsl.core;

public record Pair<F, S>(F first, S second) {

  @Deprecated
  public F getFirst() {
    return first();
  }

  @Deprecated
  public S getSecond() {
    return second();
  }

  public static <T, U> Pair<T, U> of(T key, U value) {
    return new Pair<>(key, value);
  }
}
