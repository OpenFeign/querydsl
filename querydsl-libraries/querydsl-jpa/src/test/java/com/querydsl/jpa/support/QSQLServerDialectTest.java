package com.querydsl.jpa.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Boots an offline Hibernate {@link SessionFactory} with the dialect forced to {@link
 * QSQLServerDialect} so that the functions it contributes through {@code
 * initializeFunctionRegistry} can be inspected without a running SQL Server. JDBC metadata access
 * is disabled so the throwaway H2 connection is never probed for dialect resolution.
 */
public class QSQLServerDialectTest {

  private static SessionFactory sessionFactory;
  private static SqmFunctionRegistry functionRegistry;

  @BeforeAll
  static void bootSessionFactory() {
    var props = new Properties();
    props.put("hibernate.dialect", QSQLServerDialect.class.getName());
    props.put("hibernate.connection.driver_class", "org.h2.Driver");
    props.put("hibernate.connection.url", "jdbc:h2:mem:qsqlserverdialect;DB_CLOSE_DELAY=-1");
    props.put("hibernate.connection.username", "sa");
    props.put("hibernate.connection.password", "");
    // Keep the forced SQL Server dialect: never inspect the H2 connection to resolve a dialect.
    props.put("hibernate.boot.allow_jdbc_metadata_access", "false");
    props.put("hibernate.hbm2ddl.auto", "none");

    ServiceRegistry serviceRegistry =
        new StandardServiceRegistryBuilder().applySettings(props).build();
    var cfg = new Configuration();
    cfg.setProperties(props);
    sessionFactory = cfg.buildSessionFactory(serviceRegistry);
    functionRegistry =
        sessionFactory
            .unwrap(SessionFactoryImplementor.class)
            .getQueryEngine()
            .getSqmFunctionRegistry();
  }

  @AfterAll
  static void closeSessionFactory() {
    if (sessionFactory != null) {
      sessionFactory.close();
    }
  }

  @Test
  public void registers_querydsl_date_functions() {
    // The date-part functions contributed from SQLServer2012Templates must be present.
    assertThat(functionRegistry.findFunctionDescriptor("year")).isNotNull();
    assertThat(functionRegistry.findFunctionDescriptor("month")).isNotNull();
    assertThat(functionRegistry.findFunctionDescriptor("week")).isNotNull();
    assertThat(functionRegistry.findFunctionDescriptor("day")).isNotNull();
    assertThat(functionRegistry.findFunctionDescriptor("hour")).isNotNull();
    assertThat(functionRegistry.findFunctionDescriptor("minute")).isNotNull();
    assertThat(functionRegistry.findFunctionDescriptor("second")).isNotNull();
  }

  @Test
  public void registers_current_date() {
    // current_date has a null Hibernate type (Comparable) and is registered through the no-type
    // branch of DialectSupport; the dialect must still expose it.
    assertThat(functionRegistry.findFunctionDescriptor("current_date")).isNotNull();
  }
}
