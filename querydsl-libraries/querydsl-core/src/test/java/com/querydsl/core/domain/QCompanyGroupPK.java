package com.querydsl.core.domain;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import jakarta.annotation.Generated;
import java.io.Serial;

/** QCompanyGroupPK is a Querydsl query type for CompanyGroupPK */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
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
