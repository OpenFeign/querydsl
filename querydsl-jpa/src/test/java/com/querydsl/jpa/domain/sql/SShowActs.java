package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import javax.annotation.processing.Generated;

/** SShowActs is a Querydsl query type for SShowActs */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SShowActs extends com.querydsl.sql.RelationalPathBase<SShowActs> {

  private static final long serialVersionUID = -330389690;

  public static final SShowActs showActs = new SShowActs("show_acts");

  public SShowActs(String variable) {
    super(SShowActs.class, forVariable(variable), "null", "show_acts");
    addMetadata();
  }

  public SShowActs(String variable, String schema, String table) {
    super(SShowActs.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SShowActs(String variable, String schema) {
    super(SShowActs.class, forVariable(variable), schema, "show_acts");
    addMetadata();
  }

  public SShowActs(Path<? extends SShowActs> path) {
    super(path.getType(), path.getMetadata(), "null", "show_acts");
    addMetadata();
  }

  public SShowActs(PathMetadata metadata) {
    super(SShowActs.class, metadata, "null", "show_acts");
    addMetadata();
  }

  public void addMetadata() {}
}
