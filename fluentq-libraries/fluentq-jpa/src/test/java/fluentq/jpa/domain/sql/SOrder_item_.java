package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SOrder_item_ is a FluentQ query type for SOrder_item_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SOrder_item_ extends fluentq.sql.RelationalPathBase<SOrder_item_> {

  private static final long serialVersionUID = -131699213;

  public static final SOrder_item_ order_item_ = new SOrder_item_("order__item_");

  public final NumberPath<Integer> _index = createNumber("_index", Integer.class);

  public final NumberPath<Long> itemsID = createNumber("itemsID", Long.class);

  public final NumberPath<Long> orderID = createNumber("orderID", Long.class);

  public final fluentq.sql.PrimaryKey<SOrder_item_> primary = createPrimaryKey(orderID, itemsID);

  public final fluentq.sql.ForeignKey<SOrder_> order_item_OrderIDFK =
      createForeignKey(orderID, "ID");

  public final fluentq.sql.ForeignKey<SItem_> order_item_itemsIDFK =
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
