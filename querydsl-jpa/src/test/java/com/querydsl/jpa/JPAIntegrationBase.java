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

import com.querydsl.jpa.impl.JPAProvider;
import com.querydsl.jpa.impl.JPAUtil;
import com.querydsl.jpa.testutil.JPATestRunner;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

@RunWith(JPATestRunner.class)
public class JPAIntegrationBase extends ParsingTest implements JPATest {

  @Rule @ClassRule public static TestRule targetRule = new TargetRule();

  @Rule @ClassRule public static TestRule hibernateOnly = new JPAProviderRule();

  private EntityManager em;

  private JPQLTemplates templates;

  @Override
  protected QueryHelper<?> query() {
    return new QueryHelper<Void>(templates) {
      @Override
      public void parse() {
        JPQLSerializer serializer = new JPQLSerializer(templates);
        serializer.serialize(getMetadata(), false, null);
        Query query = em.createQuery(serializer.toString());
        JPAUtil.setConstants(query, serializer.getConstants(), getMetadata().getParams());
        try {
          query.getResultList();
        } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e);
        }
      }
    };
  }

  @Override
  public void setEntityManager(EntityManager em) {
    this.em = em;
    this.templates = JPAProvider.getTemplates(em);
  }
}
