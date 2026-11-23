package fluentq.codegen;

import static org.assertj.core.api.Assertions.fail;

import fluentq.codegen.utils.MemFileManager;
import fluentq.codegen.utils.MemSourceFileObject;
import fluentq.codegen.utils.SimpleCompiler;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.tools.SimpleJavaFileObject;

public final class CompileUtils {

  private CompileUtils() {}

  public static void assertCompiles(String name, String source) {
    var parent = CompileUtils.class.getClassLoader();
    var compiler = new SimpleCompiler();
    var fileManager = new MemFileManager(parent, compiler.getStandardFileManager(null, null, null));
    var classpath = SimpleCompiler.getClassPath(parent);
    List<String> compilationOptions = Arrays.asList("-classpath", classpath, "-g:none");

    // compile
    SimpleJavaFileObject javaFileObject = new MemSourceFileObject(name, source);
    Writer out = new StringWriter();
    var task =
        compiler.getTask(
            out,
            fileManager,
            null,
            compilationOptions,
            null,
            Collections.singletonList(javaFileObject));
    if (!task.call()) {
      fail("Compilation of " + source + " failed.\n" + out.toString());
    }
  }
}
