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
package fluentq.codegen;

import fluentq.codegen.utils.ScalaWriter;
import fluentq.codegen.utils.model.ClassType;
import fluentq.codegen.utils.model.Parameter;
import fluentq.codegen.utils.model.Type;
import fluentq.core.types.Expression;
import fluentq.core.types.dsl.BooleanExpression;
import fluentq.core.types.dsl.ComparableExpression;
import fluentq.core.types.dsl.DateExpression;
import fluentq.core.types.dsl.DateTimeExpression;
import fluentq.core.types.dsl.EnumExpression;
import fluentq.core.types.dsl.NumberExpression;
import fluentq.core.types.dsl.SimpleExpression;
import fluentq.core.types.dsl.StringExpression;
import fluentq.core.types.dsl.TemporalExpression;
import fluentq.core.types.dsl.TimeExpression;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ScalaTypeDump {

  @Test
  @Disabled
  public void test() throws IOException {
    List<Class<?>> classes = new ArrayList<>();
    classes.add(SimpleExpression.class);
    classes.add(ComparableExpression.class);
    classes.add(BooleanExpression.class);
    classes.add(StringExpression.class);
    classes.add(TemporalExpression.class);
    classes.add(TimeExpression.class);
    classes.add(DateTimeExpression.class);
    classes.add(DateExpression.class);
    classes.add(EnumExpression.class);
    classes.add(NumberExpression.class);

    var w = new StringWriter();
    var writer = new ScalaWriter(w);
    writer.packageDecl("fluentq.scala");
    writer.imports(Expression.class.getPackage());
    for (Class<?> cl : classes) {
      Type type = new ClassType(cl);
      Type superClass = new ClassType(cl.getSuperclass());
      writer.beginClass(type, superClass);
      for (Method m : cl.getDeclaredMethods()) {
        List<Parameter> params = new ArrayList<>();
        for (Class<?> paramType : m.getParameterTypes()) {
          params.add(new Parameter("arg" + params.size(), new ClassType(paramType)));
        }
        Type returnType = new ClassType(m.getReturnType());
        writer.beginPublicMethod(returnType, ":" + m.getName(), params.toArray(new Parameter[0]));
        writer.end();
      }
      writer.end();
    }

    System.out.println(w);
  }
}
