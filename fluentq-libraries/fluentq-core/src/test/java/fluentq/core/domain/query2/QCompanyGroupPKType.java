package fluentq.core.domain.query2;

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
public class QCompanyGroupPKType extends BeanPath<CompanyGroupPK> {

  @Serial private static final long serialVersionUID = 1605808658;

  public static final QCompanyGroupPKType companyGroupPK =
      new QCompanyGroupPKType("companyGroupPK");

  public final StringPath companyNumber = createString("companyNumber");

  public final NumberPath<Long> companyType = createNumber("companyType", Long.class);

  public QCompanyGroupPKType(String variable) {
    super(CompanyGroupPK.class, forVariable(variable));
  }

  public QCompanyGroupPKType(Path<? extends CompanyGroupPK> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  public QCompanyGroupPKType(PathMetadata metadata) {
    super(CompanyGroupPK.class, metadata);
  }
}
