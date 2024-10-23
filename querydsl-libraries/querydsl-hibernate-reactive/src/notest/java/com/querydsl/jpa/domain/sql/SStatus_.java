package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SStatus_ is a Querydsl query type for SStatus_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SStatus_ extends com.querydsl.sql.RelationalPathBase<SStatus_> {

  private static final long serialVersionUID = 1012660517;

  public static final SStatus_ status_ = new SStatus_("status_");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final StringPath name = createString("name");

  public final com.querydsl.sql.PrimaryKey<SStatus_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SItem_> _item_CURRENTSTATUSIDFK =
      createInvForeignKey(id, "CURRENTSTATUS_ID");

  public final com.querydsl.sql.ForeignKey<SItem_> _item_STATUSIDFK =
      createInvForeignKey(id, "STATUS_ID");

  public SStatus_(String variable) {
    super(SStatus_.class, forVariable(variable), "null", "status_");
    addMetadata();
  }

  public SStatus_(String variable, String schema, String table) {
    super(SStatus_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SStatus_(String variable, String schema) {
    super(SStatus_.class, forVariable(variable), schema, "status_");
    addMetadata();
  }

  public SStatus_(Path<? extends SStatus_> path) {
    super(path.getType(), path.getMetadata(), "null", "status_");
    addMetadata();
  }

  public SStatus_(PathMetadata metadata) {
    super(SStatus_.class, metadata, "null", "status_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
  }
}
