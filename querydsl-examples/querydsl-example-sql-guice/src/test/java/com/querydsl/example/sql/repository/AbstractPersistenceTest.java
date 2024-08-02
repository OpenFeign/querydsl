package com.querydsl.example.sql.repository;

import com.querydsl.example.sql.guice.GuiceTestRunner;
import com.querydsl.example.sql.guice.Transactional;
import jakarta.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(GuiceTestRunner.class)
public abstract class AbstractPersistenceTest {
  @Inject private DataSource dataSource;

  @Before
  @Transactional
  public void before() {
    try (var connection = dataSource.getConnection()) {
      List<String> tables = new ArrayList<>();
      var md = connection.getMetaData();
      var rs = md.getTables(null, "PUBLIC", null, new String[] {"TABLE"});
      try {
        while (rs.next()) {
          tables.add(rs.getString("TABLE_NAME"));
        }
      } finally {
        rs.close();
      }

      var stmt = connection.createStatement();
      try {
        stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
        for (String table : tables) {
          stmt.execute("TRUNCATE TABLE " + table);
        }
        stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
      } finally {
        stmt.close();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
