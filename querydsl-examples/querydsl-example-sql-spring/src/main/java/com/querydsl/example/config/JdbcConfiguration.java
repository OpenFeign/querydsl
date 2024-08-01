package com.querydsl.example.config;

import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.querydsl.sql.spring.SpringExceptionTranslator;
import com.querydsl.sql.types.LocalDateTimeType;
import com.querydsl.sql.types.LocalDateType;
import io.github.openfeign.querydsl.sql.json.types.JSONType;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource({"classpath:jdbc.properties"})
public class JdbcConfiguration {

  @Autowired Environment env;

  @Bean
  public DataSource dataSource() {
    var dataSource = new SimpleDriverDataSource();
    Class driver;
    try {
      driver = Class.forName(env.getRequiredProperty("jdbc.driver"));
    } catch (ClassNotFoundException | IllegalStateException e) {
      throw new RuntimeException(e);
    }
    dataSource.setDriverClass(driver);
    dataSource.setUrl(env.getRequiredProperty("jdbc.url"));
    dataSource.setUsername(env.getRequiredProperty("jdbc.user"));
    dataSource.setPassword(env.getRequiredProperty("jdbc.password"));
    return dataSource;
  }

  @Bean
  public PlatformTransactionManager transactionManager() {
    return new DataSourceTransactionManager(dataSource());
  }

  @Bean
  public com.querydsl.sql.Configuration querydslConfiguration() {
    var templates = H2Templates.builder().build();
    var configuration = new com.querydsl.sql.Configuration(templates);
    configuration.setExceptionTranslator(new SpringExceptionTranslator());
    configuration.register(new LocalDateTimeType());
    configuration.register(new LocalDateType());
    configuration.register(new JSONType());
    return configuration;
  }

  @Bean
  public SQLQueryFactory queryFactory() {
    var provider = new SpringConnectionProvider(dataSource());
    return new SQLQueryFactory(querydslConfiguration(), provider);
  }
}
