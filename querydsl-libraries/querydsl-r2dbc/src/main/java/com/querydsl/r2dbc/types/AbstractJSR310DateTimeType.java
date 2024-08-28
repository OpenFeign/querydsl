package com.querydsl.r2dbc.types;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

/**
 * Common abstract superclass for Type implementations for Java Time API (JSR310)
 *
 * @param <T>
 */
@IgnoreJRERequirement // conditionally included
public abstract class AbstractJSR310DateTimeType<T extends Temporal>
    extends AbstractType<T, Temporal> {

  protected static final DateTimeFormatter dateFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");
  protected static final DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  protected static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

  public AbstractJSR310DateTimeType(int type) {
    super(type);
  }

  @Override
  public Class<Temporal> getDatabaseClass() {
    return Temporal.class;
  }
}
