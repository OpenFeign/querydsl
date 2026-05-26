import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.support.ClassUtils;
import org.junit.Test;

public class NestedTest {

  public static class Inner {}

  @Test
  public void ClassUtils_getName() {
    var name = ClassUtils.getName(NestedTest.Inner.class);
    assertThat(name).isEqualTo("NestedTest.Inner");
  }

  @Test
  public void ClassType_getName() {
    assertThat(new ClassType(NestedTest.Inner.class).getFullName()).isEqualTo("NestedTest.Inner");
  }
}
