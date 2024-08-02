package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SDepartment_ is a Querydsl query type for SDepartment_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SDepartment_ extends com.querydsl.sql.RelationalPathBase<SDepartment_> {

  private static final long serialVersionUID = -1571818043;

  public static final SDepartment_ department_ = new SDepartment_("department_");

  public final NumberPath<Integer> companyId = createNumber("companyId", Integer.class);

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath name = createString("name");

  public final com.querydsl.sql.PrimaryKey<SDepartment_> primary = createPrimaryKey(id);

  public final com.querydsl.sql.ForeignKey<SCompany_> department_COMPANYIDFK =
      createForeignKey(companyId, "ID");

  public final com.querydsl.sql.ForeignKey<SCompany_department_>
      _company_department_departmentsIDFK = createInvForeignKey(id, "departments_ID");

  public final com.querydsl.sql.ForeignKey<SDepartment_employee_>
      _department_employee_DepartmentIDFK = createInvForeignKey(id, "Department_ID");

  public SDepartment_(String variable) {
    super(SDepartment_.class, forVariable(variable), "null", "department_");
    addMetadata();
  }

  public SDepartment_(String variable, String schema, String table) {
    super(SDepartment_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SDepartment_(String variable, String schema) {
    super(SDepartment_.class, forVariable(variable), schema, "department_");
    addMetadata();
  }

  public SDepartment_(Path<? extends SDepartment_> path) {
    super(path.getType(), path.getMetadata(), "null", "department_");
    addMetadata();
  }

  public SDepartment_(PathMetadata metadata) {
    super(SDepartment_.class, metadata, "null", "department_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        companyId,
        ColumnMetadata.named("COMPANY_ID").withIndex(3).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
  }
}
