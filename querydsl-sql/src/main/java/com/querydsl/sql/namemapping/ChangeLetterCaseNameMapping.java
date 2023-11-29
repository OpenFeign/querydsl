/*
 * Copyright 2018, The Querydsl Team (http://www.querydsl.com/team)
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
package com.querydsl.sql.namemapping;

import com.querydsl.sql.SchemaAndTable;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Simple implementation of {@link NameMapping} that changes the letter-case (lower-case or
 * upper-case) of the schema, table and column names. The information how the database stores the
 * identifiers are available normally from the <code>stores*Identifiers</code> function of the
 * {@link java.sql.DatabaseMetaData}
 */
public class ChangeLetterCaseNameMapping implements NameMapping {

  /**
   * The target character-case (lower or upper) that the {@link ChangeLetterCaseNameMapping} should
   * use to convert the identifiers names.
   */
  public enum LetterCase {
    LOWER,
    UPPER
  }

  private Locale locale;

  private final LetterCase targetCase;

  /**
   * Constructor.
   *
   * @param targetCase The characters of all table and column names will be converted to the
   *     specified letter-case.
   * @param locale The locale that is used for the letter-case conversion.
   */
  public ChangeLetterCaseNameMapping(LetterCase targetCase, Locale locale) {
    this.locale = Objects.requireNonNull(locale);
    this.targetCase = Objects.requireNonNull(targetCase);
  }

  @Override
  public Optional<String> getColumnOverride(SchemaAndTable key, String column) {
    return Optional.ofNullable(targetCaseOrNull(column));
  }

  @Override
  public Optional<SchemaAndTable> getOverride(SchemaAndTable key) {
    return Optional.of(
        new SchemaAndTable(targetCaseOrNull(key.getSchema()), targetCaseOrNull(key.getTable())));
  }

  private String targetCaseOrNull(String text) {
    if (targetCase == LetterCase.LOWER) {
      return text.toLowerCase(locale);
    } else {
      return text.toUpperCase(locale);
    }
  }
}
