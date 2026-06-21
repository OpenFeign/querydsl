/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.sql.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.sql.Connections;
import java.io.File;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public abstract class ExportBaseTest {

  @TempDir File folder;

  @Test
  public void export() throws SQLException {
    var config = new MetadataExporterConfigImpl();
    //    config.setSpatial(true);
    config.setSchemaPattern(getSchemaPattern());
    config.setPackageName("test");
    config.setTargetFolder(folder);
    config.setNamingStrategyClass(DefaultNamingStrategy.class);

    var exporter = new MetaDataExporter(config);
    exporter.export(Connections.getConnection().getMetaData());

    assertThat(folder.listFiles().length).isGreaterThan(0);
  }

  protected String getSchemaPattern() {
    return null;
  }

  @AfterAll
  public static void tearDownAfterClass() throws SQLException {
    Connections.close();
  }
}
