package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SEmployeeJOBFUNCTIONS is a Querydsl query type for SEmployeeJOBFUNCTIONS */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SEmployeeJOBFUNCTIONS
    extends com.querydsl.sql.RelationalPathBase<SEmployeeJOBFUNCTIONS> {

  private static final long serialVersionUID = -40205644;

  public static final SEmployeeJOBFUNCTIONS EmployeeJOBFUNCTIONS =
      new SEmployeeJOBFUNCTIONS("Employee_JOBFUNCTIONS");

  public final NumberPath<Integer> employeeID = createNumber("employeeID", Integer.class);

  public final StringPath jobfunction = createString("jobfunction");

  public final com.querydsl.sql.ForeignKey<SEmployee_> employeeJOBFUNCTIONSEmployeeIDFK =
      createForeignKey(employeeID, "ID");

  public SEmployeeJOBFUNCTIONS(String variable) {
    super(SEmployeeJOBFUNCTIONS.class, forVariable(variable), "null", "Employee_JOBFUNCTIONS");
    addMetadata();
  }

  public SEmployeeJOBFUNCTIONS(String variable, String schema, String table) {
    super(SEmployeeJOBFUNCTIONS.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SEmployeeJOBFUNCTIONS(String variable, String schema) {
    super(SEmployeeJOBFUNCTIONS.class, forVariable(variable), schema, "Employee_JOBFUNCTIONS");
    addMetadata();
  }

  public SEmployeeJOBFUNCTIONS(Path<? extends SEmployeeJOBFUNCTIONS> path) {
    super(path.getType(), path.getMetadata(), "null", "Employee_JOBFUNCTIONS");
    addMetadata();
  }

  public SEmployeeJOBFUNCTIONS(PathMetadata metadata) {
    super(SEmployeeJOBFUNCTIONS.class, metadata, "null", "Employee_JOBFUNCTIONS");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        employeeID,
        ColumnMetadata.named("Employee_ID").withIndex(1).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        jobfunction,
        ColumnMetadata.named("jobfunction").withIndex(2).ofType(Types.VARCHAR).withSize(255));
  }
}
