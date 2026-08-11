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
import com.querydsl.jpa.domain.GeneratedKeyStatusCodeConverter;
import com.querydsl.jpa.domain.QGeneratedKeyEntity;
import com.querydsl.jpa.impl.JPAInsertClause;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JPAExecuteWithKeyTest {

  private static EntityManagerFactory emf;
  private EntityManager entityManager;
  private EntityTransaction tx;

  @BeforeAll
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

  @AfterAll
  public static void tearDownClass() {
    if (emf != null) {
      emf.close();
    }
  }

  @BeforeEach
  public void setUp() {
    entityManager = emf.createEntityManager();
    tx = entityManager.getTransaction();
    tx.begin();
  }

  @AfterEach
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

  @Test
  public void execute_with_function_template_routes_through_native_sql() {
    // Regression for #1757: execute() must route to native SQL when value expressions
    // contain function templates, otherwise Hibernate's HQL parser rejects them.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    long rows =
        insert(entity)
            .set(
                entity.name,
                Expressions.stringTemplate("upper({0})", Expressions.constant("hello")))
            .execute();

    assertThat(rows).isEqualTo(1L);

    var stored =
        (String)
            entityManager
                .createNativeQuery("select name_ from generated_key_entity")
                .getSingleResult();
    assertThat(stored).isEqualTo("HELLO");
  }

  @Test
  public void execute_multi_row_without_keys_inserts_all_rows() {
    // #1692 follow-up: addRow() must also work without returning keys — a plain
    // execute() should emit a single multi-row INSERT and report all affected rows.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    long rows =
        insert(entity)
            .columns(entity.name)
            .values("MR-A")
            .addRow()
            .values("MR-B")
            .addRow()
            .values("MR-C")
            .execute();

    assertThat(rows).isEqualTo(3L);

    var count =
        (Number)
            entityManager
                .createNativeQuery(
                    "select count(*) from generated_key_entity where name_ like 'MR-%'")
                .getSingleResult();
    assertThat(count.longValue()).isEqualTo(3L);
  }

  @Test
  public void execute_multi_row_in_a_loop_with_trailing_addRow() {
    // The loop-friendly shape: every iteration appends a row, no "is this the first row?"
    // bookkeeping, and a trailing addRow() is fine.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var clause = insert(entity).columns(entity.name);
    for (var name : new String[] {"Loop1", "Loop2", "Loop3", "Loop4"}) {
      clause.values(name).addRow();
    }
    long rows = clause.execute();

    assertThat(rows).isEqualTo(4L);
  }

  @Test
  public void execute_multi_row_set_style_with_trailing_addRow() {
    // The loop-friendly shape for set()-style: every iteration calls set()...addRow().
    // After the loop, inserts/columns are both empty (set() goes into inserts which addRow()
    // clears), but addRow() captures the column paths on its first call so executors can
    // recover the column list. No "first row" flag, no buffer-retention trick.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var insert = insert(entity);
    for (var name : new String[] {"Set1", "Set2", "Set3"}) {
      insert.set(entity.name, name).addRow();
    }
    long rows = insert.execute();

    assertThat(rows).isEqualTo(3L);
  }

  @Test
  public void executeWithKeys_multi_row_set_style_with_trailing_addRow() {
    // Same loop-friendly shape but routed through executeWithKeys to return generated keys.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var insert = insert(entity);
    for (var name : new String[] {"KSet1", "KSet2"}) {
      insert.set(entity.name, name).addRow();
    }
    var keys = insert.executeWithKeys(entity.id);

    assertThat(keys).hasSize(2);
    assertThat(keys.get(0)).isLessThan(keys.get(1));
  }

  @Test
  public void enum_string_column_stored_as_name() {
    // #1883: @Enumerated(EnumType.STRING) fields should be bound as enum.name()
    // on the native path, not left to driver behavior.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id =
        insert(entity)
            .set(entity.name, "enum-string")
            .set(entity.statusString, GeneratedKeyEntity.Status.ACTIVE)
            .executeWithKey(entity.id);

    assertThat(id).isNotNull();
    var stored =
        (String)
            entityManager
                .createNativeQuery("select status_string_ from generated_key_entity where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
    assertThat(stored).isEqualTo("ACTIVE");
  }

  @Test
  public void enum_ordinal_column_stored_as_ordinal() {
    // #1883: @Enumerated(EnumType.ORDINAL) fields should be bound as enum.ordinal().
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id =
        insert(entity)
            .set(entity.name, "enum-ordinal")
            .set(entity.statusOrdinal, GeneratedKeyEntity.Status.DONE)
            .executeWithKey(entity.id);

    assertThat(id).isNotNull();
    var stored =
        (Number)
            entityManager
                .createNativeQuery("select status_ordinal_ from generated_key_entity where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
    assertThat(stored.intValue()).isEqualTo(GeneratedKeyEntity.Status.DONE.ordinal());
  }

  @Test
  public void enum_default_annotation_stored_as_ordinal() {
    // #1883: An enum field without @Enumerated defaults to ORDINAL per JPA spec.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id =
        insert(entity)
            .set(entity.name, "enum-default")
            .set(entity.statusDefault, GeneratedKeyEntity.Status.PENDING)
            .executeWithKey(entity.id);

    assertThat(id).isNotNull();
    var stored =
        (Number)
            entityManager
                .createNativeQuery("select status_default_ from generated_key_entity where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
    assertThat(stored.intValue()).isEqualTo(GeneratedKeyEntity.Status.PENDING.ordinal());
  }

  @Test
  public void enum_with_convert_fails_fast() {
    // #1883: @Convert on an enum field cannot be honored on the native path
    // (which bypasses JPA). Fail-fast with a clear message pointing the caller
    // to convert the value at the call site.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    assertThatThrownBy(
            () ->
                insert(entity)
                    .set(entity.name, "enum-convert")
                    .set(entity.statusConverted, GeneratedKeyEntity.Status.ACTIVE)
                    .executeWithKey(entity.id))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("@Convert")
        .hasMessageContaining("convertToDatabaseColumn");
  }

  @Test
  public void enum_with_convert_still_works_when_value_pre_converted_at_call_site() {
    // #1883: The documented workaround for @Convert enum fields is to convert the value
    // at the call site. Cast the strongly-typed EnumPath to Path<String> to satisfy the
    // compiler, then bind the converter output directly.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    @SuppressWarnings({"unchecked", "rawtypes"})
    var statusConvertedAsString =
        (com.querydsl.core.types.Path<String>)
            (com.querydsl.core.types.Path) entity.statusConverted;
    var converter = new GeneratedKeyStatusCodeConverter();
    Long id =
        insert(entity)
            .set(entity.name, "enum-convert-workaround")
            .set(
                statusConvertedAsString,
                converter.convertToDatabaseColumn(GeneratedKeyEntity.Status.DONE))
            .executeWithKey(entity.id);

    assertThat(id).isNotNull();
    var stored =
        (String)
            entityManager
                .createNativeQuery(
                    "select status_converted_ from generated_key_entity where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
    assertThat(stored).isEqualTo("code_DONE");
  }

  @Test
  public void enum_multi_row_with_addRow_stores_all_rows_correctly() {
    // #1883: Enum conversion must apply to every row of a multi-row INSERT.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    long rows =
        insert(entity)
            .set(entity.name, "mr-1")
            .set(entity.statusString, GeneratedKeyEntity.Status.PENDING)
            .addRow()
            .set(entity.name, "mr-2")
            .set(entity.statusString, GeneratedKeyEntity.Status.ACTIVE)
            .addRow()
            .set(entity.name, "mr-3")
            .set(entity.statusString, GeneratedKeyEntity.Status.DONE)
            .execute();

    assertThat(rows).isEqualTo(3L);

    @SuppressWarnings("unchecked")
    var stored =
        (java.util.List<String>)
            entityManager
                .createNativeQuery(
                    "select status_string_ from generated_key_entity where name_ like 'mr-%'"
                        + " order by name_")
                .getResultList();
    assertThat(stored).containsExactly("PENDING", "ACTIVE", "DONE");
  }

  @Test
  public void enum_ordinal_multi_row_with_addRow_stores_numeric_values() {
    // #1883: Enum conversion for @Enumerated(EnumType.ORDINAL) must also apply
    // across multi-row INSERTs — every row is bound as enum.ordinal().
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    long rows =
        insert(entity)
            .set(entity.name, "ord-1")
            .set(entity.statusOrdinal, GeneratedKeyEntity.Status.PENDING)
            .addRow()
            .set(entity.name, "ord-2")
            .set(entity.statusOrdinal, GeneratedKeyEntity.Status.ACTIVE)
            .addRow()
            .set(entity.name, "ord-3")
            .set(entity.statusOrdinal, GeneratedKeyEntity.Status.DONE)
            .execute();

    assertThat(rows).isEqualTo(3L);

    @SuppressWarnings("unchecked")
    var stored =
        (java.util.List<Number>)
            entityManager
                .createNativeQuery(
                    "select status_ordinal_ from generated_key_entity where name_ like 'ord-%'"
                        + " order by name_")
                .getResultList();
    assertThat(stored)
        .extracting(Number::intValue)
        .containsExactly(
            GeneratedKeyEntity.Status.PENDING.ordinal(),
            GeneratedKeyEntity.Status.ACTIVE.ordinal(),
            GeneratedKeyEntity.Status.DONE.ordinal());
  }

  @Test
  public void enum_ordinal_columns_values_style_also_converts_to_ordinal() {
    // #1883: Enum conversion for @Enumerated(EnumType.ORDINAL) must apply to
    // columns()/values() style as well as set() style.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id =
        insert(entity)
            .columns(entity.name, entity.statusOrdinal)
            .values("cv-ordinal", GeneratedKeyEntity.Status.ACTIVE)
            .executeWithKey(entity.id);

    assertThat(id).isNotNull();
    var stored =
        (Number)
            entityManager
                .createNativeQuery("select status_ordinal_ from generated_key_entity where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
    assertThat(stored.intValue()).isEqualTo(GeneratedKeyEntity.Status.ACTIVE.ordinal());
  }

  @Test
  public void priority_ordinal_stored_as_numeric_position() {
    // #1883: Any enum mapped with EnumType.ORDINAL — not just Status — is bound
    // as its integer position, verifying the conversion is generic across enums.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id =
        insert(entity)
            .set(entity.name, "prio-ordinal")
            .set(entity.priorityOrdinal, GeneratedKeyEntity.Priority.CRITICAL)
            .executeWithKey(entity.id);

    assertThat(id).isNotNull();
    var stored =
        (Number)
            entityManager
                .createNativeQuery(
                    "select priority_ordinal_ from generated_key_entity where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
    // CRITICAL is the 4th constant (index 3) — this is the value that lands in the DB.
    assertThat(stored.intValue()).isEqualTo(3);
    assertThat(stored.intValue()).isEqualTo(GeneratedKeyEntity.Priority.CRITICAL.ordinal());
  }

  @Test
  public void priority_string_stored_as_name() {
    // #1883: The same Priority enum mapped with EnumType.STRING stores its name(),
    // verifying the STRING branch also works for enums beyond Status.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id =
        insert(entity)
            .set(entity.name, "prio-string")
            .set(entity.priorityString, GeneratedKeyEntity.Priority.MEDIUM)
            .executeWithKey(entity.id);

    assertThat(id).isNotNull();
    var stored =
        (String)
            entityManager
                .createNativeQuery(
                    "select priority_string_ from generated_key_entity where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
    assertThat(stored).isEqualTo("MEDIUM");
  }

  @Test
  public void enum_mixed_string_and_ordinal_in_same_row() {
    // #1883: STRING and ORDINAL enum columns can coexist in one INSERT and each
    // must be converted according to its own annotation.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id =
        insert(entity)
            .set(entity.name, "mixed")
            .set(entity.statusString, GeneratedKeyEntity.Status.PENDING)
            .set(entity.statusOrdinal, GeneratedKeyEntity.Status.DONE)
            .executeWithKey(entity.id);

    assertThat(id).isNotNull();
    var stringVal =
        (String)
            entityManager
                .createNativeQuery("select status_string_ from generated_key_entity where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
    var ordinalVal =
        (Number)
            entityManager
                .createNativeQuery("select status_ordinal_ from generated_key_entity where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
    assertThat(stringVal).isEqualTo("PENDING");
    assertThat(ordinalVal.intValue()).isEqualTo(GeneratedKeyEntity.Status.DONE.ordinal());
  }

  @Test
  public void enum_columns_values_style_also_converts() {
    // #1883: Enum conversion must apply to columns()/values() style, not only set() style.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    Long id =
        insert(entity)
            .columns(entity.name, entity.statusString)
            .values("cv-style", GeneratedKeyEntity.Status.ACTIVE)
            .executeWithKey(entity.id);

    assertThat(id).isNotNull();
    var stored =
        (String)
            entityManager
                .createNativeQuery("select status_string_ from generated_key_entity where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
    assertThat(stored).isEqualTo("ACTIVE");
  }

  @Test
  public void executeWithKeys_single_row_with_enum_returns_key_and_stores_name() {
    // #1883: executeWithKeys() single-row path must also apply enum conversion.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var keys =
        insert(entity)
            .set(entity.name, "ewks-single")
            .set(entity.statusString, GeneratedKeyEntity.Status.ACTIVE)
            .set(entity.statusOrdinal, GeneratedKeyEntity.Status.DONE)
            .executeWithKeys(entity.id);

    assertThat(keys).hasSize(1);
    var id = keys.get(0);
    var stringVal =
        (String)
            entityManager
                .createNativeQuery("select status_string_ from generated_key_entity where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
    var ordinalVal =
        (Number)
            entityManager
                .createNativeQuery("select status_ordinal_ from generated_key_entity where id = ?1")
                .setParameter(1, id)
                .getSingleResult();
    assertThat(stringVal).isEqualTo("ACTIVE");
    assertThat(ordinalVal.intValue()).isEqualTo(GeneratedKeyEntity.Status.DONE.ordinal());
  }

  @Test
  public void executeWithKeys_multi_row_addRow_with_enum_returns_all_keys_and_stores_correctly() {
    // #1883: executeWithKeys() multi-row (addRow) path must apply enum conversion
    // to every row and return one key per inserted row.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var keys =
        insert(entity)
            .set(entity.name, "ewks-mr-1")
            .set(entity.statusString, GeneratedKeyEntity.Status.PENDING)
            .set(entity.statusOrdinal, GeneratedKeyEntity.Status.PENDING)
            .addRow()
            .set(entity.name, "ewks-mr-2")
            .set(entity.statusString, GeneratedKeyEntity.Status.ACTIVE)
            .set(entity.statusOrdinal, GeneratedKeyEntity.Status.ACTIVE)
            .addRow()
            .set(entity.name, "ewks-mr-3")
            .set(entity.statusString, GeneratedKeyEntity.Status.DONE)
            .set(entity.statusOrdinal, GeneratedKeyEntity.Status.DONE)
            .executeWithKeys(entity.id);

    assertThat(keys).hasSize(3);
    assertThat(keys.get(0)).isLessThan(keys.get(1));
    assertThat(keys.get(1)).isLessThan(keys.get(2));

    @SuppressWarnings("unchecked")
    var storedStrings =
        (java.util.List<String>)
            entityManager
                .createNativeQuery(
                    "select status_string_ from generated_key_entity where name_ like 'ewks-mr-%'"
                        + " order by name_")
                .getResultList();
    @SuppressWarnings("unchecked")
    var storedOrdinals =
        (java.util.List<Number>)
            entityManager
                .createNativeQuery(
                    "select status_ordinal_ from generated_key_entity where name_ like 'ewks-mr-%'"
                        + " order by name_")
                .getResultList();
    assertThat(storedStrings).containsExactly("PENDING", "ACTIVE", "DONE");
    assertThat(storedOrdinals)
        .extracting(Number::intValue)
        .containsExactly(
            GeneratedKeyEntity.Status.PENDING.ordinal(),
            GeneratedKeyEntity.Status.ACTIVE.ordinal(),
            GeneratedKeyEntity.Status.DONE.ordinal());
  }

  @Test
  public void execute_without_template_uses_jpql_path() {
    // Regression for #1757: plain value INSERTs must keep using the JPQL path so
    // JPA semantics (cascade, callbacks where applicable) are preserved.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    long rows = insert(entity).set(entity.name, "plain").execute();

    assertThat(rows).isEqualTo(1L);

    var stored =
        (String)
            entityManager
                .createNativeQuery("select name_ from generated_key_entity")
                .getSingleResult();
    assertThat(stored).isEqualTo("plain");
  }
}
