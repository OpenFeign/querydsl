package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SLineItems2 is a Querydsl query type for SLineItems2 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SLineItems2 extends com.querydsl.sql.RelationalPathBase<SLineItems2> {

  private static final long serialVersionUID = -402495474;

  public static final SLineItems2 LineItems2 = new SLineItems2("LineItems2");

  public final NumberPath<Long> lineItemsMapID = createNumber("lineItemsMapID", Long.class);

  public final NumberPath<Long> orderID = createNumber("orderID", Long.class);

  public final com.querydsl.sql.PrimaryKey<SLineItems2> primary =
      createPrimaryKey(orderID, lineItemsMapID);

  public final com.querydsl.sql.ForeignKey<SOrder_> lineItems2OrderIDFK =
      createForeignKey(orderID, "ID");

  public final com.querydsl.sql.ForeignKey<SItem_> lineItems2LineItemsMapIDFK =
      createForeignKey(lineItemsMapID, "ID");

  public SLineItems2(String variable) {
    super(SLineItems2.class, forVariable(variable), "null", "LineItems2");
    addMetadata();
  }

  public SLineItems2(String variable, String schema, String table) {
    super(SLineItems2.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SLineItems2(String variable, String schema) {
    super(SLineItems2.class, forVariable(variable), schema, "LineItems2");
    addMetadata();
  }

  public SLineItems2(Path<? extends SLineItems2> path) {
    super(path.getType(), path.getMetadata(), "null", "LineItems2");
    addMetadata();
  }

  public SLineItems2(PathMetadata metadata) {
    super(SLineItems2.class, metadata, "null", "LineItems2");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        lineItemsMapID,
        ColumnMetadata.named("lineItemsMap_ID")
            .withIndex(2)
            .ofType(Types.BIGINT)
            .withSize(19)
            .notNull());
    addMetadata(
        orderID,
        ColumnMetadata.named("Order_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
