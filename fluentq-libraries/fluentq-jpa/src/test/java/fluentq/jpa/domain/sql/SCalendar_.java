package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SCalendar_ is a FluentQ query type for SCalendar_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SCalendar_ extends fluentq.sql.RelationalPathBase<SCalendar_> {

  private static final long serialVersionUID = -953714343;

  public static final SCalendar_ calendar_ = new SCalendar_("calendar_");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final fluentq.sql.PrimaryKey<SCalendar_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SCalendarHOLIDAYS> _calendarHOLIDAYSCalendarIDFK =
      createInvForeignKey(id, "Calendar_ID");

  public final fluentq.sql.ForeignKey<SNationality_> _nationality_CALENDARIDFK =
      createInvForeignKey(id, "CALENDAR_ID");

  public SCalendar_(String variable) {
    super(SCalendar_.class, forVariable(variable), "null", "calendar_");
    addMetadata();
  }

  public SCalendar_(String variable, String schema, String table) {
    super(SCalendar_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCalendar_(String variable, String schema) {
    super(SCalendar_.class, forVariable(variable), schema, "calendar_");
    addMetadata();
  }

  public SCalendar_(Path<? extends SCalendar_> path) {
    super(path.getType(), path.getMetadata(), "null", "calendar_");
    addMetadata();
  }

  public SCalendar_(PathMetadata metadata) {
    super(SCalendar_.class, metadata, "null", "calendar_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
  }
}
