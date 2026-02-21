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

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.domain.QEmployee;
import com.querydsl.jpa.domain.QUser;
import com.querydsl.jpa.hibernate.HibernateQuery;
import org.junit.Test;

public class HibernateQueryTest {

  @Test
  public void clone_() {
    var cat = QCat.cat;
    var emptyBooleanBuilder = new BooleanBuilder();
    HibernateQuery<?> hq =
        new HibernateQuery<Void>().from(cat).where(cat.name.isNull().and(emptyBooleanBuilder));
    HibernateQuery<?> hq2 = hq.clone();
    assertThat(hq2).isNotNull();
  }

  @Test
  public void innerJoin() {
    HibernateQuery<?> hqlQuery = new HibernateQuery<Void>();
    var employee = QEmployee.employee;
    hqlQuery.from(employee);
    hqlQuery.innerJoin(employee.user, QUser.user);
    assertThat(hqlQuery.toString())
        .isEqualTo("select employee\nfrom Employee employee\n  inner join employee.user as user");
  }

  @Test
  public void cteWithNotMaterializedHint() {
    HibernateQuery<Void> query = new HibernateQuery<>();
    QCat felix = new QCat("felix");
    query
        .withNotMaterializedHint(
            felix,
            JPAExpressions.select(QCat.cat.bodyWeight.as(felix.bodyWeight))
                .from(QCat.cat)
                .where(QCat.cat.name.eq("Felix123")))
        .select(QCat.cat)
        .from(QCat.cat, felix)
        .where(QCat.cat.bodyWeight.gt(felix.bodyWeight));
    assertThat(query.toString())
        .isEqualTo(
            """
            with
            felix as not materialized (select cat.bodyWeight as bodyWeight
            from Cat cat
            where cat.name = ?1)
            select cat
            from Cat cat, felix felix
            where cat.bodyWeight > felix.bodyWeight""");
  }

  @Test
  public void multipleCtes() {
    HibernateQuery<Void> query = new HibernateQuery<>();
    QCat felix = new QCat("felix");
    QCat felixMates = new QCat("felixMates");

    query
        .with(
            felix,
            JPAExpressions.select(QCat.cat.id.as(felix.id))
                .from(QCat.cat)
                .where(QCat.cat.name.eq("Felix123")))
        .with(
            felixMates,
            JPAExpressions.select(QCat.cat.id.as(felixMates.id))
                .from(QCat.cat)
                .innerJoin(felix)
                .on(QCat.cat.mate.id.eq(felix.id)))
        .select(felixMates.id)
        .from(felixMates);
    assertThat(query.toString())
        .isEqualTo(
            """
              with
              felix as (select cat.id as id
              from Cat cat
              where cat.name = ?1),
              felixMates as (select cat.id as id
              from Cat cat
                inner join felix felix with cat.mate.id = felix.id)
              select felixMates.id
              from felixMates felixMates""");
  }
}
