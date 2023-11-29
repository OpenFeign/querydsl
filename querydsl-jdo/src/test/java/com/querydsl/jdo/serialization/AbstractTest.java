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
package com.querydsl.jdo.serialization;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.jdo.JDOQLSerializer;
import com.querydsl.jdo.JDOQLTemplates;

public abstract class AbstractTest {

  protected String serialize(SubQueryExpression<?> expr) {
    Expression<?> source = expr.getMetadata().getJoins().get(0).getTarget();
    JDOQLSerializer serializer = new JDOQLSerializer(JDOQLTemplates.DEFAULT, source);
    serializer.serialize(expr.getMetadata(), false, false);
    return serializer.toString().replace('\n', ' ');
  }
}
