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
package com.querydsl.codegen;

import com.querydsl.codegen.utils.ScalaWriter;
import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.Parameter;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.*;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;

public class ScalaTypeDump {

  @Test
  @Ignore
  public void test() throws IOException {
    List<Class<?>> classes = new ArrayList<Class<?>>();
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

    StringWriter w = new StringWriter();
    ScalaWriter writer = new ScalaWriter(w);
    writer.packageDecl("com.querydsl.scala");
    writer.imports(Expression.class.getPackage());
    for (Class<?> cl : classes) {
      Type type = new ClassType(cl);
      Type superClass = new ClassType(cl.getSuperclass());
      writer.beginClass(type, superClass);
      for (Method m : cl.getDeclaredMethods()) {
        List<Parameter> params = new ArrayList<Parameter>();
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
