package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SPersonid_ is a FluentQ query type for SPersonid_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SPersonid_ extends fluentq.sql.RelationalPathBase<SPersonid_> {

  private static final long serialVersionUID = 1132579751;

  public static final SPersonid_ personid_ = new SPersonid_("personid_");

  public final StringPath country = createString("country");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final NumberPath<Integer> medicarenumber = createNumber("medicarenumber", Integer.class);

  public final fluentq.sql.PrimaryKey<SPersonid_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SPerson_> _person_PIDIDFK = createInvForeignKey(id, "PID_ID");

  public SPersonid_(String variable) {
    super(SPersonid_.class, forVariable(variable), "null", "personid_");
    addMetadata();
  }

  public SPersonid_(String variable, String schema, String table) {
    super(SPersonid_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SPersonid_(String variable, String schema) {
    super(SPersonid_.class, forVariable(variable), schema, "personid_");
    addMetadata();
  }

  public SPersonid_(Path<? extends SPersonid_> path) {
    super(path.getType(), path.getMetadata(), "null", "personid_");
    addMetadata();
  }

  public SPersonid_(PathMetadata metadata) {
    super(SPersonid_.class, metadata, "null", "personid_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        country, ColumnMetadata.named("COUNTRY").withIndex(2).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        medicarenumber,
        ColumnMetadata.named("MEDICARENUMBER").withIndex(3).ofType(Types.INTEGER).withSize(10));
  }
}
