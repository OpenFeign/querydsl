package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinType;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.testutil.H2;
import com.querydsl.core.testutil.Performance;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

@Category({H2.class, Performance.class})
public class QueryPerformanceTest {

  private static final String QUERY =
      """
      select COMPANIES.NAME
      from COMPANIES COMPANIES
      where COMPANIES.ID = ?\
      """;

  private static final SQLTemplates templates = new H2Templates();

  private static final Configuration conf = new Configuration(templates);

  @BeforeClass
  public static void setUpClass() throws SQLException, ClassNotFoundException {
    Connections.initH2();
    var conn = Connections.getConnection();
    var stmt = conn.createStatement();
    stmt.execute(
        "create or replace table companies (id identity, name varchar(30) unique not null);");

    var pstmt = conn.prepareStatement("insert into companies (name) values (?)");
    final var iterations = 1000000;
    for (var i = 0; i < iterations; i++) {
      pstmt.setString(1, String.valueOf(i));
      pstmt.execute();
      pstmt.clearParameters();
    }
    pstmt.close();
    stmt.close();

    conn.setAutoCommit(false);
  }

  @AfterClass
  public static void tearDownClass() throws SQLException {
    var conn = Connections.getConnection();
    var stmt = conn.createStatement();
    stmt.execute("drop table companies");
    stmt.close();
    Connections.close();
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  public void jDBC() throws Exception {
    try (var conn = Connections.getH2();
        var stmt = conn.prepareStatement(QUERY)) {
      stmt.setLong(1, ThreadLocalRandom.current().nextLong());
      try (var rs = stmt.executeQuery()) {
        while (rs.next()) {
          rs.getString(1);
        }
      }
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  public void jDBC2() throws Exception {
    try (var conn = Connections.getH2();
        var stmt = conn.prepareStatement(QUERY)) {
      stmt.setString(1, String.valueOf(ThreadLocalRandom.current().nextLong()));
      try (var rs = stmt.executeQuery()) {
        while (rs.next()) {
          rs.getString(1);
        }
      }
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  public void querydsl1() throws Exception {
    try (var conn = Connections.getH2()) {
      var companies = QCompanies.companies;
      SQLQuery<?> query = new SQLQuery<Void>(conn, conf);
      query
          .from(companies)
          .where(companies.id.eq(ThreadLocalRandom.current().nextLong()))
          .select(companies.name)
          .fetch();
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  public void querydsl12() throws Exception {
    try (var conn = Connections.getH2()) {
      var companies = QCompanies.companies;
      SQLQuery<?> query = new SQLQuery<Void>(conn, conf);
      try (var it =
          query
              .from(companies)
              .where(companies.id.eq(ThreadLocalRandom.current().nextLong()))
              .select(companies.name)
              .iterate()) {
        while (it.hasNext()) {
          it.next();
        }
      }
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  public void querydsl13() throws Exception {
    try (var conn = Connections.getH2()) {
      var companies = QCompanies.companies;
      SQLQuery<?> query = new SQLQuery<Void>(conn, conf);
      try (var rs =
          query
              .select(companies.name)
              .from(companies)
              .where(companies.id.eq(ThreadLocalRandom.current().nextLong()))
              .getResults()) {
        while (rs.next()) {
          rs.getString(1);
        }
      }
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  public void querydsl14() throws Exception {
    try (var conn = Connections.getH2()) {
      var companies = QCompanies.companies;
      SQLQuery<?> query = new SQLQuery<Void>(conn, conf, new DefaultQueryMetadata());
      query
          .from(companies)
          .where(companies.id.eq(ThreadLocalRandom.current().nextLong()))
          .select(companies.name)
          .fetch();
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  public void querydsl15() throws Exception {
    try (var conn = Connections.getH2()) {
      var companies = QCompanies.companies;
      SQLQuery<?> query = new SQLQuery<Void>(conn, conf);
      query
          .from(companies)
          .where(companies.id.eq(ThreadLocalRandom.current().nextLong()))
          .select(companies.id, companies.name)
          .fetch();
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  public void querydsl2() throws Exception {
    try (var conn = Connections.getH2()) {
      var companies = QCompanies.companies;
      SQLQuery<?> query = new SQLQuery<Void>(conn, conf);
      query
          .from(companies)
          .where(companies.name.eq(String.valueOf(ThreadLocalRandom.current().nextLong())))
          .select(companies.name)
          .fetch();
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  public void querydsl22() throws Exception {
    try (var conn = Connections.getH2()) {
      var companies = QCompanies.companies;
      SQLQuery<?> query = new SQLQuery<Void>(conn, conf);
      try (var it =
          query
              .from(companies)
              .where(companies.name.eq(String.valueOf(ThreadLocalRandom.current().nextLong())))
              .select(companies.name)
              .iterate()) {
        while (it.hasNext()) {
          it.next();
        }
      }
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  public void querydsl23() throws Exception {
    try (var conn = Connections.getH2()) {
      var companies = QCompanies.companies;
      SQLQuery<?> query = new SQLQuery<Void>(conn, conf, new DefaultQueryMetadata());
      query
          .from(companies)
          .where(companies.name.eq(String.valueOf(ThreadLocalRandom.current().nextLong())))
          .select(companies.name)
          .fetch();
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.MICROSECONDS)
  public void serialization() throws Exception {
    try (var conn = Connections.getH2()) {
      var companies = QCompanies.companies;
      final QueryMetadata md = new DefaultQueryMetadata();
      md.addJoin(JoinType.DEFAULT, companies);
      md.addWhere(companies.id.eq(1L));
      md.setProjection(companies.name);

      var serializer = new SQLSerializer(conf);
      serializer.serialize(md, false);
      serializer.getConstants();
      serializer.getConstantPaths();
      assertThat(serializer.toString()).isNotNull();
    }
  }

  @Test
  public void launchBenchmark() throws Exception {
    var opt =
        new OptionsBuilder()
            .include(this.getClass().getName() + ".*")
            .mode(Mode.AverageTime)
            .timeUnit(TimeUnit.MICROSECONDS)
            .warmupTime(TimeValue.seconds(1))
            .warmupIterations(1)
            .measurementTime(TimeValue.seconds(1))
            .measurementIterations(3)
            .threads(1)
            .forks(1)
            .shouldFailOnError(true)
            .shouldDoGC(true)
            .build();

    new Runner(opt).run();
  }
}
