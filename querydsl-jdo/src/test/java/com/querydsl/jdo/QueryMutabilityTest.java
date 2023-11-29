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

import com.querydsl.core.QueryMutability;
import com.querydsl.jdo.test.domain.QProduct;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.junit.Test;

public class QueryMutabilityTest extends AbstractJDOTest {

  @Test
  public void queryMutability()
      throws IOException,
          SecurityException,
          IllegalArgumentException,
          NoSuchMethodException,
          IllegalAccessException,
          InvocationTargetException {
    QProduct product = QProduct.product;
    JDOQuery<?> query = query().from(product);
    new QueryMutability(query).test(product.name, product.description);
  }

  @Test
  public void clone_() {
    QProduct product = QProduct.product;
    JDOQuery<?> query = new JDOQuery<Void>().from(product).where(product.name.isNotNull());
    JDOQuery<?> query2 = query.clone(pm);
    assertEquals(query.getMetadata().getJoins(), query2.getMetadata().getJoins());
    assertEquals(query.getMetadata().getWhere(), query2.getMetadata().getWhere());
    query2.select(product).fetch();
  }
}
