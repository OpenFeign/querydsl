package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SDepartment_employee_ is a Querydsl query type for SDepartment_employee_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SDepartment_employee_
    extends com.querydsl.sql.RelationalPathBase<SDepartment_employee_> {

  private static final long serialVersionUID = 1771153228;

  public static final SDepartment_employee_ department_employee_ =
      new SDepartment_employee_("department__employee_");

  public final NumberPath<Integer> departmentID = createNumber("departmentID", Integer.class);

  public final NumberPath<Integer> employeesID = createNumber("employeesID", Integer.class);

  public final com.querydsl.sql.PrimaryKey<SDepartment_employee_> primary =
      createPrimaryKey(departmentID, employeesID);

  public final com.querydsl.sql.ForeignKey<SDepartment_> department_employee_DepartmentIDFK =
      createForeignKey(departmentID, "ID");

  public final com.querydsl.sql.ForeignKey<SEmployee_> department_employee_employeesIDFK =
      createForeignKey(employeesID, "ID");

  public SDepartment_employee_(String variable) {
    super(SDepartment_employee_.class, forVariable(variable), "null", "department__employee_");
    addMetadata();
  }

  public SDepartment_employee_(String variable, String schema, String table) {
    super(SDepartment_employee_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SDepartment_employee_(String variable, String schema) {
    super(SDepartment_employee_.class, forVariable(variable), schema, "department__employee_");
    addMetadata();
  }

  public SDepartment_employee_(Path<? extends SDepartment_employee_> path) {
    super(path.getType(), path.getMetadata(), "null", "department__employee_");
    addMetadata();
  }

  public SDepartment_employee_(PathMetadata metadata) {
    super(SDepartment_employee_.class, metadata, "null", "department__employee_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        departmentID,
        ColumnMetadata.named("Department_ID")
            .withIndex(1)
            .ofType(Types.INTEGER)
            .withSize(10)
            .notNull());
    addMetadata(
        employeesID,
        ColumnMetadata.named("employees_ID")
            .withIndex(2)
            .ofType(Types.INTEGER)
            .withSize(10)
            .notNull());
  }
}
