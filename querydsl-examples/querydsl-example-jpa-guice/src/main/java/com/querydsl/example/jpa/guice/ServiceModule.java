package com.querydsl.example.jpa.guice;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.querydsl.example.jpa.model.Identifiable;
import com.querydsl.example.jpa.repository.TweetRepository;
import com.querydsl.example.jpa.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.jpa.HibernatePersistenceConfiguration;

public class ServiceModule extends AbstractModule {
  @Override
  protected void configure() {
    var persistence = new JpaPersistence(createEntityManagerFactory());
    bind(EntityManager.class).toProvider(persistence);
    bindInterceptor(annotatedWith(Transactional.class), any(), persistence);
    bindInterceptor(any(), annotatedWith(Transactional.class), persistence);
    bind(TweetRepository.class).in(Scopes.SINGLETON);
    bind(UserRepository.class).in(Scopes.SINGLETON);
  }

  private static EntityManagerFactory createEntityManagerFactory() {
    Map<String, Object> overrides = new HashMap<>();
    System.getProperties().forEach((key, value) -> overrides.put(key.toString(), value));
    return new HibernatePersistenceConfiguration("h2", Identifiable.class)
        .property(PersistenceConfiguration.JDBC_DRIVER, "org.h2.Driver")
        .property(PersistenceConfiguration.JDBC_URL, "jdbc:h2:./target/h2-hibernate")
        .property(PersistenceConfiguration.JDBC_USER, "sa")
        .property(PersistenceConfiguration.SCHEMAGEN_DATABASE_ACTION, "drop-and-create")
        .properties(overrides)
        .createEntityManagerFactory();
  }
}
