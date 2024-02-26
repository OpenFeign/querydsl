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

import com.querydsl.core.support.Context;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.domain.QCompany;
import com.querydsl.jpa.domain.QDomesticCat;
import com.querydsl.jpa.domain.QEmployee;
import org.junit.Test;

public class JPACollectionAnyVisitorTest {

  private QCat cat = QCat.cat;

  @Test
  public void path() {
    assertThat(serialize(cat.kittens.any())).isEqualTo("cat_kittens_0");
  }

  @Test
  public void longer_path() {
    assertThat(serialize(cat.kittens.any().name)).isEqualTo("cat_kittens_0.name");
  }

  @Test
  public void nested_any_booleanOperation() {
    QCompany company = QCompany.company;
    Predicate predicate = company.departments.any().employees.any().firstName.eq("Bob");
    assertThat(serialize(predicate))
        .isEqualTo(
            """
            exists (select 1
            from company.departments as company_departments_0
              inner join company_departments_0.employees as company_departments_0_employees_1
            where company_departments_0_employees_1.firstName = ?1)\
            """);
  }

  @Test
  public void simple_booleanOperation() {
    Predicate predicate = cat.kittens.any().name.eq("Ruth123");
    assertThat(serialize(predicate))
        .isEqualTo(
            """
            exists (select 1
            from cat.kittens as cat_kittens_0
            where cat_kittens_0.name = ?1)\
            """);
  }

  @Test
  public void simple_booleanOperation_longPath() {
    Predicate predicate = cat.kittens.any().kittens.any().name.eq("Ruth123");
    assertThat(serialize(predicate))
        .isEqualTo(
            """
            exists (select 1
            from cat.kittens as cat_kittens_0
              inner join cat_kittens_0.kittens as cat_kittens_0_kittens_1
            where cat_kittens_0_kittens_1.name = ?1)\
            """);
  }

  @Test
  public void simple_booleanOperation_elementCollection() {
    QEmployee employee = QEmployee.employee;
    Predicate predicate = employee.jobFunctions.any().stringValue().eq("CODER");
    assertThat(serialize(predicate))
        .isEqualTo(
            """
            exists (select 1
            from Employee employee_1463394548
              inner join employee_1463394548.jobFunctions as employee_jobFunctions_0
            where employee_1463394548 = employee and str(employee_jobFunctions_0) = ?1)\
            """);
  }

  @Test
  public void simple_stringOperation() {
    Predicate predicate = cat.kittens.any().name.substring(1).eq("uth123");
    assertThat(serialize(predicate))
        .isEqualTo(
            """
            exists (select 1
            from cat.kittens as cat_kittens_0
            where substring(cat_kittens_0.name,2) = ?1)\
            """);
  }

  @Test
  public void and_operation() {
    Predicate predicate =
        cat.kittens.any().name.eq("Ruth123").and(cat.kittens.any().bodyWeight.gt(10.0));
    assertThat(serialize(predicate))
        .isEqualTo(
            """
            exists (select 1
            from cat.kittens as cat_kittens_0
            where cat_kittens_0.name = ?1) and exists (select 1
            from cat.kittens as cat_kittens_1
            where cat_kittens_1.bodyWeight > ?2)\
            """);
  }

  @Test
  public void template() {
    Expression<Boolean> templateExpr =
        ExpressionUtils.template(
            Boolean.class, "{0} = {1}", cat.kittens.any().name, ConstantImpl.create("Ruth123"));
    assertThat(serialize(templateExpr))
        .isEqualTo(
            """
            exists (select 1
            from cat.kittens as cat_kittens_0
            where cat_kittens_0.name = ?1)\
            """);
  }

  @Test
  public void cast() {
    //        JPAQuery query = new JPAQuery(em).from(QPerson.person);
    //        QDog anyDog = QPerson.person.animals.any().as(QDog.class);
    //        query.where(anyDog.gender.eq("M"));
    //        List<Person> foundOwners = query.fetch(QPerson.person);

    QDomesticCat anyCat = QCat.cat.kittens.any().as(QDomesticCat.class);
    Predicate predicate = anyCat.name.eq("X");

    assertThat(serialize(predicate))
        .isEqualTo(
            """
            exists (select 1
            from cat.kittens as cat_kittens_0
            where cat_kittens_0.name = ?1)\
            """);
  }

  private String serialize(Expression<?> expression) {
    Expression<?> transformed = expression.accept(new JPACollectionAnyVisitor(), new Context());
    JPQLSerializer serializer = new JPQLSerializer(HQLTemplates.DEFAULT, null);
    serializer.handle(transformed);
    return serializer.toString();
  }
}
