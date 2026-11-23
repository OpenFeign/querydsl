package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SCompany_ is a FluentQ query type for SCompany_ */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SCompany_ extends fluentq.sql.RelationalPathBase<SCompany_> {

  private static final long serialVersionUID = -590751734;

  public static final SCompany_ company_ = new SCompany_("company_");

  public final NumberPath<Integer> ceoId = createNumber("ceoId", Integer.class);

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath name = createString("name");

  public final StringPath officialName = createString("officialName");

  public final NumberPath<Integer> ratingordinal = createNumber("ratingordinal", Integer.class);

  public final StringPath ratingstring = createString("ratingstring");

  public final fluentq.sql.PrimaryKey<SCompany_> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SEmployee_> company_CEOIDFK = createForeignKey(ceoId, "ID");

  public final fluentq.sql.ForeignKey<SCompany_department_> _company_department_CompanyIDFK =
      createInvForeignKey(id, "Company_ID");

  public final fluentq.sql.ForeignKey<SDepartment_> _department_COMPANYIDFK =
      createInvForeignKey(id, "COMPANY_ID");

  public final fluentq.sql.ForeignKey<SEmployee_> _employee_COMPANYIDFK =
      createInvForeignKey(id, "COMPANY_ID");

  public final fluentq.sql.ForeignKey<SUser_> _user_COMPANYIDFK =
      createInvForeignKey(id, "COMPANY_ID");

  public SCompany_(String variable) {
    super(SCompany_.class, forVariable(variable), "null", "company_");
    addMetadata();
  }

  public SCompany_(String variable, String schema, String table) {
    super(SCompany_.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SCompany_(String variable, String schema) {
    super(SCompany_.class, forVariable(variable), schema, "company_");
    addMetadata();
  }

  public SCompany_(Path<? extends SCompany_> path) {
    super(path.getType(), path.getMetadata(), "null", "company_");
    addMetadata();
  }

  public SCompany_(PathMetadata metadata) {
    super(SCompany_.class, metadata, "null", "company_");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        ceoId, ColumnMetadata.named("CEO_ID").withIndex(6).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        name, ColumnMetadata.named("NAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        officialName,
        ColumnMetadata.named("official_name").withIndex(3).ofType(Types.VARCHAR).withSize(255));
    addMetadata(
        ratingordinal,
        ColumnMetadata.named("RATINGORDINAL").withIndex(4).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        ratingstring,
        ColumnMetadata.named("RATINGSTRING").withIndex(5).ofType(Types.VARCHAR).withSize(255));
  }
}
