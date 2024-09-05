package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SOrder_item_ is a Querydsl query type for SOrder_item_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SOrder_item_ extends com.querydsl.sql.RelationalPathBase<SOrder_item_> {

  private static final long serialVersionUID = -131699213;

  public static final SOrder_item_ order_item_ = new SOrder_item_("order__item_");

  public final NumberPath<Integer> _index = createNumber("_index", Integer.class);

  public final NumberPath<Long> itemsID = createNumber("itemsID", Long.class);

  public final NumberPath<Long> orderID = createNumber("orderID", Long.class);

  public final com.querydsl.sql.PrimaryKey<SOrder_item_> primary =
      createPrimaryKey(orderID, itemsID);

  public final com.querydsl.sql.ForeignKey<SOrder_> order_item_OrderIDFK =
      createForeignKey(orderID, "ID");

  public final com.querydsl.sql.ForeignKey<SItem_> order_item_itemsIDFK =
      createForeignKey(itemsID, "ID");

  public SOrder_item_(String variable) {
    super(SOrder_item_.class, forVariable(variable), "null", "order__item_");
    addMetadata();
  }

  public SOrder_item_(String variable, String schema, String table) {
    super(SOrder_item_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SOrder_item_(String variable, String schema) {
    super(SOrder_item_.class, forVariable(variable), schema, "order__item_");
    addMetadata();
  }

  public SOrder_item_(Path<? extends SOrder_item_> path) {
    super(path.getType(), path.getMetadata(), "null", "order__item_");
    addMetadata();
  }

  public SOrder_item_(PathMetadata metadata) {
    super(SOrder_item_.class, metadata, "null", "order__item_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        _index, ColumnMetadata.named("_index").withIndex(3).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        itemsID,
        ColumnMetadata.named("items_ID").withIndex(2).ofType(Types.BIGINT).withSize(19).notNull());
    addMetadata(
        orderID,
        ColumnMetadata.named("Order_ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
  }
}
