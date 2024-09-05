package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SNamelist_ is a Querydsl query type for SNamelist_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SNamelist_ extends com.querydsl.sql.RelationalPathBase<SNamelist_> {

  private static final long serialVersionUID = 1524945998;

  public static final SNamelist_ namelist_ = new SNamelist_("namelist_");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final com.querydsl.sql.PrimaryKey<SNamelist_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SNameListNAMES> _nameListNAMESNameListIDFK =
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
