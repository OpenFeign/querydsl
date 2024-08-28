package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SCatalog_price_ is a Querydsl query type for SCatalog_price_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SCatalog_price_ extends com.querydsl.sql.RelationalPathBase<SCatalog_price_> {

  private static final long serialVersionUID = -1317112412;

  public static final SCatalog_price_ catalog_price_ = new SCatalog_price_("catalog__price_");

  public final NumberPath<Integer> catalogID = createNumber("catalogID", Integer.class);

  public final NumberPath<Long> pricesID = createNumber("pricesID", Long.class);

  public final com.querydsl.sql.PrimaryKey<SCatalog_price_> primary =
      createPrimaryKey(catalogID, pricesID);

  public final com.querydsl.sql.ForeignKey<SCatalog_> catalog_price_CatalogIDFK =
      createForeignKey(catalogID, "ID");

  public final com.querydsl.sql.ForeignKey<SPrice_> catalog_price_pricesIDFK =
      createForeignKey(pricesID, "ID");

  public SCatalog_price_(String variable) {
    super(SCatalog_price_.class, forVariable(variable), "null", "catalog__price_");
    addMetadata();
  }

  public SCatalog_price_(String variable, String schema, String table) {
    super(SCatalog_price_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCatalog_price_(String variable, String schema) {
    super(SCatalog_price_.class, forVariable(variable), schema, "catalog__price_");
    addMetadata();
  }

  public SCatalog_price_(Path<? extends SCatalog_price_> path) {
    super(path.getType(), path.getMetadata(), "null", "catalog__price_");
    addMetadata();
  }

  public SCatalog_price_(PathMetadata metadata) {
    super(SCatalog_price_.class, metadata, "null", "catalog__price_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        catalogID,
        ColumnMetadata.named("Catalog_ID")
            .withIndex(1)
            .ofType(Types.INTEGER)
            .withSize(10)
            .notNull());
    addMetadata(
        pricesID,
        ColumnMetadata.named("prices_ID").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
