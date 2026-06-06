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
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Regression test for the JDBC column-read ordering in {@link MetaDataExporter}. Oracle exposes
 * {@code COLUMN_DEF} (index 13) as a streamed LONG and closes that stream as soon as a
 * higher-indexed column such as {@code ORDINAL_POSITION} (index 17) is read first, which throws
 * "ORA-17027: Stream has already been closed." on tables whose columns have default values. The
 * exporter therefore must read {@code COLUMN_DEF} before {@code ORDINAL_POSITION}.
 *
 * <p>The Oracle JDBC driver streams LONG / LONG RAW columns and requires that they be read in
 * SELECT-list order: the row is sent column by column and the stream of a LONG column is discarded
 * once a later column is fetched. Because {@code DatabaseMetaData.getColumns(...)} backs {@code
 * COLUMN_DEF} with such a LONG, the same ordering constraint applies to reading column metadata.
 * See:
 *
 * <ul>
 *   <li>Oracle JDBC Developer's Guide, "Java Streams in JDBC": <a
 *       href="https://docs.oracle.com/cd/E11882_01/java.112/e16548/jstreams.htm">
 *       https://docs.oracle.com/cd/E11882_01/java.112/e16548/jstreams.htm</a>
 *   <li>jOOQ, "Oracle LONG and LONG RAW Causing 'Stream has already been closed' Exception": <a
 *       href="https://blog.jooq.org/oracle-long-and-long-raw-causing-stream-has-already-been-closed-exception/">
 *       https://blog.jooq.org/oracle-long-and-long-raw-causing-stream-has-already-been-closed-exception/</a>
 * </ul>
 */
public class MetaDataExporterColumnDefOrderingTest {

  @TempDir File folder;

  @Test
  public void column_def_is_read_before_ordinal_position() throws SQLException {
    var readOrder = new ArrayList<String>();

    ResultSet columns = createMock(ResultSet.class);
    expect(columns.next()).andReturn(true).andReturn(false);
    expect(columns.getString("COLUMN_NAME")).andReturn("NAME").anyTimes();
    expect(columns.getInt("DATA_TYPE")).andReturn(Types.VARCHAR).anyTimes();
    expect(columns.getString("TYPE_NAME")).andReturn("VARCHAR").anyTimes();
    expect(columns.getObject("COLUMN_SIZE")).andReturn(255).anyTimes();
    expect(columns.getObject("DECIMAL_DIGITS")).andReturn(null).anyTimes();
    expect(columns.getInt("NULLABLE")).andReturn(DatabaseMetaData.columnNullable).anyTimes();
    // Simulate Oracle's streamed-LONG behaviour: once ORDINAL_POSITION has been read the COLUMN_DEF
    // stream is closed, so reading it afterwards fails.
    expect(columns.getString("COLUMN_DEF"))
        .andAnswer(
            () -> {
              if (readOrder.contains("ORDINAL_POSITION")) {
                throw new SQLException("ORA-17027: Stream has already been closed.");
              }
              readOrder.add("COLUMN_DEF");
              return "'some default'";
            })
        .anyTimes();
    expect(columns.getInt("ORDINAL_POSITION"))
        .andAnswer(
            () -> {
              readOrder.add("ORDINAL_POSITION");
              return 1;
            })
        .anyTimes();
    columns.close();
    replay(columns);

    DatabaseMetaData metadata = createMock(DatabaseMetaData.class);
    expect(metadata.getDatabaseProductName()).andReturn("Oracle").anyTimes();
    expect(metadata.getDatabaseMajorVersion()).andReturn(19).anyTimes();
    expect(metadata.getTables(anyObject(), anyObject(), anyObject(), anyObject()))
        .andReturn(tableRow());
    expect(metadata.getColumns(anyObject(), anyObject(), anyObject(), anyObject()))
        .andReturn(columns);
    replay(metadata);

    var config = new MetadataExporterConfigImpl();
    config.setPackageName("test");
    config.setTargetFolder(folder);
    // Keep the mock surface focused on column reading.
    config.setExportPrimaryKeys(false);
    config.setExportForeignKeys(false);

    var exporter = new MetaDataExporter(config);
    // With the old ordering (ORDINAL_POSITION first) the COLUMN_DEF answer would raise ORA-17027
    // and export(...) would propagate the SQLException, failing this test.
    exporter.export(metadata);

    assertThat(readOrder).containsExactly("COLUMN_DEF", "ORDINAL_POSITION");
    assertThat(new File(folder, "test/QFoo.java")).exists();
  }

  private ResultSet tableRow() throws SQLException {
    ResultSet tables = createMock(ResultSet.class);
    expect(tables.next()).andReturn(true).andReturn(false);
    expect(tables.getString("TABLE_CAT")).andReturn(null).anyTimes();
    expect(tables.getString("TABLE_SCHEM")).andReturn("PUBLIC").anyTimes();
    expect(tables.getString("TABLE_NAME")).andReturn("FOO").anyTimes();
    tables.close();
    replay(tables);
    return tables;
  }
}
