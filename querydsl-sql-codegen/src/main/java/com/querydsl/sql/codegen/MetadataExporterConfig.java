package com.querydsl.sql.codegen;

import com.querydsl.codegen.BeanSerializer;
import com.querydsl.codegen.Property;
import com.querydsl.codegen.Serializer;
import com.querydsl.sql.SchemaAndTable;
import com.querydsl.sql.codegen.support.CustomType;
import com.querydsl.sql.codegen.support.NumericMapping;
import com.querydsl.sql.codegen.support.RenameMapping;
import com.querydsl.sql.codegen.support.TypeMapping;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.List;

public interface MetadataExporterConfig {

  /**
   * Override the name prefix for the classes (default: Q)
   *
   * @return name prefix for querydsl-types (default: Q)
   */
  String getNamePrefix();

  /**
   * Override the name suffix for the classes (default: "")
   *
   * @return name suffix for querydsl-types (default: "")
   */
  String getNameSuffix();

  /**
   * Override the bean prefix for the classes (default: "")
   *
   * @return bean prefix for bean-types (default: "")
   */
  String getBeanPrefix();

  /**
   * Override the bean suffix for the classes (default: "")
   *
   * @return bean suffix for bean-types (default: "")
   */
  String getBeanSuffix();

  /**
   * Set the target folder for beans
   *
   * <p>defaults to the targetFolder value
   *
   * @return target source folder to create the bean sources into
   */
  File getBeansTargetFolder();

  /**
   * Set the target folder
   *
   * @return target source folder to create the sources into (e.g. target/generated-sources/java)
   */
  File getTargetFolder();

  /**
   * Set true to create Scala sources instead of Java sources
   *
   * @return whether to create Scala sources (default: false)
   */
  boolean isCreateScalaSources();

  /**
   * Set the package name
   *
   * @return package name for sources
   */
  String getPackageName();

  /**
   * Override the bean package name (default: packageName)
   *
   * @return package name for bean sources
   */
  String getBeanPackageName();

  /**
   * Set whether inner classes should be created for keys
   *
   * @return whether inner classes should be created for keys
   */
  boolean isInnerClassesForKeys();

  /**
   * Override the NamingStrategy (default: new DefaultNamingStrategy())
   *
   * @return naming strategy to override (default: new DefaultNamingStrategy())
   */
  Class<? extends NamingStrategy> getNamingStrategyClass();

  /**
   * Set the schema pattern filter to be used
   *
   * @return a schema name pattern; must match the schema name as it is stored in the database; ""
   *     retrieves those without a schema; {@code null} means that the schema name should not be
   *     used to narrow the search (default: null)
   */
  String getSchemaPattern();

  /**
   * @return a catalog name; must match the catalog name as it is stored in the database; ""
   *     retrieves those without a catalog; <code>null</code> means that the catalog name should not
   *     be used to narrow the search
   */
  String getCatalogPattern();

  /**
   * Set the table name pattern filter to be used
   *
   * @return a table name pattern; must match the table name as it is stored in the database
   *     (default: null)
   */
  String getTableNamePattern();

  /**
   * @return whether column annotations should be created
   */
  boolean isColumnAnnotations();

  /**
   * @return whether validation annotations should be created
   */
  boolean isValidationAnnotations();

  /**
   * <b>!!! Important !!!</b><i> {@link NamingStrategy#getPackage(String, SchemaAndTable)} will be
   * invoked only if <code>schemaToPackage</code> is set to <code>true</code>.</i>
   *
   * @deprecated This flag will not be necessary in the future because the generated package name
   *     can be controlled in method {@link NamingStrategy#getPackage(String, SchemaAndTable)}.
   * @return whether schema names should be appended to the package name.
   */
  @Deprecated
  boolean isSchemaToPackage();

  /**
   * @return whether names should be normalized to lowercase
   */
  boolean isLowerCase();

  /**
   * @return whether tables should be exported
   */
  boolean isExportTables();

  /**
   * @return whether views should be exported
   */
  boolean isExportViews();

  /**
   * @return whether all table types should be exported
   */
  boolean isExportAll();

  /**
   * @return whether serialize beans as well
   */
  boolean isExportBeans();

  /**
   * @return whether primary keys should be exported
   */
  boolean isExportPrimaryKeys();

  /**
   * @return whether foreign keys should be exported
   */
  boolean isExportForeignKeys();

  /**
   * @return whether direct foreign keys should be exported
   */
  boolean isExportDirectForeignKeys();

  /**
   * @return whether inverse foreign keys should be exported
   */
  boolean isExportInverseForeignKeys();

  /**
   * @return the source encoding
   */
  Charset getSourceEncoding();

  /**
   * @return the table types to export as a comma separated string
   */
  String getTableTypesToExport();

  /**
   * Set the java imports
   *
   * @return java imports array
   */
  List<String> getImports();

  /**
   * Set the fully qualified class name of the "generated" annotation added ot the generated sources
   *
   * @return the fully qualified class name of the <em>Single-Element Annotation</em> (with {@code
   *     String} element) to be used on the generated sources, or {@code null} (defaulting to {@code
   *     javax.annotation.Generated} or {@code javax.annotation.processing.Generated} depending on
   *     the java version).
   * @see <a
   *     href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.7.3">Single-Element
   *     Annotation</a>
   */
  String getGeneratedAnnotationClass();

  /**
   * Set the Bean serializer class to create bean types as well
   *
   * @return serializer for JavaBeans (default: null)
   */
  Class<? extends BeanSerializer> getBeanSerializerClass();

  /**
   * @return additional interfaces to be implemented by beans
   */
  String[] getBeanInterfaces();

  /**
   * @return switch for {@code toString} addition
   */
  boolean isBeanAddToString();

  /**
   * @return switch for full constructor addition
   */
  boolean isBeanAddFullConstructor();

  /**
   * @return switch to print supertype content
   */
  boolean isBeanPrintSupertype();

  /**
   * @return custom type classnames to use
   */
  List<CustomType> getCustomTypes();

  /**
   * @return custom type mappings to use
   */
  List<TypeMapping> getTypeMappings();

  /**
   * @return custom numeric mappings
   */
  List<NumericMapping> getNumericMappings();

  /**
   * @return custom rename mappings
   */
  List<RenameMapping> getRenameMappings();

  /**
   * @return the column comparator class
   */
  Class<? extends Comparator<Property>> getColumnComparatorClass();

  /**
   * @return the serializer class
   */
  Class<? extends Serializer> getSerializerClass();
}
