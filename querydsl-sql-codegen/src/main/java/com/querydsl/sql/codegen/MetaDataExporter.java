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
package com.querydsl.sql.codegen;

import com.querydsl.codegen.BeanSerializer;
import com.querydsl.codegen.CodegenModule;
import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.GeneratedAnnotationResolver;
import com.querydsl.codegen.Property;
import com.querydsl.codegen.QueryTypeFactory;
import com.querydsl.codegen.Serializer;
import com.querydsl.codegen.SimpleSerializerConfig;
import com.querydsl.codegen.TypeMappings;
import com.querydsl.codegen.utils.CodeWriter;
import com.querydsl.codegen.utils.JavaWriter;
import com.querydsl.codegen.utils.ScalaWriter;
import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import com.querydsl.sql.ColumnImpl;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.SQLTemplatesRegistry;
import com.querydsl.sql.SchemaAndTable;
import com.querydsl.sql.codegen.support.CustomType;
import com.querydsl.sql.codegen.support.ForeignKeyData;
import com.querydsl.sql.codegen.support.InverseForeignKeyData;
import com.querydsl.sql.codegen.support.NotNullImpl;
import com.querydsl.sql.codegen.support.NumericMapping;
import com.querydsl.sql.codegen.support.PrimaryKeyData;
import com.querydsl.sql.codegen.support.RenameMapping;
import com.querydsl.sql.codegen.support.SizeImpl;
import com.querydsl.sql.codegen.support.TypeMapping;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import org.jetbrains.annotations.Nullable;

/**
 * {@code MetadataExporter} exports JDBC metadata to Querydsl query types
 *
 * <p>Example
 *
 * <pre>
 * MetaDataExporter exporter = new MetaDataExporter();
 * exporter.setPackageName("com.example.domain");
 * exporter.setTargetFolder(new File("target/generated-sources/java"));
 * exporter.export(connection.getMetaData());
 * </pre>
 *
 * @author tiwe
 */
public class MetaDataExporter {

  private static final Logger logger = Logger.getLogger(MetaDataExporter.class.getName());

  private final SQLTemplatesRegistry sqlTemplatesRegistry = new SQLTemplatesRegistry();

  private final SQLCodegenModule module = new SQLCodegenModule();

  private final Set<String> classes = new HashSet<String>();

  @Nullable private String beanPackageName;

  private final Map<EntityType, Type> entityToWrapped = new HashMap<EntityType, Type>();

  private Serializer serializer;

  private QueryTypeFactory queryTypeFactory;

  private NamingStrategy namingStrategy;

  private Configuration configuration;

  private KeyDataFactory keyDataFactory;

  private final MetadataExporterConfig config;

  private Serializer beanSerializer;

  private TypeMappings typeMappings;

  public MetaDataExporter(MetadataExporterConfig config) {
    this.config = config;
  }

  protected EntityType createEntityType(SchemaAndTable schemaAndTable, final String className) {
    EntityType classModel;

    if (beanSerializer == null) {
      String packageName = normalizePackage(module.getPackageName(), schemaAndTable);
      String simpleName = module.getPrefix() + className + module.getSuffix();
      Type classTypeModel =
          new SimpleType(
              TypeCategory.ENTITY,
              packageName + "." + simpleName,
              packageName,
              simpleName,
              false,
              false);
      classModel =
          new EntityType(
              classTypeModel,
              module.get(Function.class, CodegenModule.VARIABLE_NAME_FUNCTION_CLASS));
      typeMappings.register(classModel, classModel);

    } else {
      String beanPackage = normalizePackage(beanPackageName, schemaAndTable);
      String simpleName = module.getBeanPrefix() + className + module.getBeanSuffix();
      Type classTypeModel =
          new SimpleType(
              TypeCategory.ENTITY,
              beanPackage + "." + simpleName,
              beanPackage,
              simpleName,
              false,
              false);
      classModel =
          new EntityType(
              classTypeModel,
              module.get(Function.class, CodegenModule.VARIABLE_NAME_FUNCTION_CLASS));

      Type mappedType = queryTypeFactory.create(classModel);
      entityToWrapped.put(classModel, mappedType);
      typeMappings.register(classModel, mappedType);
    }

    classModel.getData().put("schema", schemaAndTable.getSchema());
    classModel.getData().put("table", schemaAndTable.getTable());
    return classModel;
  }

