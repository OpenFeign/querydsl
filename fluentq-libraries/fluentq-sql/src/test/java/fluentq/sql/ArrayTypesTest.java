package fluentq.sql;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Types;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArrayTypesTest {

  private Configuration configuration;

  @BeforeEach
  public void setUp() {
    configuration = Configuration.DEFAULT;
  }

  @Test
  public void test() {
    assertThat(getJavaType("_integer")).isEqualTo(Integer[].class);
    assertThat(getJavaType("integer[]")).isEqualTo(Integer[].class);
    assertThat(getJavaType("INTEGER ARRAY")).isEqualTo(Integer[].class);
  }

  private Class<?> getJavaType(String typeName) {
    return configuration.getJavaType(Types.ARRAY, typeName, 0, 0, "", "");
  }
}
