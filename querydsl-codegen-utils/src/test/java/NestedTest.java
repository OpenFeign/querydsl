import static org.junit.jupiter.api.Assertions.assertEquals;

import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.support.ClassUtils;
import org.junit.Test;

public class NestedTest {

  public static class Inner {}

  @Test
  public void ClassUtils_getName() {
    String name = ClassUtils.getName(NestedTest.Inner.class);
    assertEquals("NestedTest.Inner", name);
  }

  @Test
  public void ClassType_getName() {
    assertEquals("NestedTest.Inner", new ClassType(NestedTest.Inner.class).getFullName());
  }
}
