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
package com.querydsl.core.types.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Templates;
import com.querydsl.core.types.ToStringVisitor;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

class OperationTest {

  enum ExampleEnum {
    A,
    B
  }

  @SuppressWarnings("unchecked")
  @Test
  void various() {
    var args = new Expression[] {new StringPath("x"), new StringPath("y")};
    List<Operation<?>> operations = new ArrayList<>();
    //        paths.add(new ArrayOperation(String[].class, "p"));
    operations.add(new BooleanOperation(Ops.EQ, args));
    operations.add(new ComparableOperation(String.class, Ops.SUBSTR_1ARG, args));
    operations.add(new DateOperation(Date.class, Ops.DateTimeOps.CURRENT_DATE, args));
    operations.add(new DateTimeOperation(Date.class, Ops.DateTimeOps.CURRENT_TIMESTAMP, args));
    operations.add(new EnumOperation(ExampleEnum.class, Ops.ALIAS, args));
    operations.add(new NumberOperation(Integer.class, Ops.ADD, args));
    operations.add(new SimpleOperation(String.class, Ops.TRIM, args));
    operations.add(new StringOperation(Ops.CONCAT, args));
    operations.add(new TimeOperation(Time.class, Ops.DateTimeOps.CURRENT_TIME, args));

    for (Operation<?> operation : operations) {
      Operation<?> other =
          ExpressionUtils.operation(
              operation.getType(), operation.getOperator(), new ArrayList<>(operation.getArgs()));
      assertThat(operation.accept(ToStringVisitor.DEFAULT, Templates.DEFAULT))
          .isEqualTo(operation.toString());
      assertThat(other).hasSameHashCodeAs(operation).isEqualTo(operation);
      assertThat(operation.getOperator()).isNotNull();
      assertThat(operation.getArgs()).isNotNull();
      assertThat(operation.getType()).isNotNull();
    }
  }
}
