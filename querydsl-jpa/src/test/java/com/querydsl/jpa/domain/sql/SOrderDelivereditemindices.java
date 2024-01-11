package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import javax.annotation.processing.Generated;

/** SOrderDelivereditemindices is a Querydsl query type for SOrderDelivereditemindices */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SOrderDelivereditemindices
    extends com.querydsl.sql.RelationalPathBase<SOrderDelivereditemindices> {

  private static final long serialVersionUID = -1737470186;

  public static final SOrderDelivereditemindices orderDelivereditemindices =
      new SOrderDelivereditemindices("order_delivereditemindices");

  public SOrderDelivereditemindices(String variable) {
    super(
        SOrderDelivereditemindices.class,
        forVariable(variable),
        "null",
        "order_delivereditemindices");
    addMetadata();
  }

  public SOrderDelivereditemindices(String variable, String schema, String table) {
    super(SOrderDelivereditemindices.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SOrderDelivereditemindices(String variable, String schema) {
    super(
        SOrderDelivereditemindices.class,
        forVariable(variable),
        schema,
        "order_delivereditemindices");
    addMetadata();
  }

  public SOrderDelivereditemindices(Path<? extends SOrderDelivereditemindices> path) {
    super(path.getType(), path.getMetadata(), "null", "order_delivereditemindices");
    addMetadata();
  }

  public SOrderDelivereditemindices(PathMetadata metadata) {
    super(SOrderDelivereditemindices.class, metadata, "null", "order_delivereditemindices");
    addMetadata();
  }

  public void addMetadata() {}
}
