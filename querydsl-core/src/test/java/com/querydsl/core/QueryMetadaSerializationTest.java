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
package com.querydsl.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.querydsl.core.QueryFlag.Position;
import com.querydsl.core.testutil.Serialization;
import com.querydsl.core.types.dsl.*;
import com.querydsl.core.util.ReflectionUtils;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import org.junit.Test;

public class QueryMetadaSerializationTest {

  private QueryMetadata metadata = new DefaultQueryMetadata();

  @Test
  public void serialization() throws IOException, ClassNotFoundException {
    StringPath expr = Expressions.stringPath("str");
    metadata.addJoin(JoinType.DEFAULT, expr);
    metadata.addFlag(new QueryFlag(Position.AFTER_FILTERS, ""));
    metadata.addGroupBy(expr);
    metadata.addHaving(expr.isEmpty());
    //        metadata.getJoins().get(0).addFlag(new JoinFlag(""));
    metadata.addJoinCondition(expr.isEmpty());
    metadata.addOrderBy(expr.asc());
    metadata.setProjection(expr);
    metadata.addWhere(expr.isEmpty());

    QueryMetadata metadata2 = Serialization.serialize(metadata);

    assertThat(metadata2.getFlags()).isEqualTo(metadata.getFlags());
    assertThat(metadata2.getGroupBy().get(0)).isEqualTo(metadata.getGroupBy().get(0));
    assertThat(metadata2.getGroupBy()).isEqualTo(metadata.getGroupBy());
    assertThat(metadata2.getHaving()).isEqualTo(metadata.getHaving());
    assertThat(metadata2.getJoins()).isEqualTo(metadata.getJoins());
    assertThat(metadata2.getModifiers()).isEqualTo(metadata.getModifiers());
    assertThat(metadata2.getOrderBy()).isEqualTo(metadata.getOrderBy());
    assertThat(metadata2.getParams()).isEqualTo(metadata.getParams());
    assertThat(metadata2.getProjection()).isEqualTo(metadata.getProjection());
    assertThat(metadata2.getWhere()).isEqualTo(metadata.getWhere());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void fullySerializable() {
    Set<Class<?>> checked =
        new HashSet<Class<?>>(
            Arrays.asList(
                Collection.class,
                List.class,
                Set.class,
                Map.class,
                Object.class,
                String.class,
                Class.class));
    Stack<Class<?>> classes = new Stack<Class<?>>();
    classes.addAll(
        Arrays.<Class<?>>asList(
            NumberPath.class,
            NumberOperation.class,
            NumberTemplate.class,
            BeanPath.class,
            DefaultQueryMetadata.class));
    while (!classes.isEmpty()) {
      Class<?> clazz = classes.pop();
      checked.add(clazz);
      if (!Serializable.class.isAssignableFrom(clazz) && !clazz.isPrimitive()) {
        fail("", clazz.getName() + " is not serializable");
      }
      for (Field field : clazz.getDeclaredFields()) {
        if (Modifier.isTransient(field.getModifiers())) {
          continue;
        }
        Set<Class<?>> types = new HashSet<Class<?>>(3);
        types.add(field.getType());
        if (field.getType().getSuperclass() != null) {
          types.add(field.getType().getSuperclass());
        }
        if (field.getType().getComponentType() != null) {
          types.add(field.getType().getComponentType());
        }
        if (Collection.class.isAssignableFrom(field.getType())) {
          types.add(ReflectionUtils.getTypeParameterAsClass(field.getGenericType(), 0));
        } else if (Map.class.isAssignableFrom(field.getType())) {
          types.add(ReflectionUtils.getTypeParameterAsClass(field.getGenericType(), 0));
          types.add(ReflectionUtils.getTypeParameterAsClass(field.getGenericType(), 1));
        }
        types.removeAll(checked);
        classes.addAll(types);
      }
    }
  }
}
