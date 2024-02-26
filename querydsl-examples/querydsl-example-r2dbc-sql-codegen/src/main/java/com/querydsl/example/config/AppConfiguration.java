package com.querydsl.example.config;

import com.querydsl.example.dao.CustomerDao;
import com.querydsl.example.dao.CustomerDaoImpl;
import com.querydsl.example.dao.OrderDao;
import com.querydsl.example.dao.OrderDaoImpl;
import com.querydsl.example.dao.PersonDao;
import com.querydsl.example.dao.PersonDaoImpl;
import com.querydsl.example.dao.ProductDao;
import com.querydsl.example.dao.ProductDaoImpl;
import com.querydsl.example.dao.SupplierDao;
import com.querydsl.example.dao.SupplierDaoImpl;
import com.querydsl.r2dbc.R2DBCConnectionProvider;
import com.querydsl.r2dbc.R2DBCQueryFactory;
import com.querydsl.r2dbc.SQLTemplates;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import reactor.core.publisher.Mono;

@Configuration
@EnableTransactionManagement
@Import(R2DBCConfiguration.class)
public class AppConfiguration {

  @Autowired Environment env;

  @Bean
  CustomerDao customerDao() {
    return new CustomerDaoImpl();
  }

  @Bean
  OrderDao orderDao() {
    return new OrderDaoImpl();
  }

  @Bean
  PersonDao personDao() {
    return new PersonDaoImpl();
  }

  @Bean
  ProductDao productDao() {
    return new ProductDaoImpl();
  }

  @Bean
  SupplierDao supplierDao() {
    return new SupplierDaoImpl();
  }

  @Bean
  R2DBCConnectionProvider provider(ConnectionFactory connectionFactory) {
    return () -> Mono.from(connectionFactory.create());
  }

  @Bean
  R2DBCQueryFactory queryFactory(R2DBCConnectionProvider provider) {
    return new R2DBCQueryFactory(SQLTemplates.DEFAULT, provider);
  }

  @Bean
  R2dbcTransactionManager transactionManager(ConnectionFactory connectionFactory) {
    return new R2dbcTransactionManager(connectionFactory);
  }
}
