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

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.sql.codegen.ExtendedBeanSerializer;
import com.querydsl.sql.codegen.OriginalNamingStrategy;
import com.querydsl.sql.codegen.support.NumericMapping;
import com.querydsl.sql.codegen.support.RenameMapping;
import com.querydsl.sql.codegen.support.TypeMapping;
import com.querydsl.sql.types.BytesType;
import com.querydsl.sql.types.LocalDateTimeType;
import com.querydsl.sql.types.LocalDateType;
import com.querydsl.sql.types.LocalTimeType;
import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class MetadataExportMojoTest {

  private final String url =
      "jdbc:h2:mem:testdb"
          + System.currentTimeMillis()
          + ";MODE=legacy;INIT="
          + "CREATE TABLE NO_SCHEMA_TABLE (COL1 INT)        \\;"
          + "CREATE SCHEMA SCHEMA1                          \\;"
          + "CREATE TABLE SCHEMA1.SCHEMA1_TABLE (COL1 INT)  \\;"
          + "CREATE SCHEMA SCHEMA2                          \\;"
          + "CREATE TABLE SCHEMA2.SCHEMA2_TABLE (COL1 INT)  \\;";

  private final MavenProject project = new MavenProject();

  private final MetadataExportMojo mojo = new MetadataExportMojo();

  @Rule public TestName testName = new TestName();

  @Before
  public void setUp() {
    mojo.setProject(project);
    mojo.setJdbcDriver("org.h2.Driver");
    mojo.setJdbcUrl(url);
    mojo.setJdbcUser("sa");
    mojo.setNamePrefix("Q"); // default value
    mojo.setPackageName("com.example");
    mojo.setExportTables(true); // default value
    mojo.setExportViews(true); // default value
  }

  @Test
  public void execute() throws Exception {
    var target = new File("target/export").getCanonicalFile();
    mojo.setTargetFolder(target.getAbsolutePath());
    mojo.execute();

    assertThat(project.getCompileSourceRoots())
        .isEqualTo(Collections.singletonList(target.getAbsolutePath()));
    assertThat(target).exists();
  }

  @Test
  public void execute_with_customTypes() throws Exception {
    var target = new File("target/export2").getCanonicalFile();
    mojo.setTargetFolder(target.getAbsolutePath());
    mojo.setCustomTypes(new String[] {BytesType.class.getName()});
    mojo.execute();

    assertThat(project.getCompileSourceRoots())
        .isEqualTo(Collections.singletonList(target.getAbsolutePath()));
    assertThat(target).exists();
  }

  @Test
  public void execute_with_jsr310Types() throws Exception {
    var target = new File("target/export3").getCanonicalFile();
    mojo.setTargetFolder(target.getAbsolutePath());
    mojo.setCustomTypes(
        new String[] {
          LocalDateType.class.getName(),
          LocalTimeType.class.getName(),
          LocalDateTimeType.class.getName()
        });

    mojo.execute();

    assertThat(project.getCompileSourceRoots())
        .isEqualTo(Collections.singletonList(target.getAbsolutePath()));
    assertThat(target).exists();
  }

  @Test
  public void execute_with_typeMappings() throws Exception {
    var target = new File("target/export4").getCanonicalFile();
    mojo.setTargetFolder(target.getAbsolutePath());
    var mapping = new TypeMapping();
    mapping.setTable("CATALOGS");
    mapping.setColumn("CATALOG_NAME");
    mapping.setType(Object.class.getName());
    mojo.setTypeMappings(new TypeMapping[] {mapping});

    mojo.execute();

    assertThat(project.getCompileSourceRoots())
        .isEqualTo(Collections.singletonList(target.getAbsolutePath()));
    assertThat(target).exists();
  }

  @Test
  public void executeWithNumericMappings() throws Exception {
    var target = new File("target/export5").getCanonicalFile();
    mojo.setTargetFolder(target.getAbsolutePath());
    var mapping = new NumericMapping();
    mapping.setTotal(1);
    mapping.setDecimal(1);
    mapping.setJavaType(Number.class.getName());
    mojo.setNumericMappings(new NumericMapping[] {mapping});

    mojo.execute();

    assertThat(project.getCompileSourceRoots())
        .isEqualTo(Collections.singletonList(target.getAbsolutePath()));
    assertThat(target).exists();
  }

  @Test
  public void executeWithBeans() throws Exception {
    mojo.setTargetFolder("target/export6");
    mojo.setExportBeans(true);
    mojo.execute();

    assertThat(new File("target/export6")).exists();
  }

  @Test
  @Ignore
  public void executeWithScalaSources() throws Exception {
    mojo.setTargetFolder("target/export7");
    mojo.setCreateScalaSources(true);
    mojo.execute();

    assertThat(new File("target/export7")).exists();
  }

  @Test
  public void executeWithNamingStrategy() throws Exception {
    mojo.setTargetFolder("target/export8");
    mojo.setNamingStrategyClass(OriginalNamingStrategy.class.getName());
    mojo.execute();

    assertThat(new File("target/export8")).exists();
  }

  @Test
  public void executeWithBeans2() throws Exception {
    mojo.setTargetFolder("target/export9");
    mojo.setExportBeans(true);
    mojo.setBeanSerializerClass(ExtendedBeanSerializer.class.getName());
    mojo.execute();

    assertThat(new File("target/export9")).exists();
  }

  @Test
  public void executeWithBeans3() throws Exception {
    mojo.setTargetFolder("target/export10");
    mojo.setExportBeans(true);
    mojo.setBeanInterfaces(new String[] {Serializable.class.getName()});
    mojo.execute();

    assertThat(new File("target/export10")).exists();
  }

  @Test
  public void executeWithImport1() throws Exception {
    mojo.setTargetFolder("target/export11");
    mojo.setImports(new String[] {"com.pck1", "com.pck2", "com.Q1", "com.Q2"});
    mojo.execute();

    assertThat(new File("target/export11")).exists();
  }

  @Test
  public void executeWithImportAndBeans1() throws Exception {
    mojo.setTargetFolder("target/export12");
    mojo.setImports(new String[] {"com.pck1", "com.pck2", "com.Q1", "com.Q2"});
    mojo.setExportBeans(true);
    mojo.execute();

    assertThat(new File("target/export12")).exists();
  }

  @Test
  public void executeWithRenames() throws Exception {
    var mapping = new RenameMapping();
    mapping.setFromSchema("ABC");
    mapping.setToSchema("DEF");

    var target = new File("target/export13").getCanonicalFile();
    mojo.setTargetFolder(target.getAbsolutePath());
    mojo.setRenameMappings(new RenameMapping[] {mapping});
    mojo.execute();

    assertThat(project.getCompileSourceRoots())
        .isEqualTo(Collections.singletonList(target.getAbsolutePath()));
    assertThat(target).exists();
  }

  // region Schema Pattern Matching

  @Test
  public void executeWithUnsetSchemaPattern() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern(null);
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java")).exists();
    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java")).exists();
    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java")).exists();
  }

  @Test
  public void executeWithExactSchemaPattern() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern("SCHEMA1");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java")).exists();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java").exists()).isFalse();
    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java").exists()).isFalse();
  }

  @Test
  public void executeWithSimilarSchemaPattern() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern("%EMA1");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java")).exists();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java").exists()).isFalse();
    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java").exists()).isFalse();
  }

  @Test
  public void executeWithMismatchedSchemaPattern() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern("NON_EXISTENT_SCHEMA");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java").exists()).isFalse();
    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java").exists()).isFalse();
    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java").exists()).isFalse();
  }

  @Test
  public void executeWithMultipleSchemaPatterns() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern("SCHEMA1,SCHEMA2");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java")).exists();
    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java")).exists();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java").exists()).isFalse();
  }

  // endregion Schema Pattern Matching

  // region Schema Pattern Matching - Empty Values

  @Test
  public void executeWithEmptySchemaPattern() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern("");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java")).exists();

    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java").exists()).isFalse();
    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java").exists()).isFalse();
  }

  @Test
  public void executeWithMultipleSchemaPatternsAndInterleavedEmpty() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern("SCHEMA1,,SCHEMA2");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java")).exists();
    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java")).exists();
    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java")).exists();
  }

  @Test
  public void executeWithMultipleSchemaPatternsAndLeadingEmpty() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern(",SCHEMA2");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java")).exists();
    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java")).exists();

    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java").exists()).isFalse();
  }

  @Test
  @Ignore("Trailing empty strings are not handled correctly by the MetaDataExporter")
  public void executeWithMultipleSchemaPatternsAndTrailingEmpty() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern("SCHEMA1,");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java")).exists();
    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java")).exists();

    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java").exists()).isFalse();
  }

  // endregion Schema Pattern Matching - Empty Values

  // region Schema Pattern Matching - BLANK Values

  @Test
  public void executeWithBlankUppercaseSchemaPattern() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern("BLANK");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java")).exists();

    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java").exists()).isFalse();
    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java").exists()).isFalse();
  }

  @Test
  public void executeWithBlankLowercaseSchemaPattern() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern("blank");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java")).exists();

    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java").exists()).isFalse();
    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java").exists()).isFalse();
  }

  @Test
  public void executeWithSchemaPatternContainingBlank() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern("SCHEMA1BLANK");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java").exists()).isFalse();
    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java").exists()).isFalse();
    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java").exists()).isFalse();
  }

  @Test
  public void executeWithMultipleSchemaPatternsAndInterleavedBlank() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern("SCHEMA1,BLANK,SCHEMA2");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java")).exists();
    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java")).exists();
    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java")).exists();
  }

  @Test
  public void executeWithMultipleSchemaPatternsAndLeadingBlank() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern("BLANK,SCHEMA2");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java")).exists();
    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java")).exists();

    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java").exists()).isFalse();
  }

  @Test
  @Ignore("Trailing empty strings are not handled correctly by the MetaDataExporter")
  public void executeWithMultipleSchemaPatternsAndTrailingBlank() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern("SCHEMA1,BLANK");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java")).exists();
    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java")).exists();

    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java").exists()).isFalse();
  }

  @Test
  public void executeWithMultipleSchemaPatternsAndContainingBlank() throws Exception {
    var targetFolder = "target/" + testName.getMethodName();

    mojo.setTargetFolder(targetFolder);
    mojo.setSchemaPattern("SCHEMA1,SCHEMA2BLANK");
    mojo.execute();

    assertThat(new File(targetFolder + "/com/example/QSchema1Table.java")).exists();

    assertThat(new File(targetFolder + "/com/example/QNoSchemaTable.java").exists()).isFalse();
    assertThat(new File(targetFolder + "/com/example/QSchema2Table.java").exists()).isFalse();
  }

  // endregion Schema Pattern Matching - BLANK Values
}
