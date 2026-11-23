package fluentq.collections;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.core.types.Expression;
import fluentq.core.types.TemplatesTestUtils;
import fluentq.core.types.dsl.Expressions;
import org.junit.jupiter.api.Test;

public class CollQueryTemplatesTest {

  @Test
  public void generic_precedence() {
    TemplatesTestUtils.testPrecedence(CollQueryTemplates.DEFAULT);
  }

  @Test
  public void concat() {
    var a = Expressions.stringPath("a");
    var b = Expressions.stringPath("b");
    Expression<?> expr = a.append(b).toLowerCase();
    var str = new CollQuerySerializer(CollQueryTemplates.DEFAULT).handle(expr).toString();
    assertThat(str).isEqualTo("(a + b).toLowerCase()");
  }
}
