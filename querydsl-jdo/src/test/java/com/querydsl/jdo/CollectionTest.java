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

import com.querydsl.jdo.test.domain.Book;
import com.querydsl.jdo.test.domain.Product;
import com.querydsl.jdo.test.domain.QProduct;
import com.querydsl.jdo.test.domain.QStore;
import java.util.Arrays;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class CollectionTest extends AbstractJDOTest {

  private final QStore store = QStore.store;

  @Test
  public void contains_key() {
    query(store, store.productsByName.containsKey("XXX"));
  }

  @Test
  public void contains_value() {
    Product product = query().from(QProduct.product).select(QProduct.product).fetch().get(0);
    query(store, store.productsByName.containsValue(product));
  }

  @Test
  @Ignore
  public void get() {
    query(store, store.products.get(0).name.isNotNull());
  }

  @Test
  public void isEmpty() {
    query(store, store.products.isEmpty());
  }

  @Test
  public void isNotEmpty() {
    query(store, store.products.isNotEmpty());
  }

  @Test
  public void size() {
    query(store, store.products.size().gt(0));
  }

  @Test
  public void collection_any() {
    query(store, store.products.any().name.eq("Sony Discman"));
  }

  @Test
  public void collection_any_and() {
    query(
        store,
        store.products.any().name.eq("Sony Discman").and(store.products.any().price.gt(10.0)));
  }

  @Test
  public void collection_any_count() {
    query().from(store).where(store.products.any().name.eq("Sony Discman")).fetchCount();
  }

  @Test
  @Ignore // Not supported
  public void collection_any_in_projection() {
    query().from(store).select(store.products.any()).fetch();
  }

  @BeforeClass
  public static void doPersist() {
    doPersist(
        Arrays.asList(
            new Product("Sony Discman", "A standard discman from Sony", 200.00, 3),
            new Book(
                "Lord of the Rings by Tolkien",
                "The classic story",
                49.99,
                5,
                "JRR Tolkien",
                "12345678",
                "MyBooks Factory")));
  }
}
