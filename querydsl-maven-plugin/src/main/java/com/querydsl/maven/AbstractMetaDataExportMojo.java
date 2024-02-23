/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.maven;

import com.querydsl.codegen.BeanSerializer;
import com.querydsl.codegen.Property;
import com.querydsl.codegen.Serializer;
import com.querydsl.core.util.StringUtils;
import com.querydsl.sql.codegen.MetaDataExporter;
import com.querydsl.sql.codegen.MetadataExporterConfig;
import com.querydsl.sql.codegen.NamingStrategy;
import com.querydsl.sql.codegen.support.CustomType;
import com.querydsl.sql.codegen.support.NumericMapping;
import com.querydsl.sql.codegen.support.RenameMapping;
import com.querydsl.sql.codegen.support.TypeMapping;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.wagon.authentication.AuthenticationInfo;

/**
 * {@code AbstractMetaDataExportMojo} is the base class for {@link MetaDataExporter} usage
 *
 * @author tiwe
 */
public abstract class AbstractMetaDataExportMojo extends AbstractMojo
    implements MetadataExporterConfig {

  /** maven project */
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  /** The Maven Wagon manager to use when obtaining server authentication details. */
  @Component private WagonManager wagonManager;

  /** The server id in settings.xml to use as an alternative to jdbcUser and jdbcPassword */
  @Parameter private String server;

  /** JDBC driver class name */
  @Parameter(required = true)
  private String jdbcDriver;

  /** JDBC connection url */
  @Parameter(required = true)
  private String jdbcUrl;

  /** JDBC connection username */
  @Parameter private String jdbcUser;

  /** JDBC connection password */
  @Parameter private String jdbcPassword;

  /** name prefix for querydsl-types (default: "Q") */
  @Parameter(defaultValue = "Q")
  private String namePrefix;

  /** name suffix for querydsl-types (default: "") */
  @Parameter(defaultValue = "")
  private String nameSuffix;

  /** name prefix for bean types (default: "") */
  @Parameter(defaultValue = "")
  private String beanPrefix;

  /** name suffix for bean types (default: "") */
  @Parameter(defaultValue = "")
  private String beanSuffix;

  /** package name for sources */
  @Parameter(required = true)
  private String packageName;

  /** package name for bean sources (default: packageName) */
  @Parameter private String beanPackageName;

  /**
   * schemaPattern a schema name pattern; must match the schema name as it is stored in the
   * database; "" retrieves those without a schema; {@code null} means that the schema name should
   * not be used to narrow the search (default: null)
   */
  @Parameter private String schemaPattern;

  /**
   * a catalog name; must match the catalog name as it is stored in the database; "" retrieves those
   * without a catalog; <code>null</code> means that the catalog name should not be used to narrow
   * the search
   */
  @Parameter private String catalogPattern;

  /**
   * tableNamePattern a table name pattern; must match the table name as it is stored in the
   * database (default: null)
   */
  @Parameter private String tableNamePattern;

  /** target source folder to create the sources into (e.g. target/generated-sources/java) */
  @Parameter(required = true)
  private String targetFolder;

  /** target source folder to create the bean sources into */
  @Parameter private String beansTargetFolder;

  /** namingstrategy class to override (default: DefaultNamingStrategy) */
  @Parameter private String namingStrategyClass;

  /** name for bean serializer class */
  @Parameter private String beanSerializerClass;

  /** name for serializer class */
  @Parameter private String serializerClass;

  /** serialize beans as well */
  @Parameter(defaultValue = "false")
  private boolean exportBeans;

  /** additional interfaces to be implemented by beans */
  @Parameter private String[] beanInterfaces;

  /** switch for {@code toString} addition */
  @Parameter(defaultValue = "false")
  private boolean beanAddToString;

  /** switch for full constructor addition */
  @Parameter(defaultValue = "false")
  private boolean beanAddFullConstructor;

  /** switch to print supertype content */
  @Parameter(defaultValue = "false")
  private boolean beanPrintSupertype;

  /** wrap key properties into inner classes (default: false) */
  @Parameter(defaultValue = "false")
  private boolean innerClassesForKeys;

  /** export validation annotations (default: false) */
  @Parameter(defaultValue = "false")
  private boolean validationAnnotations;

  /** export column annotations (default: false) */
  @Parameter(defaultValue = "false")
  private boolean columnAnnotations;

  /** custom type classnames to use */
  @Parameter private String[] customTypes;

  /** custom type mappings to use */
  @Parameter private TypeMapping[] typeMappings;

  /** custom numeric mappings */
  @Parameter private NumericMapping[] numericMappings;

  /** custom rename mappings */
  @Parameter private RenameMapping[] renameMappings;

  /** switch for generating scala sources */
  @Parameter(defaultValue = "false")
  private boolean createScalaSources;

  /**
   * switch for using schema as suffix in package generation, full package name will be {@code
   * ${packageName}.${schema}}
   */
  @Parameter(defaultValue = "false")
  private boolean schemaToPackage;

  /** switch to normalize schema, table and column names to lowercase */
  @Parameter(defaultValue = "false")
  private boolean lowerCase;

  /** switch to export tables */
  @Parameter(defaultValue = "true")
  private boolean exportTables;

  /** switch to export views */
  @Parameter(defaultValue = "true")
  private boolean exportViews;

  /** switch to export all types */
  @Parameter(defaultValue = "false")
  private boolean exportAll;

  /** switch to export primary keys */
  @Parameter(defaultValue = "true")
  private boolean exportPrimaryKeys;

  /** switch to export foreign keys */
  @Parameter(defaultValue = "true")
  private boolean exportForeignKeys;

  /** switch to export direct foreign keys */
  @Parameter(defaultValue = "true")
  private boolean exportDirectForeignKeys;

  /** switch to export inverse foreign keys */
  @Parameter(defaultValue = "true")
  private boolean exportInverseForeignKeys;

  /** override default column order (default: alphabetical) */
  @Parameter private String columnComparatorClass;

  /**
   * Comma-separated list of table types to export (allowable values will depend on JDBC driver).
   * Allows for arbitrary set of types to be exported, e.g.: "TABLE, MATERIALIZED VIEW". The
   * exportTables and exportViews parameters will be ignored if this parameter is set. (default:
   * none)
   */
  @Parameter private String tableTypesToExport;

  /**
   * java import added to generated query classes: com.bar for package (without .* notation)
   * com.bar.Foo for class
   */
  @Parameter private String[] imports;

  /** Whether to skip the exporting execution */
  @Parameter(defaultValue = "false", property = "maven.querydsl.skip")
  private boolean skip;

  /**
   * The fully qualified class name of the <em>Single-Element Annotation</em> (with <code>String
   * </code> element) to put on the generated sources.Defaults to <code>javax.annotation.Generated
   * </code> or <code>javax.annotation.processing.Generated</code> depending on the java version.
   * <em>See also</em> <a
   * href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.7.3">Single-Element
   * Annotation</a>
   *
   * @parameter
   */
  private String generatedAnnotationClass;

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (isForTest()) {
      project.addTestCompileSourceRoot(targetFolder);
    } else {
      project.addCompileSourceRoot(targetFolder);
    }

    if (skip) {
      return;
    }

    try {

      // defaults for Scala
      if (createScalaSources) {
        if (serializerClass == null) {
          serializerClass = "com.querydsl.scala.sql.ScalaMetaDataSerializer";
        }
        if (exportBeans && beanSerializerClass == null) {
          beanSerializerClass = "com.querydsl.scala.ScalaBeanSerializer";
        }
      }

      MetaDataExporter exporter = new MetaDataExporter(this);

      Class.forName(jdbcDriver);
      String user;
      String password;
      if (server == null) {
        user = jdbcUser;
        password = jdbcPassword;
      } else {
        AuthenticationInfo info = wagonManager.getAuthenticationInfo(server);
        if (info == null) {
          throw new MojoExecutionException("No authentication info for server " + server);
        }

        user = info.getUserName();
        if (user == null) {
          throw new MojoExecutionException("Missing username from server " + server);
        }

        password = info.getPassword();
        if (password == null) {
          throw new MojoExecutionException("Missing password from server " + server);
        }
      }
      try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password)) {
        exporter.export(conn.getMetaData());
      }
    } catch (ClassNotFoundException | SQLException e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  protected abstract boolean isForTest();

  public void setProject(MavenProject project) {
    this.project = project;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public void setJdbcDriver(String jdbcDriver) {
    this.jdbcDriver = jdbcDriver;
  }

  public void setJdbcUrl(String jdbcUrl) {
    this.jdbcUrl = jdbcUrl;
  }

  public void setJdbcUser(String jdbcUser) {
    this.jdbcUser = jdbcUser;
  }

  public void setJdbcPassword(String jdbcPassword) {
    this.jdbcPassword = jdbcPassword;
  }

  public void setNamePrefix(String namePrefix) {
    this.namePrefix = namePrefix;
  }

  public void setNameSuffix(String nameSuffix) {
    this.nameSuffix = nameSuffix;
  }

  public void setBeanInterfaces(String[] beanInterfaces) {
    this.beanInterfaces = beanInterfaces;
  }

  public void setBeanPrefix(String beanPrefix) {
    this.beanPrefix = beanPrefix;
  }

  public void setBeanSuffix(String beanSuffix) {
    this.beanSuffix = beanSuffix;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public void setBeanPackageName(String beanPackageName) {
    this.beanPackageName = beanPackageName;
  }

  public void setCatalogPattern(String catalogPattern) {
    this.catalogPattern = catalogPattern;
  }

  public void setSchemaPattern(String schemaPattern) {
    this.schemaPattern = schemaPattern;
  }

  public void setTableNamePattern(String tableNamePattern) {
    this.tableNamePattern = tableNamePattern;
  }

  public void setTargetFolder(String targetFolder) {
    this.targetFolder = targetFolder;
  }

  public void setNamingStrategyClass(String namingStrategyClass) {
    this.namingStrategyClass = namingStrategyClass;
  }

  public void setBeanSerializerClass(String beanSerializerClass) {
    this.beanSerializerClass = beanSerializerClass;
  }

  public void setSerializerClass(String serializerClass) {
    this.serializerClass = serializerClass;
  }

  public void setExportBeans(boolean exportBeans) {
    this.exportBeans = exportBeans;
  }

  public void setExportTables(boolean exportTables) {
    this.exportTables = exportTables;
  }

  public void setExportViews(boolean exportViews) {
    this.exportViews = exportViews;
  }

  public void setInnerClassesForKeys(boolean innerClassesForKeys) {
    this.innerClassesForKeys = innerClassesForKeys;
  }

  public void setValidationAnnotations(boolean validationAnnotations) {
    this.validationAnnotations = validationAnnotations;
  }

  public void setColumnAnnotations(boolean columnAnnotations) {
    this.columnAnnotations = columnAnnotations;
  }

  public void setCustomTypes(String[] customTypes) {
    this.customTypes = customTypes;
  }

  public void setCreateScalaSources(boolean createScalaSources) {
    this.createScalaSources = createScalaSources;
  }

  public void setSchemaToPackage(boolean schemaToPackage) {
    this.schemaToPackage = schemaToPackage;
  }

  public void setLowerCase(boolean lowerCase) {
    this.lowerCase = lowerCase;
  }

  public void setTypeMappings(TypeMapping[] typeMappings) {
    this.typeMappings = typeMappings;
  }

  public void setNumericMappings(NumericMapping[] numericMappings) {
    this.numericMappings = numericMappings;
  }

  public void setRenameMappings(RenameMapping[] renameMappings) {
    this.renameMappings = renameMappings;
  }

  public void setImports(String[] imports) {
    this.imports = imports;
  }

  public void setSkip(boolean skip) {
    this.skip = skip;
  }

  public void setGeneratedAnnotationClass(String generatedAnnotationClass) {
    this.generatedAnnotationClass = generatedAnnotationClass;
  }

  private static String emptyIfSetToBlank(String value) {
    boolean setToBlank = value == null || value.equalsIgnoreCase("BLANK");
    return setToBlank ? "" : value;
  }

  private static String processBlankValues(String value) {
    if (value == null) {
      return null;
    }
    return BLANK_VALUE_PATTERN.matcher(value).replaceAll(BLANK_VALUE_REPLACEMENT);
  }

  private static final Pattern BLANK_VALUE_PATTERN =
      Pattern.compile("(^|,)BLANK(,|$)", Pattern.CASE_INSENSITIVE);
  private static final String BLANK_VALUE_REPLACEMENT = "$1$2";

  @Override
  public String getNamePrefix() {
    return emptyIfSetToBlank(namePrefix);
  }

  @Override
  public String getNameSuffix() {
    return StringUtils.nullToEmpty(nameSuffix);
  }

  @Override
  public String getBeanPrefix() {
    return StringUtils.nullToEmpty(beanPrefix);
  }

  @Override
  public String getBeanSuffix() {
    return StringUtils.nullToEmpty(beanSuffix);
  }

  @Override
  public File getBeansTargetFolder() {
    if (beansTargetFolder != null) {
      return new File(beansTargetFolder);
    }
    return null;
  }

  @Override
  public File getTargetFolder() {
    return new File(targetFolder);
  }

  @Override
  public boolean isCreateScalaSources() {
    return createScalaSources;
  }

  @Override
  public String getPackageName() {
    return packageName;
  }

  @Override
  public String getBeanPackageName() {
    return beanPackageName;
  }

  @Override
  public boolean isInnerClassesForKeys() {
    return innerClassesForKeys;
  }

  @Override
  public Class<? extends NamingStrategy> getNamingStrategyClass() {
    if (namingStrategyClass == null) {
      return null;
    }
    try {
      return (Class<? extends NamingStrategy>) Class.forName(namingStrategyClass);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getSchemaPattern() {
    return processBlankValues(schemaPattern);
  }

  @Override
  public String getCatalogPattern() {
    return catalogPattern;
  }

  @Override
  public String getTableNamePattern() {
    return tableNamePattern;
  }

  @Override
  public boolean isColumnAnnotations() {
    return columnAnnotations;
  }

  @Override
  public boolean isValidationAnnotations() {
    return validationAnnotations;
  }

  @Override
  public boolean isSchemaToPackage() {
    return schemaToPackage;
  }

  @Override
  public boolean isLowerCase() {
    return lowerCase;
  }

  @Override
  public boolean isExportTables() {
    return exportTables;
  }

  @Override
  public boolean isExportViews() {
    return exportViews;
  }

  @Override
  public boolean isExportAll() {
    return exportAll;
  }

  @Override
  public boolean isExportPrimaryKeys() {
    return exportPrimaryKeys;
  }

  @Override
  public boolean isExportForeignKeys() {
    return exportForeignKeys;
  }

  @Override
  public boolean isExportDirectForeignKeys() {
    return exportDirectForeignKeys;
  }

  @Override
  public boolean isExportInverseForeignKeys() {
    return exportInverseForeignKeys;
  }

  @Override
  public Charset getSourceEncoding() {
    String sourceEncoding = (String) project.getProperties().get("project.build.sourceEncoding");
    if (sourceEncoding != null) {
      return Charset.forName(sourceEncoding);
    }

    return StandardCharsets.UTF_8;
  }

  @Override
  public String getTableTypesToExport() {
    return tableTypesToExport;
  }

  @Override
  public List<String> getImports() {
    if (imports == null) {
      return null;
    }
    return Arrays.asList(imports);
  }

  @Override
  public String getGeneratedAnnotationClass() {
    return generatedAnnotationClass;
  }

  @Override
  public Class<? extends BeanSerializer> getBeanSerializerClass() {
    if (exportBeans && beanSerializerClass != null) {
      try {
        return (Class<? extends BeanSerializer>) Class.forName(beanSerializerClass);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }

  @Override
  public boolean isExportBeans() {
    return exportBeans;
  }

  @Override
  public String[] getBeanInterfaces() {
    return beanInterfaces;
  }

  @Override
  public boolean isBeanAddToString() {
    return beanAddToString;
  }

  @Override
  public boolean isBeanAddFullConstructor() {
    return beanAddFullConstructor;
  }

  @Override
  public boolean isBeanPrintSupertype() {
    return beanPrintSupertype;
  }

  @Override
  public List<CustomType> getCustomTypes() {
    if (customTypes == null) {
      return null;
    }
    return Arrays.stream(customTypes).map(CustomType::new).collect(Collectors.toList());
  }

  @Override
  public List<TypeMapping> getTypeMappings() {
    if (typeMappings == null) {
      return null;
    }
    return Arrays.stream(typeMappings).collect(Collectors.toList());
  }

  @Override
  public List<NumericMapping> getNumericMappings() {
    if (numericMappings == null) {
      return null;
    }
    return Arrays.stream(numericMappings).collect(Collectors.toList());
  }

  @Override
  public List<RenameMapping> getRenameMappings() {
    if (renameMappings == null) {
      return null;
    }
    return Arrays.stream(renameMappings).collect(Collectors.toList());
  }

  @Override
  public Class<? extends Comparator<Property>> getColumnComparatorClass() {
    if (columnComparatorClass == null) {
      return null;
    }
    try {
      return (Class<? extends Comparator<Property>>) Class.forName(columnComparatorClass);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Class<? extends Serializer> getSerializerClass() {
    if (serializerClass == null) {
      return null;
    }
    try {
      return (Class<? extends Serializer>) Class.forName(serializerClass);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
