package fluentq.apt.domain;

import fluentq.apt.domain.custom.CustomNumber;
import fluentq.core.annotations.QueryEntity;
import org.junit.Ignore;

@Ignore
public class NumberTest {

  @QueryEntity
  public static class Entity {

    CustomNumber customNumber;
  }
}
