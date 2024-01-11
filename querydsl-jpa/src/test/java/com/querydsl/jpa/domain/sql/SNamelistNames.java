package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import javax.annotation.processing.Generated;

/** SNamelistNames is a Querydsl query type for SNamelistNames */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SNamelistNames extends com.querydsl.sql.RelationalPathBase<SNamelistNames> {

  private static final long serialVersionUID = -136041577;

  public static final SNamelistNames namelistNames = new SNamelistNames("namelist_names");

  public SNamelistNames(String variable) {
    super(SNamelistNames.class, forVariable(variable), "null", "namelist_names");
    addMetadata();
  }

  public SNamelistNames(String variable, String schema, String table) {
    super(SNamelistNames.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SNamelistNames(String variable, String schema) {
    super(SNamelistNames.class, forVariable(variable), schema, "namelist_names");
    addMetadata();
  }

  public SNamelistNames(Path<? extends SNamelistNames> path) {
    super(path.getType(), path.getMetadata(), "null", "namelist_names");
    addMetadata();
  }

  public SNamelistNames(PathMetadata metadata) {
    super(SNamelistNames.class, metadata, "null", "namelist_names");
    addMetadata();
  }

  public void addMetadata() {}
}
