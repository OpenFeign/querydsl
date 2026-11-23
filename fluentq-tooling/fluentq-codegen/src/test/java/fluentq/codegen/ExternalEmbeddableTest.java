package fluentq.codegen;

import fluentq.core.annotations.QueryEmbedded;
import fluentq.core.annotations.QueryEntity;
import fluentq.core.domain.EmbeddableWithoutQType;
import org.junit.jupiter.api.Disabled;

@Disabled
public class ExternalEmbeddableTest {

  @QueryEntity
  public static class EntityWithExternalEmbeddable {

    @QueryEmbedded EmbeddableWithoutQType embeddable;
  }
}
