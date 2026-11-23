package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SEmployee_ is a FluentQ query type for SEmployee_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SEmployee_ extends fluentq.sql.RelationalPathBase<SEmployee_> {

  private static final long serialVersionUID = -1377764375;

  public static final SEmployee_ employee_ = new SEmployee_("employee_");

  public final NumberPath<Integer> companyId = createNumber("companyId", Integer.class);

  public final StringPath firstname = createString("firstname");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath lastname = createString("lastname");

  public final NumberPath<Long> userId = createNumber("userId", Long.class);

  public final fluentq.sql.PrimaryKey<SEmployee_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SCompany_> employee_COMPANYIDFK =
      createForeignKey(companyId, "ID");

  public final fluentq.sql.ForeignKey<SUser_> employee_USERIDFK = createForeignKey(userId, "ID");

  public final fluentq.sql.ForeignKey<SEmployeeJOBFUNCTIONS> _employeeJOBFUNCTIONSEmployeeIDFK =
      createInvForeignKey(id, "Employee_ID");

  public final fluentq.sql.ForeignKey<SCompany_> _company_CEOIDFK =
      createInvForeignKey(id, "CEO_ID");

  public final fluentq.sql.ForeignKey<SDepartment_employee_> _department_employee_employeesIDFK =
      createInvForeignKey(id, "employees_ID");

  public SEmployee_(String variable) {
    super(SEmployee_.class, forVariable(variable), "null", "employee_");
    addMetadata();
  }

  public SEmployee_(String variable, String schema, String table) {
    super(SEmployee_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SEmployee_(String variable, String schema) {
    super(SEmployee_.class, forVariable(variable), schema, "employee_");
    addMetadata();
  }

  public SEmployee_(Path<? extends SEmployee_> path) {
    super(path.getType(), path.getMetadata(), "null", "employee_");
    addMetadata();
  }

  public SEmployee_(PathMetadata metadata) {
    super(SEmployee_.class, metadata, "null", "employee_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        companyId,
        ColumnMetadata.named("COMPANY_ID").withIndex(4).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        firstname,
        ColumnMetadata.named("FIRSTNAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        lastname,
        ColumnMetadata.named("LASTNAME").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        userId, ColumnMetadata.named("USER_ID").withIndex(5).ofType(Types.BIGINT).withSize(19));
  }
}
