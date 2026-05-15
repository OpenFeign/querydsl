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

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.domain.Author;
import com.querydsl.jpa.domain.GeneratedKeyEntity;
import com.querydsl.jpa.domain.Numeric;
import com.querydsl.jpa.domain.QAuthor;
import com.querydsl.jpa.domain.QGeneratedKeyEntity;
import com.querydsl.jpa.domain.QNumeric;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLTemplates;
import java.util.List;
import org.junit.Test;

public class JpaNativeInsertSerializerTest {

  private JpaNativeInsertSerializer newSerializer(SQLTemplates templates) {
    return new JpaNativeInsertSerializer(new Configuration(templates));
  }

  @Test
  public void serializeInsert_with_table_and_column_annotations() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var serializer = newSerializer(SQLTemplates.DEFAULT);

    serializer.serializeInsert(
        GeneratedKeyEntity.class, List.of(entity.name), List.of(Expressions.constant("value")));

    assertThat(serializer.toString())
        .isEqualToIgnoringCase("insert into generated_key_entity (name_)\nvalues (?)");
    assertThat(serializer.getConstants()).containsExactly("value");
  }

  @Test
  public void serializeInsert_falls_back_to_simple_class_name() {
    // Author has @Table(name = "author_")
    var author = QAuthor.author;
    var serializer = newSerializer(SQLTemplates.DEFAULT);

    serializer.serializeInsert(
        Author.class, List.of(author.name), List.of(Expressions.constant("Tolkien")));

    assertThat(serializer.toString())
        .isEqualToIgnoringCase("insert into author_ (name)\nvalues (?)");
  }

  @Test
  public void serializeInsert_multiple_columns() {
    var numeric = QNumeric.numeric;
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var serializer = newSerializer(SQLTemplates.DEFAULT);

    serializer.serializeInsert(
        Numeric.class,
        List.of(numeric.value),
        List.of(Expressions.constant(new java.math.BigDecimal("1.23"))));

    assertThat(serializer.toString())
        .isEqualToIgnoringCase("insert into numeric_ (value_)\nvalues (?)");
    assertThat(serializer.getConstants()).hasSize(1);
  }

  @Test
  public void serializeInsert_preserves_function_template() {
    // Regression: function templates (e.g. UPPER({0}), dbo.encrypt({0})) used to be silently
    // dropped, leaving only the inner constant bound as a plain ? placeholder.
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var serializer = newSerializer(SQLTemplates.DEFAULT);

    Expression<String> wrapped =
        Expressions.stringTemplate("upper({0})", Expressions.constant("value"));
    serializer.serializeInsert(GeneratedKeyEntity.class, List.of(entity.name), List.of(wrapped));

    assertThat(serializer.toString())
        .isEqualToIgnoringCase("insert into generated_key_entity (name_)\nvalues (upper(?))");
    assertThat(serializer.getConstants()).containsExactly("value");
  }

  @Test
  public void serializeInsert_supports_zero_arg_function_template() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var serializer = newSerializer(SQLTemplates.DEFAULT);

    Expression<String> nowFn = Expressions.stringTemplate("current_timestamp");
    serializer.serializeInsert(GeneratedKeyEntity.class, List.of(entity.name), List.of(nowFn));

    assertThat(serializer.toString())
        .isEqualToIgnoringCase(
            "insert into generated_key_entity (name_)\nvalues (current_timestamp)");
    // No bind values needed for a literal function call
    assertThat(serializer.getConstants()).isEmpty();
  }

  @Test
  public void serializeInsert_with_quoting_templates() {
    // Custom SQLTemplates that always quotes identifiers
    var alwaysQuote = new SQLTemplates("\"", '\\', true) {};
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var serializer = newSerializer(alwaysQuote);

    serializer.serializeInsert(
        GeneratedKeyEntity.class, List.of(entity.name), List.of(Expressions.constant("value")));

    assertThat(serializer.toString())
        .isEqualToIgnoringCase("insert into \"generated_key_entity\" (\"name_\")\nvalues (?)");
  }

  @Test
  public void serializeInsert_collects_constants_in_order() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var serializer = newSerializer(SQLTemplates.DEFAULT);

    Expression<String> first =
        Expressions.stringTemplate(
            "concat({0}, {1})", Expressions.constant("a"), Expressions.constant("b"));
    serializer.serializeInsert(GeneratedKeyEntity.class, List.of(entity.name), List.of(first));

    assertThat(serializer.getConstants()).containsExactly("a", "b");
  }

  @Test(expected = IllegalArgumentException.class)
  public void serializeInsert_rejects_mismatched_column_value_counts() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var serializer = newSerializer(SQLTemplates.DEFAULT);

    serializer.serializeInsert(
        GeneratedKeyEntity.class,
        List.of(entity.name),
        List.of(Expressions.constant("a"), Expressions.constant("b")));
  }
}
