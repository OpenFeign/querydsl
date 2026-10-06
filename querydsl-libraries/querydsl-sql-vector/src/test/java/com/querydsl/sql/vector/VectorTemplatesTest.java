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
  void rendersVectorExpressionsAsDB2SqlWithConvertedParameters() {
    SQLTemplates db2VectorTemplates = new DB2VectorTemplates();
    var embedding = document.embedding;

    assertThat(renderSelectClauseSql(db2VectorTemplates, embedding.l2Distance(QUERY_EMBEDDING)))
        .isEqualTo("vector_distance(d.embedding, vector(?, 3, float32), EUCLIDEAN)");
    assertThat(renderSelectClauseSql(db2VectorTemplates, embedding.l1Distance(embedding)))
        .isEqualTo("vector_distance(d.embedding, d.embedding, MANHATTAN)");
    assertThat(renderSelectClauseSql(db2VectorTemplates, embedding.l2Norm()))
        .isEqualTo("vector_norm(d.embedding, EUCLIDEAN)");
    assertThat(DB2VectorType.DEFAULT.getLiteral(QUERY_EMBEDDING))
        .isEqualTo("vector('[1.0,0.0,0.5]', 3, float32)");
  }

  @Test
  void rendersVectorExpressionsAsSQLServerSql() {
    SQLTemplates sqlServerVectorTemplates = new SQLServerVectorTemplates();
    var embedding = document.embedding;

    assertThat(
            renderSelectClauseSql(sqlServerVectorTemplates, embedding.l2Distance(QUERY_EMBEDDING)))
        .isEqualTo("vector_distance('euclidean', d.embedding, ?)");
    assertThat(
            renderSelectClauseSql(
                sqlServerVectorTemplates, embedding.l2SquaredDistance(QUERY_EMBEDDING)))
        .isEqualTo("square(vector_distance('euclidean', d.embedding, ?))");
    assertThat(
            renderSelectClauseSql(
                sqlServerVectorTemplates, embedding.innerProduct(QUERY_EMBEDDING)))
        .isEqualTo("(vector_distance('dot', d.embedding, ?) * -1)");
    assertThat(renderSelectClauseSql(sqlServerVectorTemplates, embedding.dimensionCount()))
        .isEqualTo("vectorproperty(d.embedding, 'Dimensions')");
    assertThat(SQLServerVectorType.DEFAULT.getLiteral(QUERY_EMBEDDING))
        .isEqualTo("cast('[1.0,0.0,0.5]' as vector(3))");
  }

  @Test
  void rendersVectorExpressionsAsMariaDBSql() {
    SQLTemplates mariaDBVectorTemplates = new MariaDBVectorTemplates();
    var embedding = document.embedding;

    assertThat(
            renderSelectClauseSql(
                mariaDBVectorTemplates, embedding.cosineDistance(QUERY_EMBEDDING)))
        .isEqualTo("vec_distance_cosine(d.embedding, ?)");
    assertThat(
            renderSelectClauseSql(
                mariaDBVectorTemplates, embedding.l2SquaredDistance(QUERY_EMBEDDING)))
        .isEqualTo("power(vec_distance_euclidean(d.embedding, ?), 2)");
    assertThat(renderSelectClauseSql(mariaDBVectorTemplates, embedding.dimensionCount()))
        .isEqualTo("(length(d.embedding) div 4)");
    assertThat(MariaDBVectorType.DEFAULT.getLiteral(QUERY_EMBEDDING))
        .isEqualTo("vec_fromtext('[1.0,0.0,0.5]')");
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
