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
package com.querydsl.sql.vector;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.sql.Configuration;
import com.querydsl.sql.codegen.MetaDataExporter;
import com.querydsl.sql.codegen.MetadataExporterConfigImpl;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("com.querydsl.core.testutil.PostgreSQL")
class VectorCodegenTest {

  @Test
  void exportsVectorColumnsAlongsideSpatialSupport() throws Exception {
    try (var connection = PostgreSQLVectorTest.openPgvectorConnection()) {
      PostgreSQLVectorTest.installVectorExtensionAndRecreateVectorDocumentTable(connection);
      var config = new MetadataExporterConfigImpl();
      config.setPackageName("test");
      config.setTargetFolder(new File("target/vectorExport"));
      config.setTableNamePattern("vector_document");
      var exporter = new MetaDataExporter(config);
      exporter.setConfiguration(new Configuration(new PGvectorTemplates()));

      exporter.export(connection.getMetaData());
    }

    var generatedQVectorDocumentSource =
        Files.readString(Path.of("target/vectorExport/test/QVectorDocument.java"));
    assertThat(generatedQVectorDocumentSource)
        .contains("extends RelationalPathSpatial<")
        .contains(
            "public final VectorPath embedding = new VectorPath(forProperty(\"embedding\"));");
  }
}
