package fluentq.core.domain.query;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.domain.CompanyGroupPK;
import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.BeanPath;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.StringPath;
import jakarta.annotation.Generated;
import java.io.Serial;

/** QCompanyGroupPK is a FluentQ query type for CompanyGroupPK */
@Generated("fluentq.codegen.EmbeddableSerializer")
public class QCompanyGroupPK extends BeanPath<CompanyGroupPK> {

  @Serial private static final long serialVersionUID = 1605808658;

  public static final QCompanyGroupPK companyGroupPK = new QCompanyGroupPK("companyGroupPK");

  public final StringPath companyNumber = createString("companyNumber");

  public final NumberPath<Long> companyType = createNumber("companyType", Long.class);

  public QCompanyGroupPK(String variable) {
    super(CompanyGroupPK.class, forVariable(variable));
  }

  public QCompanyGroupPK(Path<? extends CompanyGroupPK> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  public QCompanyGroupPK(PathMetadata metadata) {
    super(CompanyGroupPK.class, metadata);
  }
}
