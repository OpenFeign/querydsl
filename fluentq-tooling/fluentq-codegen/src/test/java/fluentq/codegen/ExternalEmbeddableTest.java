package fluentq.codegen;

import fluentq.core.annotations.QueryEmbedded;
import fluentq.core.annotations.QueryEntity;
import fluentq.core.domain.EmbeddableWithoutQType;
import org.junit.Ignore;

@Ignore
public class ExternalEmbeddableTest {

  @QueryEntity
  public static class EntityWithExternalEmbeddable {

    @QueryEmbedded EmbeddableWithoutQType embeddable;
  }
}
