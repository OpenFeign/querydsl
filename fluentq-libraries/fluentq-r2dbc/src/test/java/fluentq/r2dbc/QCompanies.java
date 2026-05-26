package fluentq.r2dbc;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import fluentq.sql.ColumnMetadata;
import fluentq.sql.PrimaryKey;
import fluentq.sql.RelationalPathBase;
import jakarta.annotation.Generated;

/** QCompanies is a FluentQ query type for QCompanies */
@Generated("fluentq.sql.codegen.MetaDataSerializer")
public class QCompanies extends RelationalPathBase<QCompanies> {

  private static final long serialVersionUID = 1808918375;

  public static final QCompanies companies = new QCompanies("COMPANIES");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final StringPath name = createString("name");

  public final PrimaryKey<QCompanies> constraint5 = createPrimaryKey(id);

  public QCompanies(String variable) {
    super(QCompanies.class, forVariable(variable), "PUBLIC", "COMPANIES");
    addMetadata();
  }

  public QCompanies(Path<? extends QCompanies> path) {
    super(path.getType(), path.getMetadata(), "PUBLIC", "COMPANIES");
    addMetadata();
  }

  public QCompanies(PathMetadata metadata) {
    super(QCompanies.class, metadata, "PUBLIC", "COMPANIES");
    addMetadata();
  }

  protected void addMetadata() {
    addMetadata(id, ColumnMetadata.named("ID"));
    addMetadata(name, ColumnMetadata.named("NAME"));
  }
}
