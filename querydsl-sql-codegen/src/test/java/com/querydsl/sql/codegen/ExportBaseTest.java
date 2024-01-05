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

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.sql.Connections;
import java.sql.SQLException;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public abstract class ExportBaseTest {

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void export() throws SQLException {
    MetadataExporterConfigImpl config = new MetadataExporterConfigImpl();
    //    config.setSpatial(true);
    config.setSchemaPattern(getSchemaPattern());
    config.setPackageName("test");
    config.setTargetFolder(folder.getRoot());
    config.setNamingStrategyClass(DefaultNamingStrategy.class);

    MetaDataExporter exporter = new MetaDataExporter(config);
    exporter.export(Connections.getConnection().getMetaData());

    assertThat(folder.getRoot().listFiles().length).isGreaterThan(0);
  }

  protected String getSchemaPattern() {
    return null;
  }

  @AfterClass
  public static void tearDownAfterClass() throws SQLException {
    Connections.close();
  }
}
