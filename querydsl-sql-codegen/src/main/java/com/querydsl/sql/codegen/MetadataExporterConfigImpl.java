package com.querydsl.sql.codegen;

import com.querydsl.codegen.BeanSerializer;
import com.querydsl.codegen.Property;
import com.querydsl.codegen.Serializer;
import com.querydsl.sql.codegen.support.CustomType;
import com.querydsl.sql.codegen.support.NumericMapping;
import com.querydsl.sql.codegen.support.RenameMapping;
import com.querydsl.sql.codegen.support.TypeMapping;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

public class MetadataExporterConfigImpl implements MetadataExporterConfig {

  private String namePrefix;

  private String nameSuffix;

  private String beanPrefix;
  private String beanSuffix;

  private File beansTargetFolder;
  private File targetFolder;
  private boolean createScalaSources;
  private String packageName;
  private String beanPackageName;
  private boolean innerClassesForKeys;
  private Class<? extends NamingStrategy> namingStrategyClass = DefaultNamingStrategy.class;

  private String schemaPattern;
  private String catalogPattern;
  private String tableNamePattern;

  private boolean columnAnnotations;

  private boolean validationAnnotations;

  private boolean schemaToPackage;

  private boolean lowerCase;

  private boolean exportTables = true;

  private boolean exportViews = true;

  private boolean exportAll;

  private boolean exportBeans;

  private boolean exportPrimaryKeys = true;

  private boolean exportForeignKeys = true;

  private boolean exportDirectForeignKeys = true;

  private boolean exportInverseForeignKeys = true;

  private Charset sourceEncoding = StandardCharsets.UTF_8;

  private String tableTypesToExport;

  private List<String> imports;

  private String generatedAnnotationClass;

  private Class<? extends BeanSerializer> beanSerializerClass;

  private String[] beanInterfaces;

  private boolean beanAddToString;

  private boolean beanAddFullConstructor;

  private boolean beanPrintSupertype;

  private List<CustomType> customTypes;

  private List<TypeMapping> typeMappings;

  private List<NumericMapping> numericMappings;

  private List<RenameMapping> renameMappings;

  private Class<? extends Comparator<Property>> columnComparatorClass;

  private Class<? extends Serializer> serializerClass;

  public String getNamePrefix() {
    return namePrefix;
  }

  public void setNamePrefix(String namePrefix) {
    this.namePrefix = namePrefix;
  }

  public String getNameSuffix() {
    return nameSuffix;
  }

  public void setNameSuffix(String nameSuffix) {
    this.nameSuffix = nameSuffix;
  }

  public String getBeanPrefix() {
    return beanPrefix;
  }

  public void setBeanPrefix(String beanPrefix) {
    this.beanPrefix = beanPrefix;
  }

  public String getBeanSuffix() {
    return beanSuffix;
  }

  public void setBeanSuffix(String beanSuffix) {
    this.beanSuffix = beanSuffix;
  }

  public File getBeansTargetFolder() {
    return beansTargetFolder;
  }

  public void setBeansTargetFolder(File beansTarFolder) {
    this.beansTargetFolder = beansTarFolder;
  }

  public File getTargetFolder() {
    return targetFolder;
  }

  public void setTargetFolder(File tarFolder) {
    this.targetFolder = tarFolder;
  }

  public boolean isCreateScalaSources() {
    return createScalaSources;
  }

