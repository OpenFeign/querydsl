package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import javax.annotation.processing.Generated;

/** SCalendarHolidays is a Querydsl query type for SCalendarHolidays */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SCalendarHolidays extends com.querydsl.sql.RelationalPathBase<SCalendarHolidays> {

  private static final long serialVersionUID = 324116737;

  public static final SCalendarHolidays calendarHolidays =
      new SCalendarHolidays("calendar_holidays");

  public SCalendarHolidays(String variable) {
    super(SCalendarHolidays.class, forVariable(variable), "null", "calendar_holidays");
    addMetadata();
  }

  public SCalendarHolidays(String variable, String schema, String table) {
    super(SCalendarHolidays.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCalendarHolidays(String variable, String schema) {
    super(SCalendarHolidays.class, forVariable(variable), schema, "calendar_holidays");
    addMetadata();
  }

  public SCalendarHolidays(Path<? extends SCalendarHolidays> path) {
    super(path.getType(), path.getMetadata(), "null", "calendar_holidays");
    addMetadata();
  }

  public SCalendarHolidays(PathMetadata metadata) {
    super(SCalendarHolidays.class, metadata, "null", "calendar_holidays");
    addMetadata();
  }

  public void addMetadata() {}
}
