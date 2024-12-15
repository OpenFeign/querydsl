package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SStore_ is a Querydsl query type for SStore_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SStore_ extends com.querydsl.sql.RelationalPathBase<SStore_> {

  private static final long serialVersionUID = 864365094;

  public static final SStore_ store_ = new SStore_("store_");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final NumberPath<Long> locationId = createNumber("locationId", Long.class);

  public final com.querydsl.sql.PrimaryKey<SStore_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SLocation_> store_LOCATIONIDFK =
      createForeignKey(locationId, "ID");

  public final com.querydsl.sql.ForeignKey<SStore_customer_> _store_customer_StoreIDFK =
      createInvForeignKey(id, "Store_ID");

  public SStore_(String variable) {
    super(SStore_.class, forVariable(variable), "null", "store_");
    addMetadata();
  }

  public SStore_(String variable, String schema, String table) {
    super(SStore_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SStore_(String variable, String schema) {
    super(SStore_.class, forVariable(variable), schema, "store_");
    addMetadata();
  }

  public SStore_(Path<? extends SStore_> path) {
    super(path.getType(), path.getMetadata(), "null", "store_");
    addMetadata();
  }

  public SStore_(PathMetadata metadata) {
    super(SStore_.class, metadata, "null", "store_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        locationId,
        ColumnMetadata.named("LOCATION_ID").withIndex(2).ofType(Types.BIGINT).withSize(19));
  }
}
