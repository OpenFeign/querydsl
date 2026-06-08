package com.querydsl.sql.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.sql.Connections;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@Tag("com.querydsl.core.testutil.H2")
public class ExportH2TwoSchemasTest {

  @TempDir File folder;

  @BeforeAll
  public static void setUpClass() throws Exception {
    Connections.initH2();

    var stmt = Connections.getStatement();
    stmt.execute("create schema if not exists newschema");
    stmt.execute(
        """
        create table if not exists \
        newschema.SURVEY2(ID2 int auto_increment, NAME2 varchar(30), NAME3 varchar(30))\
        """);
  }

  @AfterAll
  public static void tearDownAfterClass() throws SQLException {
    Connections.close();
  }

  @Test
  public void export() throws SQLException, MalformedURLException, IOException {
    var config = new MetadataExporterConfigImpl();
    config.setSchemaPattern(null);
    config.setPackageName("test");
    config.setTargetFolder(folder);

    var exporter = new MetaDataExporter(config);
    exporter.export(Connections.getConnection().getMetaData());

    var contents = Files.readString(new File(folder, "test/QSurvey.java").toPath());
    assertThat(contents).contains("id");
    assertThat(contents).contains("name");
    assertThat(contents).contains("name2");

    assertThat(contents).doesNotContain("id2");
    assertThat(contents).doesNotContain("name3");
  }
}
