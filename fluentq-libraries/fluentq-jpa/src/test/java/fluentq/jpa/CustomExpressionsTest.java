/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.jpa;

import static fluentq.jpa.Constants.cat;
import static fluentq.jpa.Constants.cust;

import fluentq.core.types.Expression;
import fluentq.core.types.TemplateFactory;
import fluentq.core.types.dsl.StringTemplate;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class CustomExpressionsTest extends AbstractQueryTest {

  public static class MyCustomExpr extends StringTemplate {

    private static final long serialVersionUID = 1L;

    public MyCustomExpr(Expression<?>... args) {
      super(TemplateFactory.DEFAULT.create("myCustom({0},{1})"), Arrays.asList(args));
    }
  }

  @Test
  public void customExpressions() {
    assertToString("myCustom(cust,cat)", new MyCustomExpr(cust, cat));
  }
}
