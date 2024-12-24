package com.querydsl.jpa;

import com.querydsl.core.Target;

/**
 * @author tiwe
 */
public final class Mode {

  public static final ThreadLocal<String> mode = new ThreadLocal<>();

  public static final ThreadLocal<Target> target = new ThreadLocal<>();

  private Mode() {}
}
