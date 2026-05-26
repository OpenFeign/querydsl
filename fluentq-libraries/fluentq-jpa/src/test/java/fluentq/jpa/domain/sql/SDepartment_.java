package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SDepartment_ is a FluentQ query type for SDepartment_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SDepartment_ extends fluentq.sql.RelationalPathBase<SDepartment_> {

  private static final long serialVersionUID = -1571818043;

  public static final SDepartment_ department_ = new SDepartment_("department_");

  public final NumberPath<Integer> companyId = createNumber("companyId", Integer.class);

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath name = createString("name");

  public final fluentq.sql.PrimaryKey<SDepartment_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SCompany_> department_COMPANYIDFK =
      createForeignKey(companyId, "ID");

  public final fluentq.sql.ForeignKey<SCompany_department_> _company_department_departmentsIDFK =
      createInvForeignKey(id, "departments_ID");

  public final fluentq.sql.ForeignKey<SDepartment_employee_> _department_employee_DepartmentIDFK =
      createInvForeignKey(id, "Department_ID");

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
