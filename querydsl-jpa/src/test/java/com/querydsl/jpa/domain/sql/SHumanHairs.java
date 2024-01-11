package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import javax.annotation.processing.Generated;

/** SHumanHairs is a Querydsl query type for SHumanHairs */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SHumanHairs extends com.querydsl.sql.RelationalPathBase<SHumanHairs> {

  private static final long serialVersionUID = 189604396;

  public static final SHumanHairs humanHairs = new SHumanHairs("human_hairs");

  public SHumanHairs(String variable) {
    super(SHumanHairs.class, forVariable(variable), "null", "human_hairs");
    addMetadata();
  }

  public SHumanHairs(String variable, String schema, String table) {
    super(SHumanHairs.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SHumanHairs(String variable, String schema) {
    super(SHumanHairs.class, forVariable(variable), schema, "human_hairs");
    addMetadata();
  }

  public SHumanHairs(Path<? extends SHumanHairs> path) {
    super(path.getType(), path.getMetadata(), "null", "human_hairs");
    addMetadata();
  }

  public SHumanHairs(PathMetadata metadata) {
    super(SHumanHairs.class, metadata, "null", "human_hairs");
    addMetadata();
  }

  public void addMetadata() {}
}
