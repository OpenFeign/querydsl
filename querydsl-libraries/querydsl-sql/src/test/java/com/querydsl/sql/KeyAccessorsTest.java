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

import static com.querydsl.core.types.PathMetadataFactory.forVariable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.querydsl.core.types.dsl.NumberPath;
import java.io.Serializable;
import org.junit.Test;

public class KeyAccessorsTest {

  //    @Table("EMPLOYEE")
  public static class QEmployee extends RelationalPathBase<QEmployee> {

    private static final long serialVersionUID = 2020996035;

    public static final QEmployee employee = new QEmployee("EMPLOYEE");

    public class PrimaryKeys implements Serializable {

      private static final long serialVersionUID = 3378990153959880914L;

      public final PrimaryKey<QEmployee> sysIdx53 = createPrimaryKey(id);
    }

    public class ForeignKeys implements Serializable {

      private static final long serialVersionUID = 4541576457291793933L;

      public final ForeignKey<QEmployee> superiorFk = createForeignKey(superiorId, "ID");

      public final ForeignKey<QEmployee> _superiorFk = createInvForeignKey(id, "SUPERIOR_ID");
    }

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> superiorId = createNumber("superiorId", Integer.class);

    public final PrimaryKeys pk = new PrimaryKeys();

    public final ForeignKeys fk = new ForeignKeys();

    public QEmployee(String variable) {
      super(QEmployee.class, forVariable(variable), "", "EMPLOYEE");
      addMetadata();
    }

    protected void addMetadata() {
      addMetadata(id, ColumnMetadata.named("ID"));
      addMetadata(superiorId, ColumnMetadata.named("SUPERIOR_ID"));
    }
  }

  @Test
  public void keys() {
    QEmployee employee = QEmployee.employee;
    assertThat(employee.pk.sysIdx53).isNotNull();
    assertThat(employee.fk.superiorFk).isNotNull();
    assertThat(employee.fk._superiorFk).isNotNull();
  }
}
