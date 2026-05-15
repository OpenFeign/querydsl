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
import com.querydsl.jpa.domain.QGeneratedKeyEntity;
import com.querydsl.jpa.impl.JPAInsertClause;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JPAExecuteWithKeyTest {

  private static EntityManagerFactory emf;
  private EntityManager entityManager;
  private EntityTransaction tx;

  @BeforeClass
  public static void setUpClass() {
    emf =
        Persistence.createEntityManagerFactory(
            "executeWithKeyTest",
            Map.of(
                "jakarta.persistence.jdbc.driver", "org.h2.Driver",
                "jakarta.persistence.jdbc.url", "jdbc:h2:mem:jpa_ewk_test;DB_CLOSE_DELAY=-1",
                "jakarta.persistence.jdbc.user", "sa",
                "jakarta.persistence.jdbc.password", "",
                "hibernate.hbm2ddl.auto", "create-drop",
                "hibernate.show_sql", "false"));
  }

  @AfterClass
  public static void tearDownClass() {
    if (emf != null) {
      emf.close();
    }
  }

  @Before
  public void setUp() {
    entityManager = emf.createEntityManager();
    tx = entityManager.getTransaction();
    tx.begin();
  }

  @After
  public void tearDown() {
    if (tx != null && tx.isActive()) {
      tx.rollback();
    }
    if (entityManager != null) {
      entityManager.close();
    }
  }

  private JPAInsertClause insert(EntityPath<?> entity) {
    return new JPAInsertClause(entityManager, entity);
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
            entityManager
                .createNativeQuery("select name_ from generated_key_entity where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
    assertThat(stored).isEqualTo("VALUE");
  }

  @Test
  public void executeWithKey_with_zero_arg_function_template() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id =
        insert(entity)
            .set(entity.name, Expressions.stringTemplate("'fixed_' || current_user"))
            .executeWithKey(entity.id);

    assertThat(id).isNotNull();

    var stored =
        (String)
            entityManager
                .createNativeQuery("select name_ from generated_key_entity where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
    assertThat(stored).startsWith("fixed_");
  }

  @Test
  public void executeWithKeys_multi_row_returns_all_keys_in_order() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var keys =
        insert(entity)
            .columns(entity.name)
            .values("RowA")
            .addRow()
            .values("RowB")
            .addRow()
            .values("RowC")
            .executeWithKeys(entity.id);

    assertThat(keys).hasSize(3);
    assertThat(keys.get(0)).isLessThan(keys.get(1));
    assertThat(keys.get(1)).isLessThan(keys.get(2));
  }

  @Test
  public void executeWithKeys_single_row_returns_size_one_list() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var keys = insert(entity).columns(entity.name).values("Solo").executeWithKeys(entity.id);

    assertThat(keys).hasSize(1);
    assertThat(keys.get(0)).isPositive();
  }

  @Test
  public void executeWithKey_rejects_after_addRow() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    assertThatThrownBy(
            () ->
                insert(entity)
                    .columns(entity.name)
                    .values("First")
                    .addRow()
                    .values("Second")
                    .executeWithKey(entity.id))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("executeWithKeys");
  }

  @Test
  public void addRow_rejects_with_no_values() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    assertThatThrownBy(() -> insert(entity).columns(entity.name).addRow())
        .isInstanceOf(IllegalStateException.class);
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
