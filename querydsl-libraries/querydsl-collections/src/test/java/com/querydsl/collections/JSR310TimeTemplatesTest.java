package com.querydsl.collections;

import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.TimePath;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import org.junit.Test;

public class JSR310TimeTemplatesTest {

  private CollQuery<?> query = new CollQuery<Void>(JSR310TimeTemplates.DEFAULT);

  @Test
  public void dateTime() {
    DateTimePath<ZonedDateTime> entity = Expressions.dateTimePath(ZonedDateTime.class, "entity");
    query
        .from(
            entity,
            Arrays.asList(
                ZonedDateTime.now(), ZonedDateTime.of(0, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault())))
        .select(
            entity.year(),
            entity.yearMonth(),
            entity.month(),
            entity.dayOfMonth(),
            entity.dayOfWeek(),
            entity.dayOfYear(),
            entity.hour(),
            entity.minute(),
            entity.second(),
            entity.milliSecond())
        .fetch();
  }

  @Test
  public void localDate() {
    DatePath<LocalDate> entity = Expressions.datePath(LocalDate.class, "entity");
    query
        .from(entity, Arrays.asList(LocalDate.now(), LocalDate.of(0, 1, 1)))
        .select(
            entity.year(),
            entity.yearMonth(),
            entity.month(),
            entity.dayOfMonth(),
            entity.dayOfWeek(),
            entity.dayOfYear())
        .fetch();
  }

  @Test
  public void localTime() {
    TimePath<LocalTime> entity = Expressions.timePath(LocalTime.class, "entity");
    query
        .from(entity, Arrays.asList(LocalTime.now(), LocalTime.of(0, 0)))
        .select(entity.hour(), entity.minute(), entity.second(), entity.milliSecond())
        .fetch();
  }
}
