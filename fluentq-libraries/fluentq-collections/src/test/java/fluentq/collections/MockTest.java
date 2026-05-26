package fluentq.collections;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.core.types.dsl.Expressions;
import fluentq.core.types.dsl.SimplePath;
import java.util.Arrays;
import java.util.List;
import org.easymock.EasyMock;
import org.junit.Test;

public class MockTest {

  @Test
  public void test() {
    List<MockTest> tests = Arrays.asList(new MockTest(), new MockTest(), new MockTest());
    SimplePath<MockTest> path = Expressions.path(MockTest.class, "obj");
    var mock = (MockTest) EasyMock.createMock(MockTest.class);
    assertThat(CollQueryFactory.from(path, tests).where(path.eq(mock)).fetch()).isEmpty();
  }
}
