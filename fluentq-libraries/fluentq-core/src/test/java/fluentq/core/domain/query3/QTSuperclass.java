package fluentq.core.domain.query3;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.domain.IdNamePair;
import fluentq.core.domain.Superclass;
import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.EntityPathBase;
import fluentq.core.types.dsl.ListPath;
import fluentq.core.types.dsl.PathInits;
import jakarta.annotation.Generated;
import java.io.Serial;

/** QSuperclass is a FluentQ query type for Superclass */
@Generated("fluentq.codegen.EntitySerializer")
public class QTSuperclass extends EntityPathBase<Superclass> {

  @Serial private static final long serialVersionUID = -1300377102;

  public static final QTSuperclass superclass = new QTSuperclass("superclass");

  public final ListPath<IdNamePair<String>, QTIdNamePair> fooOfSuperclass =
      this.<IdNamePair<String>, QTIdNamePair>createList(
          "fooOfSuperclass", IdNamePair.class, QTIdNamePair.class, PathInits.DIRECT);

  public QTSuperclass(String variable) {
    super(Superclass.class, forVariable(variable));
  }

  public QTSuperclass(Path<? extends Superclass> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  public QTSuperclass(PathMetadata metadata) {
    super(Superclass.class, metadata);
  }
}
