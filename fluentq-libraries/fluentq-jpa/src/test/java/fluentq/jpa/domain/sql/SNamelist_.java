package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SNamelist_ is a FluentQ query type for SNamelist_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SNamelist_ extends fluentq.sql.RelationalPathBase<SNamelist_> {

  private static final long serialVersionUID = 1524945998;

  public static final SNamelist_ namelist_ = new SNamelist_("namelist_");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final fluentq.sql.PrimaryKey<SNamelist_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SNameListNAMES> _nameListNAMESNameListIDFK =
      createInvForeignKey(id, "NameList_ID");

  public SNamelist_(String variable) {
    super(SNamelist_.class, forVariable(variable), "null", "namelist_");
    addMetadata();
  }

  public SNamelist_(String variable, String schema, String table) {
    super(SNamelist_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SNamelist_(String variable, String schema) {
    super(SNamelist_.class, forVariable(variable), schema, "namelist_");
    addMetadata();
  }

  public SNamelist_(Path<? extends SNamelist_> path) {
    super(path.getType(), path.getMetadata(), "null", "namelist_");
    addMetadata();
  }

  public SNamelist_(PathMetadata metadata) {
    super(SNamelist_.class, metadata, "null", "namelist_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
