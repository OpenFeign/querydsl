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

/** SFoo_ is a FluentQ query type for SFoo_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SFoo_ extends fluentq.sql.RelationalPathBase<SFoo_> {

  private static final long serialVersionUID = 1493243105;

  public static final SFoo_ foo_ = new SFoo_("foo_");

  public final StringPath bar = createString("bar");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final DatePath<java.sql.Date> startdate = createDate("startdate", java.sql.Date.class);

  public final fluentq.sql.PrimaryKey<SFoo_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SFooNames> _fooNamesFooIdFK =
      createInvForeignKey(id, "foo_id");

  public SFoo_(String variable) {
    super(SFoo_.class, forVariable(variable), "null", "foo_");
    addMetadata();
  }

  public SFoo_(String variable, String schema, String table) {
    super(SFoo_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SFoo_(String variable, String schema) {
    super(SFoo_.class, forVariable(variable), schema, "foo_");
    addMetadata();
  }

  public SFoo_(Path<? extends SFoo_> path) {
    super(path.getType(), path.getMetadata(), "null", "foo_");
    addMetadata();
  }

  public SFoo_(PathMetadata metadata) {
    super(SFoo_.class, metadata, "null", "foo_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(bar, ColumnMetadata.named("BAR").withIndex(2).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        startdate, ColumnMetadata.named("STARTDATE").withIndex(3).ofType(Types.DATE).withSize(10));
  }
}
