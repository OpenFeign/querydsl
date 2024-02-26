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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.querydsl.codegen.BeanSerializer;
import com.querydsl.codegen.GeneratedAnnotationResolver;
import com.querydsl.codegen.utils.SimpleCompiler;
import com.querydsl.sql.AbstractJDBCTest;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.types.AbstractType;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.tools.JavaCompiler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MetaDataSerializerTest extends AbstractJDBCTest {
  public static class CustomNumber {}

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Override
  @Before
  public void setUp() throws SQLException, ClassNotFoundException {
    super.setUp();
    statement.execute("drop table employee if exists");
    statement.execute("drop table survey if exists");
    statement.execute("drop table date_test if exists");
    statement.execute("drop table date_time_test if exists");
    statement.execute("drop table spaces if exists");

    // survey
    statement.execute(
        """
        create table survey (id int, name varchar(30), \
        CONSTRAINT PK_survey PRIMARY KEY (id, name))\
        """);

    // date_test
    statement.execute("create table date_test (d date)");

    // date_time
    statement.execute("create table date_time_test (dt datetime)");

    // spaces
    statement.execute("create table spaces (\"spaces  \n 1\" date)");

    // employee
    statement.execute(
        """
			create table employee(
			id INT,
			firstname VARCHAR(50),
			lastname VARCHAR(50),
			salary DECIMAL(10, 2),
			datefield DATE,
			timefield TIME,
			superior_id int,
			survey_id int,
			"123abc" int,
			survey_name varchar(30),
			CONSTRAINT PK_employee PRIMARY KEY (id),
			CONSTRAINT FK_survey FOREIGN KEY (survey_id, survey_name) REFERENCES survey(id,name),
			CONSTRAINT FK_superior FOREIGN KEY (superior_id) REFERENCES employee(id))""");
  }

  @Test
  public void normal_serialization() throws SQLException {
    var namePrefix = "Q";
    // customization of serialization
    var config = new MetadataExporterConfigImpl();
    config.setBeanSerializerClass(BeanSerializer.class);
    config.setNamePrefix(namePrefix);
    config.setPackageName("test");
    config.setTargetFolder(folder.getRoot());
    config.setNamingStrategyClass(DefaultNamingStrategy.class);
    config.setExportBeans(true);
    var exporter = new MetaDataExporter(config);
    exporter.export(connection.getMetaData());

    compile(exporter);

    // validation of output
    try {
      //
      assertFileContainsInOrder(
          "test/QSurvey.java",
          "import %s;".formatted(GeneratedAnnotationResolver.resolveDefault().getName()),
          "@Generated(\"com.querydsl.sql.codegen.MetaDataSerializer\")\npublic class QSurvey",
          // variable + schema constructor
          """
              public QSurvey(String variable, String schema) {
                  super(Survey.class, forVariable(variable), schema, "SURVEY");
                  addMetadata();
              }\
          """);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Test
  public void customized_serialization() throws SQLException {
    var namePrefix = "Q";
    var conf = new Configuration(SQLTemplates.DEFAULT);
    conf.register(
        "EMPLOYEE",
        "ID",
        new AbstractType<CustomNumber>(0) {
          @Override
          public Class<CustomNumber> getReturnedClass() {
            return CustomNumber.class;
          }

          @Override
          public CustomNumber getValue(ResultSet rs, int startIndex) throws SQLException {
            throw new UnsupportedOperationException();
          }

          @Override
          public void setValue(PreparedStatement st, int startIndex, CustomNumber value)
              throws SQLException {
            throw new UnsupportedOperationException();
          }
        });
    // customization of serialization
    var config = new MetadataExporterConfigImpl();
    config.setBeanSerializerClass(BeanSerializer.class);
    config.setNamePrefix(namePrefix);
    config.setPackageName("test");
    config.setTargetFolder(folder.getRoot());
    config.setNamingStrategyClass(DefaultNamingStrategy.class);
    config.setGeneratedAnnotationClass("com.querydsl.core.annotations.Generated");
    config.setExportBeans(true);
    var exporter = new MetaDataExporter(config);
    exporter.setConfiguration(conf);
    exporter.export(connection.getMetaData());

    compile(exporter);

    // validation of output
    try {
      //
      assertFileContainsInOrder(
          "test/QSurvey.java",
          "import com.querydsl.core.annotations.Generated;",
          "@Generated(\"com.querydsl.sql.codegen.MetaDataSerializer\")\npublic class QSurvey",
          // variable + schema constructor
          """
              public QSurvey(String variable, String schema) {
                  super(Survey.class, forVariable(variable), schema, "SURVEY");
                  addMetadata();
              }\
          """);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private void compile(MetaDataExporter exporter) {
    JavaCompiler compiler = new SimpleCompiler();
    var classes = exporter.getClasses();
    var compilationResult = compiler.run(null, null, null, classes.toArray(new String[0]));
    if (compilationResult == 0) {
      System.out.println("Compilation is successful");
    } else {
      fail("Compilation Failed");
    }
  }

  private void assertFileContainsInOrder(String path, String... methods) throws IOException {
    var content = new String(Files.readAllBytes(folder.getRoot().toPath().resolve(path)), UTF_8);
    assertThat(content).containsIgnoringWhitespaces(methods);
  }
}
