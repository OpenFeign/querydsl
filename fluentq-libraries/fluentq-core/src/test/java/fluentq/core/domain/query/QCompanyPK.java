package fluentq.core.domain.query;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.domain.CompanyPK;
import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.BeanPath;
import fluentq.core.types.dsl.StringPath;
import jakarta.annotation.Generated;
import java.io.Serial;

/** QCompanyPK is a FluentQ query type for CompanyPK */
@Generated("fluentq.codegen.EmbeddableSerializer")
public class QCompanyPK extends BeanPath<CompanyPK> {

  @Serial private static final long serialVersionUID = 124567939;

  public static final QCompanyPK companyPK = new QCompanyPK("companyPK");

  public final StringPath id = createString("id");

  public QCompanyPK(String variable) {
    super(CompanyPK.class, forVariable(variable));
  }

  public QCompanyPK(Path<? extends CompanyPK> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  public QCompanyPK(PathMetadata metadata) {
    super(CompanyPK.class, metadata);
  }
}
