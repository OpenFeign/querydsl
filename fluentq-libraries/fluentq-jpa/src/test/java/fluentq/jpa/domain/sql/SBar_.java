package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.DatePath;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SBar_ is a FluentQ query type for SBar_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SBar_ extends fluentq.sql.RelationalPathBase<SBar_> {

  private static final long serialVersionUID = 1493110580;

  public static final SBar_ bar_ = new SBar_("bar_");

  public final DatePath<java.sql.Date> date = createDate("date", java.sql.Date.class);

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final fluentq.sql.PrimaryKey<SBar_> primary = createPrimaryKey(id);

  public SBar_(String variable) {
    super(SBar_.class, forVariable(variable), "null", "bar_");
    addMetadata();
  }

  public SBar_(String variable, String schema, String table) {
    super(SBar_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SBar_(String variable, String schema) {
    super(SBar_.class, forVariable(variable), schema, "bar_");
    addMetadata();
  }

  public SBar_(Path<? extends SBar_> path) {
    super(path.getType(), path.getMetadata(), "null", "bar_");
    addMetadata();
  }

  public SBar_(PathMetadata metadata) {
    super(SBar_.class, metadata, "null", "bar_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(date, ColumnMetadata.named("DATE").withIndex(2).ofType(Types.DATE).withSize(10));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
  }
}
