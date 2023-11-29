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
package com.querydsl.apt.domain;

import static org.junit.Assert.assertEquals;

import com.querydsl.core.annotations.QueryEmbedded;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.types.dsl.EntityPathBase;
import java.util.List;
import org.junit.Test;

public class QueryEmbedded6Test {

  @QueryEntity
  public static class User {

    @QueryEmbedded List<User> list;
  }

  @Test
  public void entityPathBase_is_superClass() {
    assertEquals(EntityPathBase.class, QQueryEmbedded6Test_User.class.getSuperclass());
  }

  @Test
  public void user_list_any() {
    assertEquals(
        QQueryEmbedded6Test_User.class, QQueryEmbedded6Test_User.user.list.any().getClass());
  }
}
