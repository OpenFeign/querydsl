package com.querydsl.sql.codegen;

import static org.assertj.core.api.Assertions.fail;

import com.querydsl.codegen.BeanSerializer;
import com.querydsl.codegen.utils.SimpleCompiler;
import com.querydsl.core.testutil.Parallelized;
import com.querydsl.core.testutil.SlowTest;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.tools.JavaCompiler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(value = Parallelized.class)
@Category(SlowTest.class)
public class MetaDataExporterAllTest {

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  private static Connection connection;
  private static DatabaseMetaData metadata;
  private static JavaCompiler compiler = new SimpleCompiler();

  private String namePrefix, nameSuffix, beanPrefix, beanSuffix;
  private String beanPackageName;
  private Class<? extends BeanSerializer> beanSerializer = BeanSerializer.class;
  private Class<? extends NamingStrategy> namingStrategyClass;
  private boolean withBeans, withInnerClasses, withOrdinalPositioning;
  private boolean exportColumns, schemaToPackage;

  @BeforeClass
  public static void setUpClass() throws ClassNotFoundException, SQLException {
    Class.forName("org.h2.Driver");
    String url = "jdbc:h2:mem:testdb" + System.currentTimeMillis() + ";MODE=legacy";
    connection = DriverManager.getConnection(url, "sa", "");
    metadata = connection.getMetaData();
    MetaDataExporterTest.createTables(connection);
  }

  @AfterClass
  public static void tearDownClass() throws SQLException {
    connection.close();
  }

  @Parameterized.Parameters
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

  public MetaDataExporterAllTest(
      String namePrefix,
      String nameSuffix,
      String beanPrefix,
      String beanSuffix,
      String beanPackageName,
      Class<? extends NamingStrategy> namingStrategy,
      boolean withBeans,
      boolean withInnerClasses,
      boolean withOrdinalPositioning,
      boolean exportColumns,
      boolean schemaToPackage) {
    this.namePrefix = namePrefix;
    this.nameSuffix = nameSuffix;
    this.beanPrefix = beanPrefix;
    this.beanSuffix = beanSuffix;
    this.beanPackageName = beanPackageName;
    this.schemaToPackage = schemaToPackage;
    this.namingStrategyClass = namingStrategy;
    this.withBeans = withBeans;
    this.withInnerClasses = withInnerClasses;
    this.withOrdinalPositioning = withOrdinalPositioning;
    this.exportColumns = exportColumns;
  }

  @Test
  public void export() throws SQLException, IOException {
    MetadataExporterConfigImpl config = new MetadataExporterConfigImpl();
    config.setColumnAnnotations(exportColumns);
    config.setSchemaPattern("PUBLIC");
    config.setNamePrefix(namePrefix);
    config.setNameSuffix(nameSuffix);
    config.setBeanPrefix(beanPrefix);
    config.setBeanSuffix(beanSuffix);
    config.setInnerClassesForKeys(withInnerClasses);
    config.setPackageName("test");
    config.setBeanPackageName(beanPackageName);
    config.setTargetFolder(folder.getRoot());
    config.setNamingStrategyClass(namingStrategyClass);
    config.setSchemaToPackage(schemaToPackage);
    if (withBeans) {
      config.setBeanSerializerClass(beanSerializer);
    }
    if (withOrdinalPositioning) {
      config.setColumnComparatorClass(OrdinalPositionComparator.class);
    }

    MetaDataExporter exporter = new MetaDataExporter(config);
    exporter.export(metadata);

    Set<String> classes = exporter.getClasses();
    int compilationResult =
        compiler.run(null, System.out, System.err, classes.toArray(new String[0]));
    if (compilationResult != 0) {
      fail("", "Compilation Failed for " + folder.getRoot().getPath());
    }
  }
}
