package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SCatalog_ is a Querydsl query type for SCatalog_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SCatalog_ extends com.querydsl.sql.RelationalPathBase<SCatalog_> {

  private static final long serialVersionUID = 55977966;

  public static final SCatalog_ catalog_ = new SCatalog_("catalog_");

  public final DatePath<java.sql.Date> effectivedate =
      createDate("effectivedate", java.sql.Date.class);

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final com.querydsl.sql.PrimaryKey<SCatalog_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SCatalog_price_> _catalog_price_CatalogIDFK =
      createInvForeignKey(id, "Catalog_ID");

  public SCatalog_(String variable) {
    super(SCatalog_.class, forVariable(variable), "null", "catalog_");
    addMetadata();
  }

  public SCatalog_(String variable, String schema, String table) {
    super(SCatalog_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCatalog_(String variable, String schema) {
    super(SCatalog_.class, forVariable(variable), schema, "catalog_");
    addMetadata();
  }

  public SCatalog_(Path<? extends SCatalog_> path) {
    super(path.getType(), path.getMetadata(), "null", "catalog_");
    addMetadata();
  }

  public SCatalog_(PathMetadata metadata) {
    super(SCatalog_.class, metadata, "null", "catalog_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        effectivedate,
        ColumnMetadata.named("EFFECTIVEDATE").withIndex(2).ofType(Types.DATE).withSize(10));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
  }
}