  private String normalizePackage(String packageName, SchemaAndTable schemaAndTable) {
    String rval = packageName;
    if (config.isSchemaToPackage()) {
      rval = namingStrategy.getPackage(rval, schemaAndTable);
    }
    return rval;
  }

  protected Property createProperty(
      EntityType classModel, String normalizedColumnName, String propertyName, Type typeModel) {
    return new Property(
        classModel, propertyName, propertyName, typeModel, Collections.<String>emptyList(), false);
  }

  /**
   * Export the tables based on the given database metadata
   *
   * @param md database metadata
   * @throws SQLException
   */
  public void export(DatabaseMetaData md) throws SQLException {
    configuration = module.get(Configuration.class);
    configureModule();

    if (config.getNamingStrategyClass() != null) {
      try {
        namingStrategy = config.getNamingStrategyClass().newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    } else {
      namingStrategy = new DefaultNamingStrategy();
    }
    module.bind(NamingStrategy.class, namingStrategy);

    if (config.getBeanPackageName() == null) {
      beanPackageName = module.getPackageName();
    } else {
      beanPackageName = config.getBeanPackageName();
    }

    module.bind(SQLCodegenModule.BEAN_PACKAGE_NAME, beanPackageName);
    module.loadExtensions();

    classes.clear();
    typeMappings = module.get(TypeMappings.class);
    queryTypeFactory = module.get(QueryTypeFactory.class);
    serializer = module.get(Serializer.class);
    beanSerializer = module.get(Serializer.class, SQLCodegenModule.BEAN_SERIALIZER);
    namingStrategy = module.get(NamingStrategy.class);

    SQLTemplates templates = sqlTemplatesRegistry.getTemplates(md);
    if (templates != null) {
      configuration.setTemplates(templates);
    } else {
      logger.info("Found no specific dialect for " + md.getDatabaseProductName());
    }

    if (beanSerializer == null) {
      keyDataFactory =
          new KeyDataFactory(
              namingStrategy,
              module.getPackageName(),
              module.getPrefix(),
              module.getSuffix(),
              config.isSchemaToPackage());
    } else {
      keyDataFactory =
          new KeyDataFactory(
              namingStrategy,
              beanPackageName,
              module.getBeanPrefix(),
              module.getBeanSuffix(),
              config.isSchemaToPackage());
    }

    String[] typesArray = null;

    if (config.getTableTypesToExport() != null && !config.getTableTypesToExport().isEmpty()) {
      List<String> types = new ArrayList<String>();
      for (String tableType : config.getTableTypesToExport().split(",")) {
        types.add(tableType.trim());
      }
      typesArray = types.toArray(new String[0]);
    } else if (!config.isExportAll()) {
      List<String> types = new ArrayList<String>(2);
      if (config.isExportTables()) {
        types.add("TABLE");
      }
      if (config.isExportViews()) {
        types.add("VIEW");
      }
      typesArray = types.toArray(new String[0]);
    }

    List<String> catalogs = patternAsList(config.getCatalogPattern());
    List<String> schemas = patternAsList(config.getSchemaPattern());
    List<String> tables = patternAsList(config.getTableNamePattern());

    for (String catalog : catalogs) {
      catalog = trimIfNonNull(catalog);
      for (String schema : schemas) {
        schema = trimIfNonNull(schema);
        for (String table : tables) {
          table = trimIfNonNull(table);
          handleTables(md, catalog, schema, table, typesArray);
        }
      }
    }
  }

  private void configureModule() {
    if (config.getNamePrefix() != null) module.bind(CodegenModule.PREFIX, config.getNamePrefix());

    if (config.getNameSuffix() != null) {
      module.bind(CodegenModule.SUFFIX, config.getNameSuffix());
    }

    if (config.getBeanPrefix() != null) {
      module.bind(SQLCodegenModule.BEAN_PREFIX, config.getBeanPrefix());
    }

    if (config.getBeanSuffix() != null) {
      module.bind(SQLCodegenModule.BEAN_SUFFIX, config.getBeanSuffix());
    }

    module.bind(SQLCodegenModule.PACKAGE_NAME, config.getPackageName());

    module.bind(SQLCodegenModule.INNER_CLASSES_FOR_KEYS, config.isInnerClassesForKeys());

    module.bind(NamingStrategy.class, namingStrategy);

    module.bind(SQLCodegenModule.SCHEMA_TO_PACKAGE, config.isSchemaToPackage());

    if (config.getImports() != null && !config.getImports().isEmpty())
      module.bind(CodegenModule.IMPORTS, new HashSet<String>(config.getImports()));

    module.bindInstance(
        CodegenModule.GENERATED_ANNOTATION_CLASS,
        GeneratedAnnotationResolver.resolve(config.getGeneratedAnnotationClass()));

    if (config.isExportBeans()) {
      BeanSerializer serializer;
      if (config.getBeanSerializerClass() == null) {
        serializer = new BeanSerializer();
      } else {
        try {
          serializer = (BeanSerializer) config.getBeanSerializerClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
      if (config.getBeanInterfaces() != null) {
        for (String iface : config.getBeanInterfaces()) {
          int sepIndex = iface.lastIndexOf('.');
          if (sepIndex < 0) {
            serializer.addInterface(new SimpleType(iface));
          } else {
            String packageName = iface.substring(0, sepIndex);
            String simpleName = iface.substring(sepIndex + 1);
            serializer.addInterface(new SimpleType(iface, packageName, simpleName));
          }
        }
      }
      serializer.setAddFullConstructor(config.isBeanAddFullConstructor());
      serializer.setAddToString(config.isBeanAddToString());
      serializer.setPrintSupertype(config.isBeanPrintSupertype());
      module.bind(SQLCodegenModule.BEAN_SERIALIZER, serializer);
    }

    if (config.getCustomTypes() != null) {
      for (CustomType cl : config.getCustomTypes()) {
        try {
          configuration.register(
              (com.querydsl.sql.types.Type) Class.forName(cl.getClassName()).newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      }
    }
    if (config.getTypeMappings() != null) {
      for (TypeMapping mapping : config.getTypeMappings()) {
        mapping.apply(configuration);
      }
    }
    if (config.getNumericMappings() != null) {
      for (NumericMapping mapping : config.getNumericMappings()) {
        mapping.apply(configuration);
      }
    }
    if (config.getRenameMappings() != null) {
      for (RenameMapping mapping : config.getRenameMappings()) {
        mapping.apply(configuration);
      }
    }

    if (config.getColumnComparatorClass() != null)
      module.bind(
          SQLCodegenModule.COLUMN_COMPARATOR,
          config.getColumnComparatorClass().asSubclass(Comparator.class));

    if (config.getSerializerClass() != null) {
      module.bind(Serializer.class, config.getSerializerClass());
    }
  }

  private String trimIfNonNull(String input) {
    return input != null ? input.trim() : null;
  }

  /**
   * Splits the input on ',' if non-null and a ',' is present. Returns a singletonList of null if
   * null
   */
  private List<String> patternAsList(@Nullable String input) {
    if (input != null && input.contains(",")) {
      return Arrays.asList(input.split(","));
    } else {
      return Collections.singletonList(input);
    }
  }

  private void handleTables(
      DatabaseMetaData md,
      String catalogPattern,
      String schemaPattern,
      String tablePattern,
      String[] types)
      throws SQLException {
    try (ResultSet tables = md.getTables(catalogPattern, schemaPattern, tablePattern, types)) {
      while (tables.next()) {
        handleTable(md, tables);
      }
    }
  }

  Set<String> getClasses() {
    return classes;
  }

  private void handleColumn(EntityType classModel, String tableName, ResultSet columns)
      throws SQLException {
    String columnName = normalize(columns.getString("COLUMN_NAME"));
    String normalizedColumnName = namingStrategy.normalizeColumnName(columnName);
    int columnType = columns.getInt("DATA_TYPE");
    String typeName = columns.getString("TYPE_NAME");
    Number columnSize = (Number) columns.getObject("COLUMN_SIZE");
    Number columnDigits = (Number) columns.getObject("DECIMAL_DIGITS");
    int columnIndex = columns.getInt("ORDINAL_POSITION");
    int nullable = columns.getInt("NULLABLE");
    String columnDefaultValue = columns.getString("COLUMN_DEF");

    String propertyName = namingStrategy.getPropertyName(normalizedColumnName, classModel);
    Class<?> clazz =
        configuration.getJavaType(
            columnType,
            typeName,
            columnSize != null ? columnSize.intValue() : 0,
            columnDigits != null ? columnDigits.intValue() : 0,
            tableName,
            columnName);
    if (clazz == null) {
      clazz = Object.class;
    }
    TypeCategory fieldType = TypeCategory.get(clazz.getName());
    if (Number.class.isAssignableFrom(clazz)) {
      fieldType = TypeCategory.NUMERIC;
    } else if (Enum.class.isAssignableFrom(clazz)) {
      fieldType = TypeCategory.ENUM;
    }
    Type typeModel = new ClassType(fieldType, clazz);
    Property property = createProperty(classModel, normalizedColumnName, propertyName, typeModel);
    ColumnMetadata column =
        ColumnMetadata.named(normalizedColumnName).ofType(columnType).withIndex(columnIndex);
    if (nullable == DatabaseMetaData.columnNoNulls) {
      column = column.notNull();
    }
    if (columnSize != null) {
      column = column.withSize(columnSize.intValue());
    }
    if (columnDigits != null) {
      column = column.withDigits(columnDigits.intValue());
    }
    property.getData().put("COLUMN", column);

    if (config.isColumnAnnotations()) {
      property.addAnnotation(new ColumnImpl(normalizedColumnName));
    }
    if (config.isValidationAnnotations()) {
      if (nullable == DatabaseMetaData.columnNoNulls && columnDefaultValue == null) {
        property.addAnnotation(new NotNullImpl());
      }
      int size = columns.getInt("COLUMN_SIZE");
      if (size > 0 && clazz.equals(String.class)) {
        property.addAnnotation(new SizeImpl(0, size));
      }
    }
    classModel.addProperty(property);
  }

  private void handleTable(DatabaseMetaData md, ResultSet tables) throws SQLException {
    String catalog = tables.getString("TABLE_CAT");
    String schema = tables.getString("TABLE_SCHEM");
    String schemaName = normalize(tables.getString("TABLE_SCHEM"));
    String tableName = normalize(tables.getString("TABLE_NAME"));

    String normalizedSchemaName = namingStrategy.normalizeSchemaName(schemaName);
    String normalizedTableName = namingStrategy.normalizeTableName(tableName);

    SchemaAndTable schemaAndTable = new SchemaAndTable(normalizedSchemaName, normalizedTableName);

    if (!namingStrategy.shouldGenerateClass(schemaAndTable)) {
      return;
    }

    String className = namingStrategy.getClassName(schemaAndTable);
    EntityType classModel = createEntityType(schemaAndTable, className);

    if (config.isExportPrimaryKeys()) {
      // collect primary keys
      Map<String, PrimaryKeyData> primaryKeyData =
          keyDataFactory.getPrimaryKeys(md, catalog, schema, tableName);
      if (!primaryKeyData.isEmpty()) {
        classModel.getData().put(PrimaryKeyData.class, primaryKeyData.values());
      }
    }

    if (config.isExportForeignKeys()) {
      if (config.isExportDirectForeignKeys()) {
        // collect foreign keys
        Map<String, ForeignKeyData> foreignKeyData =
            keyDataFactory.getImportedKeys(md, catalog, schema, tableName);
        if (!foreignKeyData.isEmpty()) {
          Collection<ForeignKeyData> foreignKeysToGenerate = new LinkedHashSet<ForeignKeyData>();
          for (ForeignKeyData fkd : foreignKeyData.values()) {
            if (namingStrategy.shouldGenerateForeignKey(schemaAndTable, fkd)) {
              foreignKeysToGenerate.add(fkd);
            }
          }

          if (!foreignKeysToGenerate.isEmpty()) {
            classModel.getData().put(ForeignKeyData.class, foreignKeysToGenerate);
          }
        }
      }

      if (config.isExportInverseForeignKeys()) {
        // collect inverse foreign keys
        Map<String, InverseForeignKeyData> inverseForeignKeyData =
            keyDataFactory.getExportedKeys(md, catalog, schema, tableName);
        if (!inverseForeignKeyData.isEmpty()) {
          classModel.getData().put(InverseForeignKeyData.class, inverseForeignKeyData.values());
        }
      }
    }

    // collect columns
    try (ResultSet columns = md.getColumns(catalog, schema, tableName.replace("/", "//"), null)) {
      while (columns.next()) {
        handleColumn(classModel, tableName, columns);
      }
    }

    // serialize model
    serialize(classModel, schemaAndTable);

    logger.info("Exported " + tableName + " successfully");
  }

  private String normalize(String str) {
    if (config.isLowerCase() && str != null) {
      return str.toLowerCase();
    } else {
      return str;
    }
  }

  private void serialize(EntityType type, SchemaAndTable schemaAndTable) {
    try {
      String fileSuffix = config.isCreateScalaSources() ? ".scala" : ".java";

      if (beanSerializer != null) {
        String packageName = normalizePackage(beanPackageName, schemaAndTable);
        String path = packageName.replace('.', '/') + "/" + type.getSimpleName() + fileSuffix;
        write(
            beanSerializer,
            new File(
                config.getBeansTargetFolder() != null
                    ? config.getBeansTargetFolder()
                    : config.getTargetFolder(),
                path),
            type);

        String otherPath = entityToWrapped.get(type).getFullName().replace('.', '/') + fileSuffix;
        write(serializer, new File(config.getTargetFolder(), otherPath), type);
      } else {
        String packageName = normalizePackage(module.getPackageName(), schemaAndTable);
        String path = packageName.replace('.', '/') + "/" + type.getSimpleName() + fileSuffix;
        write(serializer, new File(config.getTargetFolder(), path), type);
      }

    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private void write(Serializer serializer, File targetFile, EntityType type) throws IOException {
    if (!classes.add(targetFile.getPath())) {
      throw new IllegalStateException(
          "Attempted to write multiple times to "
              + targetFile.getPath()
              + ", please check your configuration");
    }
    StringWriter w = new StringWriter();
    CodeWriter writer = config.isCreateScalaSources() ? new ScalaWriter(w) : new JavaWriter(w);
    serializer.serialize(type, SimpleSerializerConfig.DEFAULT, writer);

    // conditional creation
    boolean generate = true;
    byte[] bytes = w.toString().getBytes(config.getSourceEncoding());
    if (targetFile.exists() && targetFile.length() == bytes.length) {
      String str = new String(Files.readAllBytes(targetFile.toPath()), config.getSourceEncoding());
      if (str.equals(w.toString())) {
        generate = false;
      }
    } else {
      targetFile.getParentFile().mkdirs();
    }

    if (generate) {
      Files.write(targetFile.toPath(), bytes);
    }
  }

  public void setConfiguration(Configuration configuration) {
    module.bind(Configuration.class, configuration);
  }
}