  public void setCreateScalaSources(boolean createScalaSources) {
    this.createScalaSources = createScalaSources;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getBeanPackageName() {
    return beanPackageName;
  }

  public void setBeanPackageName(String beanPackageName) {
    this.beanPackageName = beanPackageName;
  }

  public boolean isInnerClassesForKeys() {
    return innerClassesForKeys;
  }

  public void setInnerClassesForKeys(boolean innerClassesForKeys) {
    this.innerClassesForKeys = innerClassesForKeys;
  }

  public Class<? extends NamingStrategy> getNamingStrategyClass() {
    return namingStrategyClass;
  }

  public void setNamingStrategyClass(Class<? extends NamingStrategy> namingStrategyClass) {
    this.namingStrategyClass = namingStrategyClass;
  }

  public String getSchemaPattern() {
    return schemaPattern;
  }

  public void setSchemaPattern(String schemaPattern) {
    this.schemaPattern = schemaPattern;
  }

  public String getCatalogPattern() {
    return catalogPattern;
  }

  public void setCatalogPattern(String catalogPattern) {
    this.catalogPattern = catalogPattern;
  }

  public String getTableNamePattern() {
    return tableNamePattern;
  }

  public void setTableNamePattern(String tableNamePattern) {
    this.tableNamePattern = tableNamePattern;
  }

  public boolean isColumnAnnotations() {
    return columnAnnotations;
  }

  public void setColumnAnnotations(boolean columnAnnotations) {
    this.columnAnnotations = columnAnnotations;
  }

  public boolean isValidationAnnotations() {
    return validationAnnotations;
  }

  public void setValidationAnnotations(boolean validationAnnotations) {
    this.validationAnnotations = validationAnnotations;
  }

  public boolean isSchemaToPackage() {
    return schemaToPackage;
  }

  public void setSchemaToPackage(boolean schemaToPackage) {
    this.schemaToPackage = schemaToPackage;
  }

  public boolean isLowerCase() {
    return lowerCase;
  }

  public void setLowerCase(boolean lowerCase) {
    this.lowerCase = lowerCase;
  }

  public boolean isExportTables() {
    return exportTables;
  }

  public void setExportTables(boolean exportTables) {
    this.exportTables = exportTables;
  }

  public boolean isExportViews() {
    return exportViews;
  }

  public void setExportViews(boolean exportViews) {
    this.exportViews = exportViews;
  }

  public boolean isExportAll() {
    return exportAll;
  }

  public void setExportAll(boolean exportAll) {
    this.exportAll = exportAll;
  }

  public boolean isExportBeans() {
    return exportBeans;
  }

  public void setExportBeans(boolean exportBeans) {
    this.exportBeans = exportBeans;
  }

  public boolean isExportPrimaryKeys() {
    return exportPrimaryKeys;
  }

  public void setExportPrimaryKeys(boolean exportPrimaryKeys) {
    this.exportPrimaryKeys = exportPrimaryKeys;
  }

  public boolean isExportForeignKeys() {
    return exportForeignKeys;
  }

  public void setExportForeignKeys(boolean exportForeignKeys) {
    this.exportForeignKeys = exportForeignKeys;
  }

  public boolean isExportDirectForeignKeys() {
    return exportDirectForeignKeys;
  }

  public void setExportDirectForeignKeys(boolean exportDirectForeignKeys) {
    this.exportDirectForeignKeys = exportDirectForeignKeys;
  }

  public boolean isExportInverseForeignKeys() {
    return exportInverseForeignKeys;
  }

  public void setExportInverseForeignKeys(boolean exportInverseForeignKeys) {
    this.exportInverseForeignKeys = exportInverseForeignKeys;
  }

  public Charset getSourceEncoding() {
    return sourceEncoding;
  }

  public void setSourceEncoding(Charset sourceEncoding) {
    this.sourceEncoding = sourceEncoding;
  }

  public String getTableTypesToExport() {
    return tableTypesToExport;
  }

  public void setTableTypesToExport(String tableTypesToExport) {
    this.tableTypesToExport = tableTypesToExport;
  }

  public List<String> getImports() {
    return imports;
  }

  public void setImports(List<String> imports) {
    this.imports = imports;
  }

  public String getGeneratedAnnotationClass() {
    return generatedAnnotationClass;
  }

  public void setGeneratedAnnotationClass(String generatedAnnotationClass) {
    this.generatedAnnotationClass = generatedAnnotationClass;
  }

  public Class<? extends BeanSerializer> getBeanSerializerClass() {
    return beanSerializerClass;
  }

  public void setBeanSerializerClass(Class<? extends BeanSerializer> beanSerializerClass) {
    this.beanSerializerClass = beanSerializerClass;
  }

  public String[] getBeanInterfaces() {
    return beanInterfaces;
  }

  public void setBeanInterfaces(String[] beanInterfaces) {
    this.beanInterfaces = beanInterfaces;
  }

  public boolean isBeanAddToString() {
    return beanAddToString;
  }

  public void setBeanAddToString(boolean beanAddToString) {
    this.beanAddToString = beanAddToString;
  }

  public boolean isBeanAddFullConstructor() {
    return beanAddFullConstructor;
  }

  public void setBeanAddFullConstructor(boolean beanAddFullConstructor) {
    this.beanAddFullConstructor = beanAddFullConstructor;
  }

  public boolean isBeanPrintSupertype() {
    return beanPrintSupertype;
  }

  public void setBeanPrintSupertype(boolean beanPrintSupertype) {
    this.beanPrintSupertype = beanPrintSupertype;
  }

  public List<CustomType> getCustomTypes() {
    return customTypes;
  }

  public void setCustomTypes(List<CustomType> customTypes) {
    this.customTypes = customTypes;
  }

  public List<TypeMapping> getTypeMappings() {
    return typeMappings;
  }

  public void setTypeMappings(List<TypeMapping> typeMappings) {
    this.typeMappings = typeMappings;
  }

  public List<NumericMapping> getNumericMappings() {
    return numericMappings;
  }

  public void setNumericMappings(List<NumericMapping> numericMappings) {
    this.numericMappings = numericMappings;
  }

  public List<RenameMapping> getRenameMappings() {
    return renameMappings;
  }

  public void setRenameMappings(List<RenameMapping> renameMappings) {
    this.renameMappings = renameMappings;
  }

  public Class<? extends Comparator<Property>> getColumnComparatorClass() {
    return columnComparatorClass;
  }

  public void setColumnComparatorClass(
      Class<? extends Comparator<Property>> columnComparatorClass) {
    this.columnComparatorClass = columnComparatorClass;
  }

  public Class<? extends Serializer> getSerializerClass() {
    return serializerClass;
  }

  public void setSerializerClass(Class<? extends Serializer> serializerClass) {
    this.serializerClass = serializerClass;
  }
}
