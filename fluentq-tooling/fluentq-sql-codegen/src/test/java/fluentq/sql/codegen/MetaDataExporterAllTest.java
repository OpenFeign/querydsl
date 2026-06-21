package fluentq.sql.codegen;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import fluentq.codegen.BeanSerializer;
import fluentq.codegen.utils.SimpleCompiler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.tools.JavaCompiler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

@Tag("fluentq.core.testutil.SlowTest")
public class MetaDataExporterAllTest {

  @TempDir File folder;

  private static Connection connection;
  private static DatabaseMetaData metadata;
  private static JavaCompiler compiler = new SimpleCompiler();

  @BeforeAll
  public static void setUpClass() throws ClassNotFoundException, SQLException {
    Class.forName("org.h2.Driver");
    var url = "jdbc:h2:mem:testdb" + System.currentTimeMillis() + ";MODE=legacy";
    connection = DriverManager.getConnection(url, "sa", "");
    metadata = connection.getMetaData();
    MetaDataExporterTest.createTables(connection);
  }

  @AfterAll
  public static void tearDownClass() throws SQLException {
    connection.close();
  }

  public static List<Object[]> parameters() {
    List<Object[]> params = new ArrayList<>();

    List<Class<? extends NamingStrategy>> ns =
        Arrays.<Class<? extends NamingStrategy>>asList(
            DefaultNamingStrategy.class, OriginalNamingStrategy.class);
    List<String> prefixOrSuffix = Arrays.asList("", "Q");
    List<String> beanPackage = Arrays.asList(null, "test2");
    List<Boolean> booleans = Arrays.asList(true, false);

    for (String namePrefix : prefixOrSuffix) {
      for (String nameSuffix : prefixOrSuffix) {
        for (String beanPrefix : prefixOrSuffix) {
          for (String beanSuffix : prefixOrSuffix) {
            for (String beanPackageName : beanPackage) {
              for (Class<? extends NamingStrategy> namingStrategy : ns) {
                for (boolean withBeans : booleans) {
                  for (boolean withInnerClasses : booleans) {
                    for (boolean withOrdinalPositioning : booleans) {
                      for (boolean exportColumns : booleans) {
                        for (boolean schemaToPackage : booleans) {
                          if (withBeans && beanPackageName == null) {
                            if (Objects.equals(namePrefix, beanPrefix)
                                && Objects.equals(nameSuffix, beanSuffix)) {
                              continue;
                            }
                          }

                          if (withInnerClasses && withOrdinalPositioning) {
                            continue;
                          }

                          params.add(
                              new Object[] {
                                namePrefix,
                                nameSuffix,
                                beanPrefix,
                                beanSuffix,
                                beanPackageName,
                                namingStrategy,
                                withBeans,
                                withInnerClasses,
                                withOrdinalPositioning,
                                exportColumns,
                                schemaToPackage
                              });
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    return params;
  }

  @TestFactory
  public Stream<DynamicTest> export() {
    return parameters().stream().map(p -> dynamicTest(Arrays.toString(p), () -> runExport(p)));
  }

  @SuppressWarnings("unchecked")
  private void runExport(Object[] p) throws SQLException, IOException {
    var namePrefix = (String) p[0];
    var nameSuffix = (String) p[1];
    var beanPrefix = (String) p[2];
    var beanSuffix = (String) p[3];
    var beanPackageName = (String) p[4];
    var namingStrategyClass = (Class<? extends NamingStrategy>) p[5];
    var withBeans = (boolean) p[6];
    var withInnerClasses = (boolean) p[7];
    var withOrdinalPositioning = (boolean) p[8];
    var exportColumns = (boolean) p[9];
    var schemaToPackage = (boolean) p[10];

    var targetFolder = Files.createTempDirectory(folder.toPath(), "case").toFile();

    var config = new MetadataExporterConfigImpl();
    config.setColumnAnnotations(exportColumns);
    config.setSchemaPattern("PUBLIC");
    config.setNamePrefix(namePrefix);
    config.setNameSuffix(nameSuffix);
    config.setBeanPrefix(beanPrefix);
    config.setBeanSuffix(beanSuffix);
    config.setInnerClassesForKeys(withInnerClasses);
    config.setPackageName("test");
    config.setBeanPackageName(beanPackageName);
    config.setTargetFolder(targetFolder);
    config.setNamingStrategyClass(namingStrategyClass);
    config.setSchemaToPackage(schemaToPackage);
    if (withBeans) {
      config.setBeanSerializerClass(BeanSerializer.class);
    }
    if (withOrdinalPositioning) {
      config.setColumnComparatorClass(OrdinalPositionComparator.class);
    }

    var exporter = new MetaDataExporter(config);
    exporter.export(metadata);

    var classes = exporter.getClasses();
    var compilationResult =
        compiler.run(null, System.out, System.err, classes.toArray(new String[0]));
    if (compilationResult != 0) {
      fail("Compilation Failed for " + targetFolder.getPath());
    }
  }
}
