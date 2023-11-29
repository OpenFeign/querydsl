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
package com.querydsl.jdo;

import static org.junit.Assert.*;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jdo.sql.JDOSQLQuery;
import com.querydsl.jdo.test.domain.Product;
import com.querydsl.jdo.test.domain.sql.SProduct;
import com.querydsl.sql.HSQLDBTemplates;
import com.querydsl.sql.SQLTemplates;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class JDOSQLQueryTest extends AbstractJDOTest {

  private final SQLTemplates sqlTemplates = new HSQLDBTemplates();

  private final SProduct product = SProduct.product;

  protected JDOSQLQuery<?> sql() {
    return new JDOSQLQuery<Void>(pm, sqlTemplates);
  }

  @Test
  public void count() {
    assertEquals(30L, sql().from(product).fetchCount());
  }

  @Test(expected = NonUniqueResultException.class)
  public void uniqueResult() {
    sql().from(product).orderBy(product.name.asc()).select(product.name).fetchOne();
  }

  @Test
  public void singleResult() {
    assertEquals(
        "A0", sql().from(product).orderBy(product.name.asc()).select(product.name).fetchFirst());
  }

  @Test
  public void singleResult_with_array() {
    assertEquals(
        "A0",
        sql()
            .from(product)
            .orderBy(product.name.asc())
            .select(new Expression<?>[] {product.name})
            .fetchFirst()
            .get(product.name));
  }

  @Test
  public void startsWith_count() {
    assertEquals(10L, sql().from(product).where(product.name.startsWith("A")).fetchCount());
    assertEquals(10L, sql().from(product).where(product.name.startsWith("B")).fetchCount());
    assertEquals(10L, sql().from(product).where(product.name.startsWith("C")).fetchCount());
  }

  @Test
  public void eq_count() {
    for (int i = 0; i < 10; i++) {
      assertEquals(1L, sql().from(product).where(product.name.eq("A" + i)).fetchCount());
      assertEquals(1L, sql().from(product).where(product.name.eq("B" + i)).fetchCount());
      assertEquals(1L, sql().from(product).where(product.name.eq("C" + i)).fetchCount());
    }
  }

  @Test
  public void scalarQueries() {
    BooleanExpression filter = product.name.startsWith("A");

    // fetchCount
    assertEquals(10L, sql().from(product).where(filter).fetchCount());

    // countDistinct
    assertEquals(10L, sql().from(product).where(filter).distinct().fetchCount());

    // fetch
    assertEquals(10, sql().from(product).where(filter).select(product.name).fetch().size());

    // fetch with limit
    assertEquals(3, sql().from(product).limit(3).select(product.name).fetch().size());

    // fetch with offset
    //        assertEquals(7, sql().from(product).offset(3).fetch(product.name).size());

    // fetch with limit and offset
    assertEquals(3, sql().from(product).offset(3).limit(3).select(product.name).fetch().size());

    // fetch multiple
    for (Tuple row :
        sql().from(product).select(product.productId, product.name, product.amount).fetch()) {
      assertNotNull(row.get(0, Object.class));
      assertNotNull(row.get(1, Object.class));
      assertNotNull(row.get(2, Object.class));
    }

    // fetchResults
    QueryResults<String> results = sql().from(product).limit(3).select(product.name).fetchResults();
    assertEquals(3, results.getResults().size());
    assertEquals(30L, results.getTotal());
  }

  @Ignore
  @Test
  @SuppressWarnings("unchecked")
  public void union() throws SQLException {
    SubQueryExpression<Integer> sq1 = sql().from(product).select(product.amount.max());
    SubQueryExpression<Integer> sq2 = sql().from(product).select(product.amount.min());
    List<Integer> list = sql().union(sq1, sq2).list();
    assertFalse(list.isEmpty());
  }

  @Ignore
  @Test
  @SuppressWarnings("unchecked")
  public void union_all() {
    SubQueryExpression<Integer> sq1 = sql().from(product).select(product.amount.max());
    SubQueryExpression<Integer> sq2 = sql().from(product).select(product.amount.min());
    List<Integer> list = sql().unionAll(sq1, sq2).list();
    assertFalse(list.isEmpty());
  }

  @Test
  public void entityProjections() {
    List<Product> products =
        sql()
            .from(product)
            .select(
                Projections.constructor(
                    Product.class,
                    product.name,
                    product.description,
                    product.price,
                    product.amount))
            .fetch();
    assertEquals(30, products.size());
    for (Product p : products) {
      assertNotNull(p.getName());
      assertNotNull(p.getDescription());
      assertNotNull(p.getPrice());
      assertNotNull(p.getAmount());
    }
  }

  @BeforeClass
  public static void doPersist() {
    List<Object> entities = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      entities.add(new Product("C" + i, "F", 200.00, 2));
      entities.add(new Product("B" + i, "E", 400.00, 4));
      entities.add(new Product("A" + i, "D", 600.00, 6));
    }
    doPersist(entities);
  }
}
