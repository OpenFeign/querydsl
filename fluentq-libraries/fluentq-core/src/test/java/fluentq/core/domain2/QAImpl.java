package fluentq.core.domain2;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.EntityPathBase;
import fluentq.core.types.dsl.NumberPath;
import fluentq.core.types.dsl.PathInits;
import jakarta.annotation.Generated;
import java.io.Serial;

/** QAImpl is a FluentQ query type for AImpl */
@Generated("fluentq.codegen.EntitySerializer")
public class QAImpl extends EntityPathBase<AImpl> {

  @Serial private static final long serialVersionUID = -261443316;

  private static final PathInits INITS = PathInits.DIRECT;

  public static final QAImpl aImpl = new QAImpl("aImpl");

  public final QABase _super;

  // inherited
  public final NumberPath<Long> id;

  // inherited
  public final QTenantImpl tenant;

  public QAImpl(String variable) {
    this(AImpl.class, forVariable(variable), INITS);
  }

  public QAImpl(Path<? extends AImpl> entity) {
    this(entity.getType(), entity.getMetadata(), INITS);
  }

  public QAImpl(PathMetadata metadata) {
    this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
  }

  public QAImpl(PathMetadata metadata, PathInits inits) {
    this(AImpl.class, metadata, inits);
  }

  public QAImpl(Class<? extends AImpl> type, PathMetadata metadata, PathInits inits) {
    super(type, metadata, inits);
    this._super = new QABase(type, metadata, inits);
    this.id = _super.id;
    this.tenant = _super.tenant;
  }
}
