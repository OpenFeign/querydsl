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
package com.querydsl.sql;

import static com.querydsl.core.Target.CUBRID;
import static com.querydsl.core.Target.DB2;
import static com.querydsl.core.Target.DERBY;
import static com.querydsl.core.Target.ORACLE;
import static com.querydsl.core.Target.POSTGRESQL;
import static com.querydsl.core.Target.SQLITE;
import static com.querydsl.core.Target.SQLSERVER;
import static com.querydsl.core.Target.TERADATA;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.testutil.ExcludeIn;
import com.querydsl.sql.dml.BeanMapper;
import com.querydsl.sql.domain.Employee;
import com.querydsl.sql.domain.QEmployee;
import org.junit.After;
import org.junit.Test;

@ExcludeIn({CUBRID, DB2, DERBY, ORACLE, SQLSERVER, POSTGRESQL, SQLITE, TERADATA})
public abstract class BeanPopulationBase extends AbstractBaseTest {

  private final QEmployee e = new QEmployee("e");

  @After
  public void tearDown() {
    delete(e).where(e.firstname.eq("John")).execute();
  }

  @Test
  public void custom_projection() {
    // Insert
    var employee = new Employee();
    employee.setFirstname("John");
    Integer id = insert(e).populate(employee).executeWithKey(e.id);
    employee.setId(id);

    // Update
    employee.setLastname("S");
    assertThat(update(e).populate(employee).where(e.id.eq(employee.getId())).execute())
        .isEqualTo(1L);

    // Query
    var smith =
        extQuery()
            .from(e)
            .where(e.lastname.eq("S"))
            .limit(1)
            .uniqueResult(Employee.class, e.lastname, e.firstname);
    assertThat(smith.getFirstname()).isEqualTo("John");
    assertThat(smith.getLastname()).isEqualTo("S");

    // Query with alias
    smith =
        extQuery()
            .from(e)
            .where(e.lastname.eq("S"))
            .limit(1)
            .uniqueResult(Employee.class, e.lastname.as("lastname"), e.firstname.as("firstname"));
    assertThat(smith.getFirstname()).isEqualTo("John");
    assertThat(smith.getLastname()).isEqualTo("S");

    // Query into custom type
    var other =
        extQuery()
            .from(e)
            .where(e.lastname.eq("S"))
            .limit(1)
            .uniqueResult(OtherEmployee.class, e.lastname, e.firstname);
    assertThat(other.getFirstname()).isEqualTo("John");
    assertThat(other.getLastname()).isEqualTo("S");

    // Delete (no changes needed)
    assertThat(delete(e).where(e.id.eq(employee.getId())).execute()).isEqualTo(1L);
  }

  @Test
  public void insert_update_query_and_delete() {
    // Insert
    var employee = new Employee();
    employee.setFirstname("John");
    Integer id = insert(e).populate(employee).executeWithKey(e.id);
    assertThat(id).isNotNull();
    employee.setId(id);

    // Update
    employee.setLastname("S");
    assertThat(update(e).populate(employee).where(e.id.eq(employee.getId())).execute())
        .isEqualTo(1L);

    // Query
    var smith = query().from(e).where(e.lastname.eq("S")).limit(1).select(e).fetchFirst();
    assertThat(smith.getFirstname()).isEqualTo("John");

    // Delete (no changes needed)
    assertThat(delete(e).where(e.id.eq(employee.getId())).execute()).isEqualTo(1L);
  }

  @Test
  public void populate_with_beanMapper() {
    var employee = new Employee();
    employee.setFirstname("John");
    insert(e).populate(employee, new BeanMapper()).execute();
  }
}
