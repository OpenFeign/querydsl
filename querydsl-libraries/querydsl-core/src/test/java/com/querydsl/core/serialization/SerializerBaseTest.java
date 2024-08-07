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
package com.querydsl.core.serialization;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.JavaTemplates;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import java.util.Map;
import org.junit.Test;

public class SerializerBaseTest {

  @Test
  public void test() {
    var serializer = new DummySerializer(new JavaTemplates());
    var strPath = Expressions.stringPath("str");
    // path
    serializer.handle(strPath);
    // operation
    serializer.handle(strPath.isNotNull());
    // long path
    serializer.handle(new PathBuilder<>(Object.class, "p").getList("l", Map.class).get(0));
    // constant
    serializer.handle(ConstantImpl.create(""));
    //  custom
    serializer.handle(ExpressionUtils.template(Object.class, "xxx", ConstantImpl.create("")));
  }
}
