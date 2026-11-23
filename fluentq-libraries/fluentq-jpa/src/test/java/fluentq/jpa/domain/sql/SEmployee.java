package fluentq.jpa.domain.sql;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.DatePath;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.core.types.dsl.TimePath;
import fluentq.sql.ColumnMetadata;
import java.sql.Types;
import javax.annotation.processing.Generated;

/** SEmployee is a FluentQ query type for SEmployee */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class SEmployee extends fluentq.sql.RelationalPathBase<SEmployee> {

  private static final long serialVersionUID = 1202481974;

  public static final SEmployee employee = new SEmployee("EMPLOYEE");

  public final DatePath<java.sql.Date> datefield = createDate("datefield", java.sql.Date.class);

  public final StringPath firstname = createString("firstname");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath lastname = createString("lastname");

  public final NumberPath<Long> salary = createNumber("salary", Long.class);

  public final NumberPath<Integer> superiorId = createNumber("superiorId", Integer.class);

  public final TimePath<java.sql.Time> timefield = createTime("timefield", java.sql.Time.class);

  public final fluentq.sql.PrimaryKey<SEmployee> primary = createPrimaryKey(id);

  public final fluentq.sql.ForeignKey<SEmployee> superiorFk = createForeignKey(superiorId, "ID");

  public final fluentq.sql.ForeignKey<SEmployee> _superiorFk =
      createInvForeignKey(id, "SUPERIOR_ID");

  public SEmployee(String variable) {
    super(SEmployee.class, forVariable(variable), "null", "EMPLOYEE");
    addMetadata();
  }

  public SEmployee(String variable, String schema, String table) {
    super(SEmployee.class, forVariable(variable), schema, table);
    addMetadata();
  }

  public SEmployee(String variable, String schema) {
    super(SEmployee.class, forVariable(variable), schema, "EMPLOYEE");
    addMetadata();
  }

  public SEmployee(Path<? extends SEmployee> path) {
    super(path.getType(), path.getMetadata(), "null", "EMPLOYEE");
    addMetadata();
  }

  public SEmployee(PathMetadata metadata) {
    super(SEmployee.class, metadata, "null", "EMPLOYEE");
    addMetadata();
  }

  public void addMetadata() {
    addMetadata(
        datefield, ColumnMetadata.named("DATEFIELD").withIndex(5).ofType(Types.DATE).withSize(10));
    addMetadata(
        firstname,
        ColumnMetadata.named("FIRSTNAME").withIndex(2).ofType(Types.VARCHAR).withSize(50));
    addMetadata(
        id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.INTEGER).withSize(10).notNull());
    addMetadata(
        lastname, ColumnMetadata.named("LASTNAME").withIndex(3).ofType(Types.VARCHAR).withSize(50));
    addMetadata(
        salary, ColumnMetadata.named("SALARY").withIndex(4).ofType(Types.DECIMAL).withSize(10));
    addMetadata(
        superiorId,
        ColumnMetadata.named("SUPERIOR_ID").withIndex(7).ofType(Types.INTEGER).withSize(10));
    addMetadata(
        timefield, ColumnMetadata.named("TIMEFIELD").withIndex(6).ofType(Types.TIME).withSize(8));
  }
}
