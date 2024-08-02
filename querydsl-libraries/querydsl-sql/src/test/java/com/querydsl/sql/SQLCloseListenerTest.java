package com.querydsl.sql;

import static com.querydsl.sql.Constants.employee;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.testutil.H2;
import com.querydsl.sql.domain.Employee;
import java.sql.SQLException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(H2.class)
public class SQLCloseListenerTest {

  private SQLQuery<Employee> query;

  @Before
  public void setUp() throws SQLException, ClassNotFoundException {
    Connections.initH2();
    var conf = new Configuration(H2Templates.DEFAULT);
    conf.addListener(SQLCloseListener.DEFAULT);
    query = new SQLQuery<Void>(Connections.getConnection(), conf).select(employee).from(employee);
  }

  @After
  public void tearDown() throws SQLException {
    Connections.close();
  }

  @Test
  public void fetch() {
    assertThat(query.fetch()).isNotEmpty();
  }

  @Test
  public void fetchOne() {
    assertThat(query.limit(1).fetchOne()).isNotNull();
  }

  @Test
  public void fetchFirst() {
    assertThat(query.fetchFirst()).isNotNull();
  }

  @Test
  public void fetchResults() {
    assertThat(query.fetchResults()).isNotNull();
  }

  @Test
  public void iterate() {
    try (var it = query.iterate()) {
      while (it.hasNext()) {
        it.next();
      }
    }
  }
}
