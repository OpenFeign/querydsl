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
package com.querydsl.sql.codegen.ant;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.junit.BeforeClass;
import org.junit.Test;

public class AntMetaDataExporterTest {

  private static final String url = "jdbc:h2:./target/dbs/h2_AntMetaDataExporterTest;MODE=legacy";

  @BeforeClass
  public static void setUp() throws SQLException {
    try (var conn = DriverManager.getConnection(url, "sa", "")) {
      try (var stmt = conn.createStatement()) {
        stmt.execute("drop table test if exists");
        stmt.execute("create table test (id int)");
      }
    }
  }

  @Test
  public void execute() {
    var exporter = new AntMetaDataExporter();
    exporter.setJdbcDriver("org.h2.Driver");
    exporter.setJdbcUser("sa");
    exporter.setJdbcUrl(url);
    exporter.setPackageName("test");
    exporter.setTargetFolder("target/AntMetaDataExporterTest");
    exporter.execute();

    assertThat(new File("target/AntMetaDataExporterTest")).exists();
    assertThat(new File("target/AntMetaDataExporterTest/test/QTest.java")).exists();
  }

  @Test
  public void execute_with_beans() {
    var exporter = new AntMetaDataExporter();
    exporter.setJdbcDriver("org.h2.Driver");
    exporter.setJdbcUser("sa");
    exporter.setJdbcUrl(url);
    exporter.setPackageName("test");
    exporter.setTargetFolder("target/AntMetaDataExporterTest2");
    exporter.setExportBeans(true);
    exporter.setNamePrefix("Q");
    exporter.setNameSuffix("");
    exporter.setBeanPrefix("");
    exporter.setBeanSuffix("Bean");
    exporter.execute();

    assertThat(new File("target/AntMetaDataExporterTest2")).exists();
    assertThat(new File("target/AntMetaDataExporterTest2/test/QTest.java")).exists();
    assertThat(new File("target/AntMetaDataExporterTest2/test/TestBean.java")).exists();
  }

  @Test
  public void execute_with_import() {
    var exporter = new AntMetaDataExporter();
    exporter.setJdbcDriver("org.h2.Driver");
    exporter.setJdbcUser("sa");
    exporter.setJdbcUrl(url);
    exporter.setPackageName("test");
    exporter.setTargetFolder("target/AntMetaDataExporterTest3");
    exporter.setExportBeans(true);
    exporter.setNamePrefix("Q");
    exporter.setNameSuffix("");
    exporter.setBeanPrefix("");
    exporter.setBeanSuffix("Bean");
    exporter.setImports(new String[] {"com.pck1", "com.pck2", "com.Q1", "com.Q2"});
    exporter.execute();

    assertThat(new File("target/AntMetaDataExporterTest3")).exists();
    assertThat(new File("target/AntMetaDataExporterTest3/test/QTest.java")).exists();
    assertThat(new File("target/AntMetaDataExporterTest3/test/TestBean.java")).exists();
  }

  @Test
  public void execute_inside_ant() {
    var buildFile = new File(getClass().getResource("/build.xml").getFile());
    var p = new Project();
    p.setUserProperty("ant.file", buildFile.getAbsolutePath());
    p.init();
    var helper = ProjectHelper.getProjectHelper();
    p.addReference("ant.projectHelper", helper);
    helper.parse(p, buildFile);
    p.executeTarget(p.getDefaultTarget());

    assertThat(new File("target/AntMetaDataExporterTest4")).exists();
    assertThat(new File("target/AntMetaDataExporterTest4/test/QTest.java")).exists();
  }
}
