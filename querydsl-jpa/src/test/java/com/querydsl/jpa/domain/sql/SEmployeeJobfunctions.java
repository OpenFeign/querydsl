package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import javax.annotation.processing.Generated;

/** SEmployeeJobfunctions is a Querydsl query type for SEmployeeJobfunctions */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SEmployeeJobfunctions
    extends com.querydsl.sql.RelationalPathBase<SEmployeeJobfunctions> {

  private static final long serialVersionUID = 2101800660;

  public static final SEmployeeJobfunctions employeeJobfunctions =
      new SEmployeeJobfunctions("employee_jobfunctions");

  public SEmployeeJobfunctions(String variable) {
    super(SEmployeeJobfunctions.class, forVariable(variable), "null", "employee_jobfunctions");
    addMetadata();
  }

  public SEmployeeJobfunctions(String variable, String schema, String table) {
    super(SEmployeeJobfunctions.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SEmployeeJobfunctions(String variable, String schema) {
    super(SEmployeeJobfunctions.class, forVariable(variable), schema, "employee_jobfunctions");
    addMetadata();
  }

  public SEmployeeJobfunctions(Path<? extends SEmployeeJobfunctions> path) {
    super(path.getType(), path.getMetadata(), "null", "employee_jobfunctions");
    addMetadata();
  }

  public SEmployeeJobfunctions(PathMetadata metadata) {
    super(SEmployeeJobfunctions.class, metadata, "null", "employee_jobfunctions");
    addMetadata();
  }

  public void addMetadata() {}
}
