package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SItem_ is a Querydsl query type for SItem_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SItem_ extends com.querydsl.sql.RelationalPathBase<SItem_> {

  private static final long serialVersionUID = -951193564;

  public static final SItem_ item_ = new SItem_("item_");

  public final NumberPath<Long> currentstatusId = createNumber("currentstatusId", Long.class);

  public final StringPath dtype = createString("dtype");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final StringPath name = createString("name");

  public final NumberPath<Integer> paymentstatus = createNumber("paymentstatus", Integer.class);

  public final NumberPath<Long> productId = createNumber("productId", Long.class);

  public final NumberPath<Long> statusId = createNumber("statusId", Long.class);

  public final com.querydsl.sql.PrimaryKey<SItem_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SStatus_> item_CURRENTSTATUSIDFK =
      createForeignKey(currentstatusId, "ID");

  public final com.querydsl.sql.ForeignKey<SItem_> item_PRODUCTIDFK =
      createForeignKey(productId, "ID");

  public final com.querydsl.sql.ForeignKey<SStatus_> item_STATUSIDFK =
      createForeignKey(statusId, "ID");

  public final com.querydsl.sql.ForeignKey<SLineItems2> _lineItems2LineItemsMapIDFK =
      createInvForeignKey(id, "lineItemsMap_ID");

  public final com.querydsl.sql.ForeignKey<SLineItems> _lineItemsLineItemsIDFK =
      createInvForeignKey(id, "lineItems_ID");

  public final com.querydsl.sql.ForeignKey<SAuditlog_> _auditlog_ITEMIDFK =
      createInvForeignKey(id, "ITEM_ID");

  public final com.querydsl.sql.ForeignKey<SItem_> _item_PRODUCTIDFK =
      createInvForeignKey(id, "PRODUCT_ID");

  public final com.querydsl.sql.ForeignKey<SItem_statuschange_> _item_statuschange_PaymentIDFK =
      createInvForeignKey(id, "Payment_ID");

  public final com.querydsl.sql.ForeignKey<SOrder_item_> _order_item_itemsIDFK =
      createInvForeignKey(id, "items_ID");

  public final com.querydsl.sql.ForeignKey<SPrice_> _price_PRODUCTIDFK =
      createInvForeignKey(id, "PRODUCT_ID");

  public SItem_(String variable) {
    super(SItem_.class, forVariable(variable), "null", "item_");
    addMetadata();
  }

  public SItem_(String variable, String schema, String table) {
    super(SItem_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SItem_(String variable, String schema) {
    super(SItem_.class, forVariable(variable), schema, "item_");
    addMetadata();
  }

  public SItem_(Path<? extends SItem_> path) {
    super(path.getType(), path.getMetadata(), "null", "item_");
    addMetadata();
  }

  public SItem_(PathMetadata metadata) {
    super(SItem_.class, metadata, "null", "item_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        currentstatusId,
        ColumnMetadata.named("CURRENTSTATUS_ID").withIndex(6).ofType(Types.BIGINT).withSize(19));
    addMetadata(
        dtype, ColumnMetadata.named("DTYPE").withIndex(2).ofType(Types.VARCHAR).withSize(31));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(4).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        paymentstatus,
        ColumnMetadata.named("PAYMENTSTATUS").withIndex(5).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        productId,
        ColumnMetadata.named("PRODUCT_ID").withIndex(3).ofType(Types.BIGINT).withSize(19));
    addMetadata(
        statusId, ColumnMetadata.named("STATUS_ID").withIndex(7).ofType(Types.BIGINT).withSize(19));
  }
}
