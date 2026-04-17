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

import com.querydsl.jpa.domain.Author;
import com.querydsl.jpa.domain.GeneratedKeyEntity;
import com.querydsl.jpa.domain.Numeric;
import com.querydsl.jpa.domain.QAuthor;
import com.querydsl.jpa.domain.QGeneratedKeyEntity;
import com.querydsl.jpa.domain.QNumeric;
import java.util.List;
import org.junit.Test;

public class JpaInsertNativeHelperTest {

  @Test
  public void resolveTableName_with_table_annotation() {
    // Author has @Table(name = "author_")
    assertThat(JpaInsertNativeHelper.resolveTableName(Author.class)).isEqualTo("author_");
  }

  @Test
  public void resolveTableName_with_generated_key_entity() {
    // GeneratedKeyEntity has @Table(name = "generated_key_entity")
    assertThat(JpaInsertNativeHelper.resolveTableName(GeneratedKeyEntity.class))
        .isEqualTo("generated_key_entity");
  }

  @Test
  public void resolveColumnName_with_column_annotation() {
    // Numeric.value has @Column(name = "value_")
    var numeric = QNumeric.numeric;
    assertThat(JpaInsertNativeHelper.resolveColumnName(numeric.value)).isEqualTo("value_");
  }

  @Test
  public void resolveColumnName_with_name_column() {
    // GeneratedKeyEntity.name has @Column(name = "name_")
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    assertThat(JpaInsertNativeHelper.resolveColumnName(entity.name)).isEqualTo("name_");
  }

  @Test
  public void resolveColumnName_without_column_annotation() {
    // Author.name has no @Column annotation
    var author = QAuthor.author;
    assertThat(JpaInsertNativeHelper.resolveColumnName(author.name)).isEqualTo("name");
  }

  @Test
  public void buildNativeInsertSQL() {
    var entity = QGeneratedKeyEntity.generatedKeyEntity;
    var sql =
        JpaInsertNativeHelper.buildNativeInsertSQL(GeneratedKeyEntity.class, List.of(entity.name));

    assertThat(sql).isEqualTo("INSERT INTO generated_key_entity (name_) VALUES (?)");
  }

  @Test
  public void buildNativeInsertSQL_multiple_columns() {
    var numeric = QNumeric.numeric;
    var sql = JpaInsertNativeHelper.buildNativeInsertSQL(Numeric.class, List.of(numeric.value));

    assertThat(sql).isEqualTo("INSERT INTO numeric_ (value_) VALUES (?)");
  }
}
