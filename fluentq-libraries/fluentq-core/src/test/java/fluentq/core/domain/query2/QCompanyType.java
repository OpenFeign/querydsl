package fluentq.core.domain.query2;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.domain.Company;
import fluentq.core.domain.QCompanyPK;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.EntityPathBase;
import fluentq.core.types.dsl.PathInits;
import jakarta.annotation.Generated;
import java.io.Serial;

/** QCompany is a FluentQ query type for Company */
@Generated("fluentq.codegen.EntitySerializer")
public class QCompanyType extends EntityPathBase<Company> {

  @Serial private static final long serialVersionUID = 616888712;

  private static final PathInits INITS = PathInits.DIRECT;

  public static final QCompanyType company = new QCompanyType("company");

  public final QCompanyPK key;

  public QCompanyType(String variable) {
    this(Company.class, forVariable(variable), INITS);
  }

  public QCompanyType(PathMetadata metadata) {
    this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
  }

  public QCompanyType(PathMetadata metadata, PathInits inits) {
    this(Company.class, metadata, inits);
  }

  public QCompanyType(Class<? extends Company> type, PathMetadata metadata, PathInits inits) {
    super(type, metadata, inits);
    this.key = inits.isInitialized("key") ? new QCompanyPK(forProperty("key")) : null;
  }
}
