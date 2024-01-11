package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import javax.annotation.processing.Generated;

/** SLineitems2 is a Querydsl query type for SLineitems2 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SLineitems2 extends com.querydsl.sql.RelationalPathBase<SLineitems2> {

  private static final long serialVersionUID = 513637358;

  public static final SLineitems2 lineitems2 = new SLineitems2("lineitems2");

  public SLineitems2(String variable) {
    super(SLineitems2.class, forVariable(variable), "null", "lineitems2");
    addMetadata();
  }

  public SLineitems2(String variable, String schema, String table) {
    super(SLineitems2.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SLineitems2(String variable, String schema) {
    super(SLineitems2.class, forVariable(variable), schema, "lineitems2");
    addMetadata();
  }

  public SLineitems2(Path<? extends SLineitems2> path) {
    super(path.getType(), path.getMetadata(), "null", "lineitems2");
    addMetadata();
  }

  public SLineitems2(PathMetadata metadata) {
    super(SLineitems2.class, metadata, "null", "lineitems2");
    addMetadata();
  }

  public void addMetadata() {}
}
