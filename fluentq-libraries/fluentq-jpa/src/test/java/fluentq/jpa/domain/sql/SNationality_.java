package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SNationality_ is a FluentQ query type for SNationality_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SNationality_ extends fluentq.sql.RelationalPathBase<SNationality_> {

  private static final long serialVersionUID = -1959593301;

  public static final SNationality_ nationality_ = new SNationality_("nationality_");

  public final NumberPath<Integer> calendarId = createNumber("calendarId", Integer.class);

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final fluentq.sql.PrimaryKey<SNationality_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SCalendar_> nationality_CALENDARIDFK =
      createForeignKey(calendarId, "ID");

  public final fluentq.sql.ForeignKey<SPerson_> _person_NATIONALITYIDFK =
      createInvForeignKey(id, "NATIONALITY_ID");

  public SNationality_(String variable) {
    super(SNationality_.class, forVariable(variable), "null", "nationality_");
    addMetadata();
  }

  public SNationality_(String variable, String schema, String table) {
    super(SNationality_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SNationality_(String variable, String schema) {
    super(SNationality_.class, forVariable(variable), schema, "nationality_");
    addMetadata();
  }

  public SNationality_(Path<? extends SNationality_> path) {
    super(path.getType(), path.getMetadata(), "null", "nationality_");
    addMetadata();
  }

  public SNationality_(PathMetadata metadata) {
    super(SNationality_.class, metadata, "null", "nationality_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        calendarId,
        ColumnMetadata.named("CALENDAR_ID").withIndex(2).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
