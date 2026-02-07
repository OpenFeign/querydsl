package com.querydsl.jpa.hibernate;

import com.querydsl.core.types.Operator;

public enum HQLOps implements Operator {
  WITH(Object.class);

  private final Class<?> type;

  HQLOps(Class<?> type) {
    this.type = type;
  }

  @Override
  public Class<?> getType() {
    return type;
  }
}
