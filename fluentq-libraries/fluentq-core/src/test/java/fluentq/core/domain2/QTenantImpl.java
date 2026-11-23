package fluentq.core.domain2;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.EntityPathBase;
import jakarta.annotation.Generated;
import java.io.Serial;

/** QTenantImpl is a FluentQ query type for TenantImpl */
@Generated("fluentq.codegen.EntitySerializer")
public class QTenantImpl extends EntityPathBase<TenantImpl> {

  @Serial private static final long serialVersionUID = -1856470561;

  public static final QTenantImpl tenantImpl = new QTenantImpl("tenantImpl");

  public QTenantImpl(String variable) {
    super(TenantImpl.class, forVariable(variable));
  }

  public QTenantImpl(Path<? extends TenantImpl> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  public QTenantImpl(PathMetadata metadata) {
    super(TenantImpl.class, metadata);
  }
}
