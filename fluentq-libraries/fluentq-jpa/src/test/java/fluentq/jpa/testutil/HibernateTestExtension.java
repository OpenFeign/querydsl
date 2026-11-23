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
package fluentq.jpa.testutil;

import fluentq.core.Target;
import fluentq.jpa.HibernateTest;
import fluentq.jpa.Mode;
import fluentq.jpa.domain.Domain;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

/**
 * JUnit 5 replacement for the former {@code HibernateTestRunner}. Builds a single {@link
 * SessionFactory}/{@link Session} (with an open transaction) per test class, injects the session
 * into every {@link HibernateTest} instance and rolls everything back afterwards.
 */
public class HibernateTestExtension
    implements BeforeAllCallback, AfterAllCallback, TestInstancePostProcessor {

  private SessionFactory sessionFactory;

  private Session session;

  private boolean isDerby = false;

  private String previousMode;

  private Target previousTarget;

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    // Mode.mode/target are ThreadLocals shared across the @Nested suites that run on the same
    // thread. Remember the values set by the enclosing suite so they can be restored in afterAll,
    // otherwise this extension would leak HSQLDB into sibling (e.g. H2/Derby) JPA suites and make
    // @IncludeIn/@ExcludeIn evaluate against the wrong target.
    previousMode = Mode.mode.get();
    previousTarget = Mode.target.get();

    Mode.mode.set("hsqldb");
    Mode.target.set(Target.HSQLDB);

    var cfg = new Configuration();
    for (Class<?> cl : Domain.classes) {
      cfg.addAnnotatedClass(cl);
    }
    var mode = Mode.mode.get() + ".properties";
    isDerby = mode.contains("derby");
    if (isDerby) {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver").getDeclaredConstructor().newInstance();
    }
    var props = new Properties();
    var is = HibernateTestExtension.class.getResourceAsStream(mode);
    if (is == null) {
      throw new IllegalArgumentException("No configuration available at classpath:" + mode);
    }
    props.load(is);
    ServiceRegistry serviceRegistry =
        new StandardServiceRegistryBuilder().applySettings(props).build();
    cfg.setProperties(props);
    sessionFactory = cfg.buildSessionFactory(serviceRegistry);
    session = sessionFactory.openSession();
    session.beginTransaction();
  }

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
    if (!(testInstance instanceof HibernateTest hibernateTest)) {
      throw new IllegalStateException(
          "In order to use the %s for %s, it should (directly or indirectly) implement %s"
              .formatted(
                  HibernateTestExtension.class.getSimpleName(),
                  testInstance.getClass(),
                  HibernateTest.class));
    }
    hibernateTest.setSession(session);
  }

  @Override
  public void afterAll(ExtensionContext context) {
    try {
      shutdown();
    } finally {
      restoreMode();
    }
  }

  private void restoreMode() {
    if (previousMode == null) {
      Mode.mode.remove();
    } else {
      Mode.mode.set(previousMode);
    }
    if (previousTarget == null) {
      Mode.target.remove();
    } else {
      Mode.target.set(previousTarget);
    }
  }

  private void shutdown() {
    if (session != null) {
      try {
        session.getTransaction().rollback();
      } finally {
        session.close();
        session = null;
      }
    }

    if (sessionFactory != null) {
      sessionFactory.getCache().evictAll();
      sessionFactory.close();
      sessionFactory = null;
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
