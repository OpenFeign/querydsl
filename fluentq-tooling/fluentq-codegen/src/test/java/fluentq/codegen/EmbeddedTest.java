package fluentq.codegen;

import fluentq.core.annotations.QueryEmbeddable;
import fluentq.core.annotations.QueryEmbedded;
import fluentq.core.annotations.QueryEntity;
import fluentq.core.annotations.QuerySupertype;

public class EmbeddedTest extends AbstractExporterTest {

  @QueryEntity
  public static class EntityClass extends AbstractEntity<SubEntityCode> {}

  @QuerySupertype
  public abstract static class AbstractEntity<C extends EntityCode> {

    @QueryEmbedded public C code;
  }

  @QuerySupertype
  public static class EntityCode {

    public String code;
  }

  @QueryEmbeddable
  public static class SubEntityCode extends EntityCode {

    public String property;
  }
}
