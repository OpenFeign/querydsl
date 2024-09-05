package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SPrice_ is a Querydsl query type for SPrice_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SPrice_ extends com.querydsl.sql.RelationalPathBase<SPrice_> {

  private static final long serialVersionUID = 776437438;

  public static final SPrice_ price_ = new SPrice_("price_");

  public final NumberPath<Long> amount = createNumber("amount", Long.class);

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final NumberPath<Long> productId = createNumber("productId", Long.class);

  public final com.querydsl.sql.PrimaryKey<SPrice_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SItem_> price_PRODUCTIDFK =
      createForeignKey(productId, "ID");

  public final com.querydsl.sql.ForeignKey<SCatalog_price_> _catalog_price_pricesIDFK =
      createInvForeignKey(id, "prices_ID");

  public SPrice_(String variable) {
    super(SPrice_.class, forVariable(variable), "null", "price_");
    addMetadata();
  }

  public SPrice_(String variable, String schema, String table) {
    super(SPrice_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SPrice_(String variable, String schema) {
    super(SPrice_.class, forVariable(variable), schema, "price_");
    addMetadata();
  }

  public SPrice_(Path<? extends SPrice_> path) {
    super(path.getType(), path.getMetadata(), "null", "price_");
    addMetadata();
  }

  public SPrice_(PathMetadata metadata) {
    super(SPrice_.class, metadata, "null", "price_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        amount, ColumnMetadata.named("AMOUNT").withIndex(2).ofType(Types.BIGINT).withSize(19));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        productId,
        ColumnMetadata.named("PRODUCT_ID").withIndex(3).ofType(Types.BIGINT).withSize(19));
  }
}
