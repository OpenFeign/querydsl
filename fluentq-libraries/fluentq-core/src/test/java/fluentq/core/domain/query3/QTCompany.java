package fluentq.core.domain.query3;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.domain.Company;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.EntityPathBase;
import fluentq.core.types.dsl.PathInits;
import jakarta.annotation.Generated;
import java.io.Serial;

/** QCompany is a FluentQ query type for Company */
@Generated("fluentq.codegen.EntitySerializer")
public class QTCompany extends EntityPathBase<Company> {

  @Serial private static final long serialVersionUID = 616888712;

  private static final PathInits INITS = PathInits.DIRECT;

  public static final QTCompany company = new QTCompany("company");

  public final QTCompanyPK key;

  public QTCompany(String variable) {
    this(Company.class, forVariable(variable), INITS);
  }

  public QTCompany(PathMetadata metadata) {
    this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
  }

  public QTCompany(PathMetadata metadata, PathInits inits) {
    this(Company.class, metadata, inits);
  }

  public QTCompany(Class<? extends Company> type, PathMetadata metadata, PathInits inits) {
    super(type, metadata, inits);
    this.key = inits.isInitialized("key") ? new QTCompanyPK(forProperty("key")) : null;
  }
}
