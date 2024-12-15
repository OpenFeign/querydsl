package com.querydsl.jpa;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.PathInits;
import jakarta.annotation.Generated;

/** QContent is a Querydsl query type for Content */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QContent extends BeanPath<Content> {

  private static final long serialVersionUID = -878421975L;

  private static final PathInits INITS = PathInits.DIRECT2;

  public static final QContent content = new QContent("content");

  public final QArticle article;

  public QContent(String variable) {
    this(Content.class, forVariable(variable), INITS);
  }

  public QContent(Path<? extends Content> path) {
    this(
        path.getType(),
        path.getMetadata(),
        path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
  }

  public QContent(PathMetadata metadata) {
    this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
  }

  public QContent(PathMetadata metadata, PathInits inits) {
    this(Content.class, metadata, inits);
  }

  public QContent(Class<? extends Content> type, PathMetadata metadata, PathInits inits) {
    super(type, metadata, inits);
    this.article =
        inits.isInitialized("article")
            ? new QArticle(forProperty("article"), inits.get("article"))
            : null;
  }
}
