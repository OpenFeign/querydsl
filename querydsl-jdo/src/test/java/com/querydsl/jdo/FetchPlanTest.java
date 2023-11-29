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

import static org.junit.Assert.assertEquals;

import com.querydsl.jdo.test.domain.Product;
import com.querydsl.jdo.test.domain.QProduct;
import com.querydsl.jdo.test.domain.QStore;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.jdo.Query;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class FetchPlanTest extends AbstractJDOTest {

  private JDOQuery<?> query;

  @After
  public void tearDown() {
    if (query != null) {
      query.close();
    }
    super.tearDown();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void listProducts() throws Exception {
    QProduct product = QProduct.product;
    query = query();
    query
        .from(product)
        .where(product.name.startsWith("A"))
        .addFetchGroup("myfetchgroup1")
        .addFetchGroup("myfetchgroup2")
        .setMaxFetchDepth(2)
        .select(product)
        .fetch();
    //        query.close();

    Field queriesField = AbstractJDOQuery.class.getDeclaredField("queries");
    queriesField.setAccessible(true);
    List<Query> queries = (List<Query>) queriesField.get(query);
    Query jdoQuery = queries.get(0);
    assertEquals(
        new HashSet<String>(Arrays.asList("myfetchgroup1", "myfetchgroup2")),
        jdoQuery.getFetchPlan().getGroups());
    assertEquals(2, jdoQuery.getFetchPlan().getMaxFetchDepth());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void listStores() throws Exception {
    QStore store = QStore.store;
    query = query();
    query.from(store).addFetchGroup("products").select(store).fetch();

    Field queriesField = AbstractJDOQuery.class.getDeclaredField("queries");
    queriesField.setAccessible(true);
    List<Query> queries = (List<Query>) queriesField.get(query);
    Query jdoQuery = queries.get(0);
    assertEquals(
        new HashSet<String>(Collections.singletonList("products")),
        jdoQuery.getFetchPlan().getGroups());
    assertEquals(1, jdoQuery.getFetchPlan().getMaxFetchDepth());
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
