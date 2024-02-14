package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SCompanyDepartment_ is a Querydsl query type for SCompanyDepartment_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SCompanyDepartment_ extends com.querydsl.sql.RelationalPathBase<SCompanyDepartment_> {

  private static final long serialVersionUID = 1058032920;

  public static final SCompanyDepartment_ companyDepartment_ =
      new SCompanyDepartment_("company_department_");

  public final NumberPath<Integer> companyID = createNumber("companyID", Integer.class);

  public final NumberPath<Integer> departmentsID = createNumber("departmentsID", Integer.class);

  public final com.querydsl.sql.PrimaryKey<SCompanyDepartment_> primary =
      createPrimaryKey(companyID, departmentsID);

  public final com.querydsl.sql.ForeignKey<SCompany> companyDepartment_CompanyIDFK =
      createForeignKey(companyID, "ID");

  public final com.querydsl.sql.ForeignKey<SDepartment_> companyDepartment_departmentsIDFK =
      createForeignKey(departmentsID, "ID");

  public SCompanyDepartment_(String variable) {
    super(SCompanyDepartment_.class, forVariable(variable), "null", "company_department_");
    addMetadata();
  }

  public SCompanyDepartment_(String variable, String schema, String table) {
    super(SCompanyDepartment_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCompanyDepartment_(String variable, String schema) {
    super(SCompanyDepartment_.class, forVariable(variable), schema, "company_department_");
    addMetadata();
  }

  public SCompanyDepartment_(Path<? extends SCompanyDepartment_> path) {
    super(path.getType(), path.getMetadata(), "null", "company_department_");
    addMetadata();
  }

  public SCompanyDepartment_(PathMetadata metadata) {
    super(SCompanyDepartment_.class, metadata, "null", "company_department_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        companyID,
        ColumnMetadata.named("Company_ID")
            .withIndex(1)
            .ofType(Types.INTEGER)
            .withSize(10)
            .notNull());
    addMetadata(
        departmentsID,
        ColumnMetadata.named("departments_ID")
            .withIndex(2)
            .ofType(Types.INTEGER)
            .withSize(10)
            .notNull());
  }
}
