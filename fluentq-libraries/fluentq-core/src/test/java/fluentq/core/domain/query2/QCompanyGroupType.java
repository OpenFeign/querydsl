package fluentq.core.domain.query2;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.domain.CompanyGroup;
import fluentq.core.domain.QCompany;
import fluentq.core.domain.QCompanyGroupPK;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.EntityPathBase;
import fluentq.core.types.dsl.PathInits;
import jakarta.annotation.Generated;
import java.io.Serial;

/** QCompanyGroup is a FluentQ query type for CompanyGroup */
@Generated("fluentq.codegen.EntitySerializer")
public class QCompanyGroupType extends EntityPathBase<CompanyGroup> {

  @Serial private static final long serialVersionUID = 144687575;

  private static final PathInits INITS = PathInits.DIRECT;

  public static final QCompanyGroupType companyGroup = new QCompanyGroupType("companyGroup");

  public final QCompanyGroupPK key;

  public final QCompany mainCompany;

  public QCompanyGroupType(String variable) {
    this(CompanyGroup.class, forVariable(variable), INITS);
  }

  public QCompanyGroupType(PathMetadata metadata) {
    this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
  }

  public QCompanyGroupType(PathMetadata metadata, PathInits inits) {
    this(CompanyGroup.class, metadata, inits);
  }

  public QCompanyGroupType(
      Class<? extends CompanyGroup> type, PathMetadata metadata, PathInits inits) {
    super(type, metadata, inits);
    this.key = inits.isInitialized("key") ? new QCompanyGroupPK(forProperty("key")) : null;
    this.mainCompany =
        inits.isInitialized("mainCompany")
            ? new QCompany(forProperty("mainCompany"), inits.get("mainCompany"))
            : null;
  }
}
