package fluentq.core.domain.query3;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.domain.IdNamePair;
import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.BeanPath;
import fluentq.core.types.dsl.SimplePath;
import fluentq.core.types.dsl.StringPath;
import jakarta.annotation.Generated;
import java.io.Serial;

/** QIdNamePair is a FluentQ query type for IdNamePair */
@Generated("fluentq.codegen.EmbeddableSerializer")
public class QTIdNamePair extends BeanPath<IdNamePair<?>> {

  @Serial private static final long serialVersionUID = -1491444395;

  public static final QTIdNamePair idNamePair = new QTIdNamePair("idNamePair");

  public final StringPath id = createString("id");

  public final SimplePath<Object> name = createSimple("name", Object.class);

  @SuppressWarnings("unchecked")
  public QTIdNamePair(String variable) {
    super((Class) IdNamePair.class, forVariable(variable));
  }

  public QTIdNamePair(Path<? extends IdNamePair<?>> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  @SuppressWarnings("unchecked")
  public QTIdNamePair(PathMetadata metadata) {
    super((Class) IdNamePair.class, metadata);
  }
}
