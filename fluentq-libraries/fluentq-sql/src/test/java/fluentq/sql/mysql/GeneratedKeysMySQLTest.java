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
package fluentq.sql.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.sql.H2Templates;
import fluentq.sql.QGeneratedKeysEntity;
import fluentq.sql.dml.SQLInsertClause;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fluentq.core.testutil.MySQL")
public class GeneratedKeysMySQLTest {

  private Connection conn;

  private Statement stmt;

  @BeforeEach
  public void setUp() throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.jdbc.Driver");
    var url = "jdbc:mysql://localhost:3306/fluentq";
    conn = DriverManager.getConnection(url, "fluentq", "fluentq");
    stmt = conn.createStatement();
  }

  @AfterEach
  public void tearDown() throws SQLException {
    try {
      stmt.close();
    } finally {
      conn.close();
    }
  }

  @Test
  public void test() throws SQLException {
    stmt.execute("drop table if exists GENERATED_KEYS");
    stmt.execute(
        """
        create table GENERATED_KEYS(\
        ID int AUTO_INCREMENT PRIMARY KEY, \
        NAME varchar(30))\
        """);

    var entity = new QGeneratedKeysEntity("entity");
    var insertClause = new SQLInsertClause(conn, new H2Templates(), entity);
    var rs = insertClause.set(entity.name, "Hello").executeWithKeys();
    var md = rs.getMetaData();
    System.out.println(md.getColumnName(1));

    assertThat(rs.next()).isTrue();
    assertThat(rs.getInt(1)).isEqualTo(1);
    assertThat(rs.next()).isFalse();

    insertClause = new SQLInsertClause(conn, new H2Templates(), entity);
    rs = insertClause.set(entity.name, "World").executeWithKeys();
    assertThat(rs.next()).isTrue();
    assertThat(rs.getInt(1)).isEqualTo(2);
    assertThat(rs.next()).isFalse();
  }
}
