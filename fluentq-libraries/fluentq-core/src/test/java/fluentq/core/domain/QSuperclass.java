package fluentq.core.domain;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.EntityPathBase;
import fluentq.core.types.dsl.ListPath;
import fluentq.core.types.dsl.PathInits;
import jakarta.annotation.Generated;
import java.io.Serial;

/** QSuperclass is a FluentQ query type for Superclass */
@Generated("fluentq.codegen.EntitySerializer")
public class QSuperclass extends EntityPathBase<Superclass> {

  @Serial private static final long serialVersionUID = -1300377102;

  public static final QSuperclass superclass = new QSuperclass("superclass");

  public final ListPath<IdNamePair<String>, QIdNamePair> fooOfSuperclass =
      this.<IdNamePair<String>, QIdNamePair>createList(
          "fooOfSuperclass", IdNamePair.class, QIdNamePair.class, PathInits.DIRECT);

  public QSuperclass(String variable) {
    super(Superclass.class, forVariable(variable));
  }

  public QSuperclass(Path<? extends Superclass> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  public QSuperclass(PathMetadata metadata) {
    super(Superclass.class, metadata);
  }
}
