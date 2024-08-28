package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SItem_statuschange_ is a Querydsl query type for SItem_statuschange_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SItem_statuschange_ extends com.querydsl.sql.RelationalPathBase<SItem_statuschange_> {

  private static final long serialVersionUID = 1154189529;

  public static final SItem_statuschange_ item_statuschange_ =
      new SItem_statuschange_("item__statuschange_");

  public final NumberPath<Long> paymentID = createNumber("paymentID", Long.class);

  public final NumberPath<Long> statusChangesID = createNumber("statusChangesID", Long.class);

  public final com.querydsl.sql.PrimaryKey<SItem_statuschange_> primary =
      createPrimaryKey(paymentID, statusChangesID);

  public final com.querydsl.sql.ForeignKey<SItem_> item_statuschange_PaymentIDFK =
      createForeignKey(paymentID, "ID");

  public final com.querydsl.sql.ForeignKey<SStatuschange_> item_statuschange_statusChangesIDFK =
      createForeignKey(statusChangesID, "ID");

  public SItem_statuschange_(String variable) {
    super(SItem_statuschange_.class, forVariable(variable), "null", "item__statuschange_");
    addMetadata();
  }

  public SItem_statuschange_(String variable, String schema, String table) {
    super(SItem_statuschange_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SItem_statuschange_(String variable, String schema) {
    super(SItem_statuschange_.class, forVariable(variable), schema, "item__statuschange_");
    addMetadata();
  }

  public SItem_statuschange_(Path<? extends SItem_statuschange_> path) {
    super(path.getType(), path.getMetadata(), "null", "item__statuschange_");
    addMetadata();
  }

  public SItem_statuschange_(PathMetadata metadata) {
    super(SItem_statuschange_.class, metadata, "null", "item__statuschange_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        paymentID,
        ColumnMetadata.named("Payment_ID")
            .withIndex(1)
            .ofType(Types.BIGINT)
            .withSize(19)
            .notNull());
    addMetadata(
        statusChangesID,
        ColumnMetadata.named("statusChanges_ID")
            .withIndex(2)
            .ofType(Types.BIGINT)
            .withSize(19)
            .notNull());
  }
}
