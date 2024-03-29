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
package com.querydsl.r2dbc.domain;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.types.dsl.TimePath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.RelationalPathBase;
import java.math.BigDecimal;

// @Schema("PUBLIC")
// @Table("EMPLOYEE")
public class QEmployeeNoPK extends RelationalPathBase<Employee> {

  private static final long serialVersionUID = 1394463749655231079L;

  public static final QEmployeeNoPK employee = new QEmployeeNoPK("EMPLOYEE");

  public final NumberPath<Integer> id = createNumber("id", Integer.class);

  public final StringPath firstname = createString("firstname");

  public final StringPath lastname = createString("lastname");

  public final NumberPath<BigDecimal> salary = createNumber("salary", BigDecimal.class);

  public final DatePath<java.time.LocalDate> datefield =
      createDate("datefield", java.time.LocalDate.class);

  public final TimePath<java.time.LocalTime> timefield =
      createTime("timefield", java.time.LocalTime.class);

  public final NumberPath<Integer> superiorId = createNumber("superiorId", Integer.class);

  public final ForeignKey<Employee> superiorIdKey = createForeignKey(superiorId, "ID");

  public final ForeignKey<Employee> _superiorIdKey = createInvForeignKey(id, "SUPERIOR_ID");

  public QEmployeeNoPK(String path) {
    super(Employee.class, PathMetadataFactory.forVariable(path), "PUBLIC", "EMPLOYEE");
    addMetadata();
  }

  public QEmployeeNoPK(PathMetadata metadata) {
    super(Employee.class, metadata, "PUBLIC", "EMPLOYEE");
    addMetadata();
  }

  protected void addMetadata() {
    addMetadata(id, ColumnMetadata.named("ID"));
    addMetadata(firstname, ColumnMetadata.named("FIRSTNAME"));
    addMetadata(lastname, ColumnMetadata.named("LASTNAME"));
    addMetadata(salary, ColumnMetadata.named("SALARY"));
    addMetadata(datefield, ColumnMetadata.named("DATEFIELD"));
    addMetadata(timefield, ColumnMetadata.named("TIMEFIELD"));
    addMetadata(superiorId, ColumnMetadata.named("SUPERIOR_ID"));
  }
}
