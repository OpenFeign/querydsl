package com.querydsl.sql.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.testutil.H2;
import com.querydsl.sql.Connections;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

@Category(H2.class)
public class ExportH2TwoSchemasTest {

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @BeforeClass
  public static void setUpClass() throws Exception {
    Connections.initH2();

    Statement stmt = Connections.getStatement();
    stmt.execute("create schema if not exists newschema");
    stmt.execute(
        """
        create table if not exists \
        newschema.SURVEY2(ID2 int auto_increment, NAME2 varchar(30), NAME3 varchar(30))\
        """);
  }

  @AfterClass
  public static void tearDownAfterClass() throws SQLException {
    Connections.close();
  }

  @Test
  public void export() throws SQLException, MalformedURLException, IOException {
    MetadataExporterConfigImpl config = new MetadataExporterConfigImpl();
    config.setSchemaPattern(null);
    config.setPackageName("test");
    config.setTargetFolder(folder.getRoot());

    MetaDataExporter exporter = new MetaDataExporter(config);
    exporter.export(Connections.getConnection().getMetaData());

    String contents =
        new String(
            Files.readAllBytes(new File(folder.getRoot(), "test/QSurvey.java").toPath()),
            StandardCharsets.UTF_8);
    assertThat(contents).contains("id");
    assertThat(contents).contains("name");
    assertThat(contents).contains("name2");

    assertThat(contents.contains("id2")).isFalse();
    assertThat(contents.contains("name3")).isFalse();
  }
}
