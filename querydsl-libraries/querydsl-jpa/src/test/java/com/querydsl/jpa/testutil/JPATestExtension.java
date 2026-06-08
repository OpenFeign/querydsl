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
package com.querydsl.jpa.testutil;

import com.querydsl.jpa.JPATest;
import com.querydsl.jpa.Mode;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

/**
 * JUnit 5 replacement for the former {@code JPATestRunner}. Creates a single {@link
 * EntityManagerFactory}/{@link EntityManager} (with an open transaction) per test class, injects
 * the entity manager into every {@link JPATest} instance and rolls everything back afterwards.
 */
public class JPATestExtension
    implements BeforeAllCallback, AfterAllCallback, TestInstancePostProcessor {

  private EntityManagerFactory entityManagerFactory;

  private EntityManager entityManager;

  private boolean isDerby;

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    var mode = Mode.mode.get();
    if (mode == null) {
      mode = "h2perf";
    }
    System.out.println(mode);
    isDerby = mode.contains("derby");
    if (isDerby) {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver").getDeclaredConstructor().newInstance();
    }
    entityManagerFactory = Persistence.createEntityManagerFactory(mode);
    entityManager = entityManagerFactory.createEntityManager();
    entityManager.getTransaction().begin();
  }

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
    if (!(testInstance instanceof JPATest jpaTest)) {
      throw new IllegalStateException(
          "In order to use the %s for %s, it should (directly or indirectly) implement %s"
              .formatted(
                  JPATestExtension.class.getSimpleName(), testInstance.getClass(), JPATest.class));
    }
    jpaTest.setEntityManager(entityManager);
  }

  @Override
  public void afterAll(ExtensionContext context) {
    shutdown();
  }

  private void shutdown() {
    if (entityManager != null) {
      try {
        if (entityManager.getTransaction().isActive()) {
          entityManager.getTransaction().rollback();
        }
      } finally {
        entityManager.close();
        entityManager = null;
      }
    }

    if (entityManagerFactory != null) {
      if (entityManagerFactory.getCache() != null) {
        entityManagerFactory.getCache().evictAll();
      }
      entityManagerFactory.close();
      entityManagerFactory = null;
    }

    // clean shutdown of derby
    if (isDerby) {
      try {
        DriverManager.getConnection("jdbc:derby:;shutdown=true");
      } catch (SQLException e) {
        if (!e.getMessage().equals("Derby system shutdown.")) {
          throw new RuntimeException(e);
        }
      }
    }
  }
}
