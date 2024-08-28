package com.querydsl.example.jpa.repository;

import com.google.inject.persist.Transactional;
import com.querydsl.example.jpa.guice.GuiceTestRunner;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(GuiceTestRunner.class)
public abstract class AbstractPersistenceTest {
  @Inject private Provider<EntityManager> em;

  @Before
  @Transactional
  public void before() {
    var entityManager = em.get();
    entityManager.getEntityManagerFactory().getCache().evictAll();
    var session = entityManager.unwrap(Session.class);
    session.doWork(
        new Work() {
          @Override
          public void execute(Connection connection) throws SQLException {
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

            try (var stmt = connection.createStatement()) {
              stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
              for (String table : tables) {
                stmt.execute("TRUNCATE TABLE " + table);
              }
              stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
            }
          }
        });
  }
}
