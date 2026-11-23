package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.DatePath;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SCalendarHOLIDAYS is a FluentQ query type for SCalendarHOLIDAYS */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SCalendarHOLIDAYS extends fluentq.sql.RelationalPathBase<SCalendarHOLIDAYS> {

  private static final long serialVersionUID = 1042099425;

  public static final SCalendarHOLIDAYS CalendarHOLIDAYS =
      new SCalendarHOLIDAYS("Calendar_HOLIDAYS");

  public final NumberPath<Integer> calendarID = createNumber("calendarID", Integer.class);

  public final DatePath<java.sql.Date> holidays = createDate("holidays", java.sql.Date.class);

  public final StringPath holidaysKey = createString("holidaysKey");

  public final fluentq.sql.ForeignKey<SCalendar_> calendarHOLIDAYSCalendarIDFK =
      createForeignKey(calendarID, "ID");

  public SCalendarHOLIDAYS(String variable) {
    super(SCalendarHOLIDAYS.class, forVariable(variable), "null", "Calendar_HOLIDAYS");
    addMetadata();
  }

  public SCalendarHOLIDAYS(String variable, String schema, String table) {
    super(SCalendarHOLIDAYS.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCalendarHOLIDAYS(String variable, String schema) {
    super(SCalendarHOLIDAYS.class, forVariable(variable), schema, "Calendar_HOLIDAYS");
    addMetadata();
  }

  public SCalendarHOLIDAYS(Path<? extends SCalendarHOLIDAYS> path) {
    super(path.getType(), path.getMetadata(), "null", "Calendar_HOLIDAYS");
    addMetadata();
  }

  public SCalendarHOLIDAYS(PathMetadata metadata) {
    super(SCalendarHOLIDAYS.class, metadata, "null", "Calendar_HOLIDAYS");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        calendarID,
        ColumnMetadata.named("Calendar_ID").withIndex(1).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        holidays, ColumnMetadata.named("HOLIDAYS").withIndex(2).ofType(Types.DATE).withSize(10));
    addMetadata(
        holidaysKey,
        ColumnMetadata.named("holidays_key").withIndex(3).ofType(Types.VARCHAR).withSize(255));
  }
}
