package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SLineItems is a Querydsl query type for SLineItems */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SLineItems extends com.querydsl.sql.RelationalPathBase<SLineItems> {

  private static final long serialVersionUID = -1537004380;

  public static final SLineItems LineItems = new SLineItems("LineItems");

  public final NumberPath<Integer> _index = createNumber("_index", Integer.class);

  public final NumberPath<Long> lineItemsID = createNumber("lineItemsID", Long.class);

  public final NumberPath<Long> orderID = createNumber("orderID", Long.class);

  public final com.querydsl.sql.PrimaryKey<SLineItems> primary =
      createPrimaryKey(orderID, lineItemsID);

  public final com.querydsl.sql.ForeignKey<SOrder_> lineItemsOrderIDFK =
      createForeignKey(orderID, "ID");

  public final com.querydsl.sql.ForeignKey<SItem_> lineItemsLineItemsIDFK =
      createForeignKey(lineItemsID, "ID");

  public SLineItems(String variable) {
    super(SLineItems.class, forVariable(variable), "null", "LineItems");
    addMetadata();
  }

  public SLineItems(String variable, String schema, String table) {
    super(SLineItems.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SLineItems(String variable, String schema) {
    super(SLineItems.class, forVariable(variable), schema, "LineItems");
    addMetadata();
  }

  public SLineItems(Path<? extends SLineItems> path) {
    super(path.getType(), path.getMetadata(), "null", "LineItems");
    addMetadata();
  }

  public SLineItems(PathMetadata metadata) {
    super(SLineItems.class, metadata, "null", "LineItems");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        _index, ColumnMetadata.named("_index").withIndex(3).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        lineItemsID,
        ColumnMetadata.named("lineItems_ID")
            .withIndex(2)
            .ofType(Types.BIGINT)
            .withSize(19)
            .notNull());
    addMetadata(
        orderID,
        ColumnMetadata.named("Order_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
