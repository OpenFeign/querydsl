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

  private static final float[] QUERY = {1, 0, 0.5f};

  private static final QDocument document = new QDocument("d");

  @Test
  void pgvector() {
    SQLTemplates templates = new PGvectorTemplates();
    var e = document.embedding;

    assertThat(select(templates, e.l2Distance(QUERY))).isEqualTo("(d.embedding <-> ?)");
    assertThat(select(templates, e.l2SquaredDistance(QUERY)))
        .isEqualTo("((d.embedding <-> ?) ^ 2)");
    assertThat(select(templates, e.cosineDistance(QUERY))).isEqualTo("(d.embedding <=> ?)");
    assertThat(select(templates, e.innerProduct(QUERY))).isEqualTo("((d.embedding <#> ?) * -1)");
    assertThat(select(templates, e.negativeInnerProduct(QUERY))).isEqualTo("(d.embedding <#> ?)");
    assertThat(select(templates, e.l1Distance(QUERY))).isEqualTo("(d.embedding <+> ?)");
    assertThat(select(templates, e.dims())).isEqualTo("vector_dims(d.embedding)");
    assertThat(select(templates, e.norm())).isEqualTo("vector_norm(d.embedding)");
    assertThat(PGvectorType.DEFAULT.getLiteral(QUERY)).isEqualTo("'[1.0,0.0,0.5]'::vector");
  }

  @Test
  void oracle() {
    SQLTemplates templates = new OracleVectorTemplates();
    var e = document.embedding;

    assertThat(select(templates, e.l2Distance(QUERY)))
        .isEqualTo("vector_distance(d.embedding, ?, EUCLIDEAN)");
    assertThat(select(templates, e.l2SquaredDistance(QUERY)))
        .isEqualTo("vector_distance(d.embedding, ?, EUCLIDEAN_SQUARED)");
    assertThat(select(templates, e.cosineDistance(QUERY)))
        .isEqualTo("vector_distance(d.embedding, ?, COSINE)");
    assertThat(select(templates, e.innerProduct(QUERY)))
        .isEqualTo("(vector_distance(d.embedding, ?, DOT) * -1)");
    assertThat(select(templates, e.negativeInnerProduct(QUERY)))
        .isEqualTo("vector_distance(d.embedding, ?, DOT)");
    assertThat(select(templates, e.l1Distance(QUERY)))
        .isEqualTo("vector_distance(d.embedding, ?, MANHATTAN)");
    assertThat(select(templates, e.dims())).isEqualTo("vector_dimension_count(d.embedding)");
    assertThat(select(templates, e.norm())).isEqualTo("vector_norm(d.embedding)");
    assertThat(OracleVectorType.DEFAULT.getLiteral(QUERY)).isEqualTo("TO_VECTOR('[1.0,0.0,0.5]')");
  }

  @Test
  void parseVectorText() {
    assertThat(VectorText.parse("[1,-2.5, 3e-2]")).containsExactly(1f, -2.5f, 0.03f);
    assertThat(VectorText.parse("[]")).isEmpty();
  }

  private static String select(SQLTemplates templates, NumberExpression<?> expr) {
    var sql =
        new SQLQuery<Void>(new Configuration(templates))
            .select(expr)
            .from(document)
            .getSQL()
            .getSQL();
    return sql.substring("select ".length(), sql.indexOf("\nfrom"));
  }
}
