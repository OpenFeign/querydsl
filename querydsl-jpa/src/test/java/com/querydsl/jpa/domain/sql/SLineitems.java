package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import javax.annotation.processing.Generated;

/** SLineitems is a Querydsl query type for SLineitems */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SLineitems extends com.querydsl.sql.RelationalPathBase<SLineitems> {

  private static final long serialVersionUID = -1507451708;

  public static final SLineitems lineitems = new SLineitems("lineitems");

  public SLineitems(String variable) {
    super(SLineitems.class, forVariable(variable), "null", "lineitems");
    addMetadata();
  }

  public SLineitems(String variable, String schema, String table) {
    super(SLineitems.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SLineitems(String variable, String schema) {
    super(SLineitems.class, forVariable(variable), schema, "lineitems");
    addMetadata();
  }

  public SLineitems(Path<? extends SLineitems> path) {
    super(path.getType(), path.getMetadata(), "null", "lineitems");
    addMetadata();
  }

  public SLineitems(PathMetadata metadata) {
    super(SLineitems.class, metadata, "null", "lineitems");
    addMetadata();
  }

  public void addMetadata() {}
}
