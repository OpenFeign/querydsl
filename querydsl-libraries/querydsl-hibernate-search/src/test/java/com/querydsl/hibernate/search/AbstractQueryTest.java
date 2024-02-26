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
package com.querydsl.hibernate.search;

import com.querydsl.core.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractQueryTest {

  private static SessionFactory sessionFactory;

  @BeforeClass
  public static void setUpClass() throws IOException {
    FileUtils.delete(new File("target/derbydb"));
    FileUtils.delete(new File("target/lucene3"));
    Configuration cfg = new Configuration();
    cfg.addAnnotatedClass(User.class);
    Properties props = new Properties();
    try (InputStream is = SearchQueryTest.class.getResourceAsStream("/derby.properties")) {
      props.load(is);
    }
    cfg.setProperties(props);
    sessionFactory = cfg.buildSessionFactory();
  }

  @AfterClass
  public static void tearDownClass() {
    if (sessionFactory != null) {
      sessionFactory.close();
    }
  }

  private Session session;

  protected Session getSession() {
    return session;
  }

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    session = sessionFactory.openSession();
    session.beginTransaction();

    // clean up
    List<User> users = session.createQuery("from User").list();
    for (User user : users) {
      session.delete(user);
    }
    session.flush();
  }

  @After
  public void tearDown() throws HibernateException, SQLException {
    if (session
        .getTransaction()
        .getStatus()
        .isNotOneOf(
            TransactionStatus.ROLLED_BACK,
            TransactionStatus.ROLLING_BACK,
            TransactionStatus.MARKED_ROLLBACK)) {
      session.getTransaction().commit();
    }
    session.close();
  }
}
