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
package com.querydsl.sql.vector;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLTemplates;
import org.junit.jupiter.api.Test;

class VectorTemplatesTest {

  private static final float[] QUERY_EMBEDDING = {1, 0, 0.5f};

  private static final QDocument document = new QDocument("d");

  @Test
  void rendersPgvectorOperatorsAndFunctionsAsSql() {
    SQLTemplates pgvectorTemplates = new PGvectorTemplates();
    var embedding = document.embedding;

    assertThat(renderSelectClauseSql(pgvectorTemplates, embedding.l2Distance(QUERY_EMBEDDING)))
        .isEqualTo("(d.embedding <-> ?)");
    assertThat(
            renderSelectClauseSql(pgvectorTemplates, embedding.l2SquaredDistance(QUERY_EMBEDDING)))
        .isEqualTo("((d.embedding <-> ?) ^ 2)");
    assertThat(renderSelectClauseSql(pgvectorTemplates, embedding.cosineDistance(QUERY_EMBEDDING)))
        .isEqualTo("(d.embedding <=> ?)");
    assertThat(renderSelectClauseSql(pgvectorTemplates, embedding.innerProduct(QUERY_EMBEDDING)))
        .isEqualTo("((d.embedding <#> ?) * -1)");
    assertThat(
            renderSelectClauseSql(
                pgvectorTemplates, embedding.negativeInnerProduct(QUERY_EMBEDDING)))
        .isEqualTo("(d.embedding <#> ?)");
    assertThat(renderSelectClauseSql(pgvectorTemplates, embedding.l1Distance(QUERY_EMBEDDING)))
        .isEqualTo("(d.embedding <+> ?)");
    assertThat(renderSelectClauseSql(pgvectorTemplates, embedding.dimensionCount()))
        .isEqualTo("vector_dims(d.embedding)");
    assertThat(renderSelectClauseSql(pgvectorTemplates, embedding.l2Norm()))
        .isEqualTo("vector_norm(d.embedding)");
    assertThat(PGvectorType.DEFAULT.getLiteral(QUERY_EMBEDDING))
        .isEqualTo("'[1.0,0.0,0.5]'::vector");
  }

  @Test
  void rendersVectorExpressionsAsOracleSql() {
    SQLTemplates oracleVectorTemplates = new OracleVectorTemplates();
    var embedding = document.embedding;

    assertThat(renderSelectClauseSql(oracleVectorTemplates, embedding.l2Distance(QUERY_EMBEDDING)))
        .isEqualTo("vector_distance(d.embedding, ?, EUCLIDEAN)");
    assertThat(
            renderSelectClauseSql(
                oracleVectorTemplates, embedding.l2SquaredDistance(QUERY_EMBEDDING)))
        .isEqualTo("vector_distance(d.embedding, ?, EUCLIDEAN_SQUARED)");
    assertThat(
            renderSelectClauseSql(oracleVectorTemplates, embedding.cosineDistance(QUERY_EMBEDDING)))
        .isEqualTo("vector_distance(d.embedding, ?, COSINE)");
    assertThat(
            renderSelectClauseSql(oracleVectorTemplates, embedding.innerProduct(QUERY_EMBEDDING)))
        .isEqualTo("(vector_distance(d.embedding, ?, DOT) * -1)");
    assertThat(
            renderSelectClauseSql(
                oracleVectorTemplates, embedding.negativeInnerProduct(QUERY_EMBEDDING)))
        .isEqualTo("vector_distance(d.embedding, ?, DOT)");
    assertThat(renderSelectClauseSql(oracleVectorTemplates, embedding.l1Distance(QUERY_EMBEDDING)))
        .isEqualTo("vector_distance(d.embedding, ?, MANHATTAN)");
    assertThat(renderSelectClauseSql(oracleVectorTemplates, embedding.dimensionCount()))
        .isEqualTo("vector_dimension_count(d.embedding)");
    assertThat(renderSelectClauseSql(oracleVectorTemplates, embedding.l2Norm()))
        .isEqualTo("vector_norm(d.embedding)");
    assertThat(OracleVectorType.DEFAULT.getLiteral(QUERY_EMBEDDING))
        .isEqualTo("TO_VECTOR('[1.0,0.0,0.5]')");
  }

  @Test
  void parseVectorText() {
    assertThat(VectorText.parse("[1,-2.5, 3e-2]")).containsExactly(1f, -2.5f, 0.03f);
    assertThat(VectorText.parse("[]")).isEmpty();
  }

  private static String renderSelectClauseSql(
      SQLTemplates templates, NumberExpression<?> expression) {
    var sql =
        new SQLQuery<Void>(new Configuration(templates))
            .select(expression)
            .from(document)
            .getSQL()
            .getSQL();
    return sql.substring("select ".length(), sql.indexOf("\nfrom"));
  }
}
