package fluentq.codegen.utils;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.codegen.utils.model.ClassType;
import fluentq.codegen.utils.model.SimpleType;
import fluentq.codegen.utils.model.Type;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Test;

public class InnerClassesTest {

  public static class Entity {}

  @Test
  public void DirectParameter() throws IOException {
    Type entityType = new ClassType(Entity.class);
    Type type =
        new SimpleType(
            "fluentq.codegen.utils.gen.QEntity",
            "fluentq.codegen.utils.gen",
            "QEntity",
            entityType);

    var str = new StringWriter();
    var writer = new JavaWriter(str);
    writer.beginClass(type);
    writer.end();

    System.err.println(str.toString());
  }

  @Test
  public void Java() {
    var str = new StringWriter();
    var writer = new JavaWriter(str);

    assertThat(writer.getRawName(new ClassType(Entity.class)))
        .isEqualTo("fluentq.codegen.utils.InnerClassesTest.Entity");
  }

  @Test
  public void Scala() {
    var str = new StringWriter();
    var writer = new ScalaWriter(str);

    assertThat(writer.getRawName(new ClassType(Entity.class)))
        .isEqualTo("fluentq.codegen.utils.InnerClassesTest$Entity");
  }
}
