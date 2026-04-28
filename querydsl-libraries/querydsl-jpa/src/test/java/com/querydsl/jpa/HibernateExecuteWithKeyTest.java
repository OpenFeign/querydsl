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
package com.querydsl.jpa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.domain.GeneratedKeyEntity;
import com.querydsl.jpa.domain.QGeneratedKeyEntity;
import com.querydsl.jpa.hibernate.HibernateInsertClause;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HibernateExecuteWithKeyTest {

  private static SessionFactory sessionFactory;
  private Session session;
  private Transaction tx;

  @BeforeClass
  public static void setUpClass() {
    var cfg = new Configuration();
    cfg.addAnnotatedClass(GeneratedKeyEntity.class);
    cfg.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
    cfg.setProperty("hibernate.connection.url", "jdbc:h2:mem:hib_ewk_test;DB_CLOSE_DELAY=-1");
    cfg.setProperty("hibernate.connection.username", "sa");
    cfg.setProperty("hibernate.connection.password", "");
    cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
    cfg.setProperty("hibernate.show_sql", "false");
    sessionFactory = cfg.buildSessionFactory();
  }

  @AfterClass
  public static void tearDownClass() {
    if (sessionFactory != null) {
      sessionFactory.close();
    }
  }

  @Before
  public void setUp() {
    session = sessionFactory.openSession();
    tx = session.beginTransaction();
  }

  @After
  public void tearDown() {
    if (tx != null && tx.isActive()) {
      tx.rollback();
    }
    if (session != null) {
      session.close();
    }
  }

  private HibernateInsertClause insert(EntityPath<?> entity) {
    return new HibernateInsertClause(session, entity);
  }

  @Test
  public void executeWithKey_set_style() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id = insert(entity).set(entity.name, "TestName").executeWithKey(entity.id);

    assertThat(id).isNotNull();
    assertThat(id).isPositive();
  }

  @Test
  public void executeWithKey_columns_values_style() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id = insert(entity).columns(entity.name).values("TestName2").executeWithKey(entity.id);

    assertThat(id).isNotNull();
    assertThat(id).isPositive();
  }

  @Test
  public void executeWithKey_with_class_type() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id = insert(entity).set(entity.name, "TestName3").executeWithKey(Long.class);

    assertThat(id).isNotNull();
    assertThat(id).isPositive();
  }

  @Test
  public void executeWithKey_multiple_inserts_return_different_keys() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id1 = insert(entity).set(entity.name, "Name1").executeWithKey(entity.id);
    Long id2 = insert(entity).set(entity.name, "Name2").executeWithKey(entity.id);

    assertThat(id1).isNotNull();
    assertThat(id2).isNotNull();
    assertThat(id2).isGreaterThan(id1);
  }

  @Test
  public void executeWithKey_with_column_annotation() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id = insert(entity).set(entity.name, "ColumnTest").executeWithKey(entity.id);

    assertThat(id).isNotNull();
    assertThat(id).isPositive();
  }

  @Test
  public void executeWithKey_with_function_template_applies_function() {
    // Regression: a function template like dbo.encrypt({0}) used to be silently dropped,
    // and only the inner constant was bound, leading to plaintext being inserted.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id =
        insert(entity)
            .set(
                entity.name,
                Expressions.stringTemplate("upper({0})", Expressions.constant("value")))
            .executeWithKey(entity.id);

    assertThat(id).isNotNull();

    var stored =
        (String)
            session
                .createNativeQuery(
                    "select name_ from generated_key_entity where id = ?1", String.class)
                .setParameter(1, id)
                .getSingleResult();
    assertThat(stored).isEqualTo("VALUE");
  }

  @Test
  public void executeWithKey_rejects_subquery() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var other = new QGeneratedKeyEntity("other");

    assertThatThrownBy(
            () ->
                insert(entity)
                    .columns(entity.name)
                    .select(JPAExpressions.select(other.name).from(other))
                    .executeWithKey(entity.id))
        .isInstanceOf(UnsupportedOperationException.class);
  }
}
