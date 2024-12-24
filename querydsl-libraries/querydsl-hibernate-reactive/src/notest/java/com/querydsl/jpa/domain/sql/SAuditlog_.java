package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SAuditlog_ is a Querydsl query type for SAuditlog_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SAuditlog_ extends com.querydsl.sql.RelationalPathBase<SAuditlog_> {

  private static final long serialVersionUID = 472909934;

  public static final SAuditlog_ auditlog_ = new SAuditlog_("auditlog_");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final NumberPath<Long> itemId = createNumber("itemId", Long.class);

  public final com.querydsl.sql.PrimaryKey<SAuditlog_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SItem_> auditlog_ITEMIDFK =
      createForeignKey(itemId, "ID");

  public SAuditlog_(String variable) {
    super(SAuditlog_.class, forVariable(variable), "null", "auditlog_");
    addMetadata();
  }

  public SAuditlog_(String variable, String schema, String table) {
    super(SAuditlog_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SAuditlog_(String variable, String schema) {
    super(SAuditlog_.class, forVariable(variable), schema, "auditlog_");
    addMetadata();
  }

  public SAuditlog_(Path<? extends SAuditlog_> path) {
    super(path.getType(), path.getMetadata(), "null", "auditlog_");
    addMetadata();
  }

  public SAuditlog_(PathMetadata metadata) {
    super(SAuditlog_.class, metadata, "null", "auditlog_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        itemId, ColumnMetadata.named("ITEM_ID").withIndex(2).ofType(Types.BIGINT).withSize(19));
  }
}
