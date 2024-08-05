package com.querydsl.codegen;

import com.querydsl.core.annotations.QueryEmbedded;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.domain.EmbeddableWithoutQType;
import org.junit.Ignore;

@Ignore
public class ExternalEmbeddableTest {

  @QueryEntity
  public static class EntityWithExternalEmbeddable {

    @QueryEmbedded EmbeddableWithoutQType embeddable;
  }
}
