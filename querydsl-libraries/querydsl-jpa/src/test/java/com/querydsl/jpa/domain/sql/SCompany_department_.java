package com.querydsl.jpa.domain.sql;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SCompany_department_ is a Querydsl query type for SCompany_department_ */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class SCompany_department_
    extends com.querydsl.sql.RelationalPathBase<SCompany_department_> {

  private static final long serialVersionUID = -884357885;

  public static final SCompany_department_ company_department_ =
      new SCompany_department_("company__department_");

  public final NumberPath<Integer> companyID = createNumber("companyID", Integer.class);

  public final NumberPath<Integer> departmentsID = createNumber("departmentsID", Integer.class);

  public final com.querydsl.sql.PrimaryKey<SCompany_department_> primary =
      createPrimaryKey(companyID, departmentsID);

  public final com.querydsl.sql.ForeignKey<SCompany_> company_department_CompanyIDFK =
      createForeignKey(companyID, "ID");

  public final com.querydsl.sql.ForeignKey<SDepartment_> company_department_departmentsIDFK =
      createForeignKey(departmentsID, "ID");

  public SCompany_department_(String variable) {
    super(SCompany_department_.class, forVariable(variable), "null", "company__department_");
    addMetadata();
  }

  public SCompany_department_(String variable, String schema, String table) {
    super(SCompany_department_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCompany_department_(String variable, String schema) {
    super(SCompany_department_.class, forVariable(variable), schema, "company__department_");
    addMetadata();
  }

  public SCompany_department_(Path<? extends SCompany_department_> path) {
    super(path.getType(), path.getMetadata(), "null", "company__department_");
    addMetadata();
  }

  public SCompany_department_(PathMetadata metadata) {
    super(SCompany_department_.class, metadata, "null", "company__department_");
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
