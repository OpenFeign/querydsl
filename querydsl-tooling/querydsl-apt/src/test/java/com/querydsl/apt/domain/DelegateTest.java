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

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QueryDelegate;
import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QuerySupertype;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.Test;

public class DelegateTest {

  @QuerySupertype
  public static class Identifiable {

    long id;
  }

  @QueryEntity
  public static class User extends Identifiable {

    String name;

    User managedBy;
  }

  @QueryEntity
  public static class SimpleUser extends User {}

  @QueryEntity
  public static class SimpleUser2 extends SimpleUser {}

  @QueryDelegate(User.class)
  public static Expression<Boolean> isManagedBy(QDelegateTest_User user, User other) {
    return ConstantImpl.create(true);
  }

  @QueryDelegate(User.class)
  public static Expression<Boolean> isManagedBy(QDelegateTest_User user, QDelegateTest_User other) {
    return ConstantImpl.create(true);
  }

  @QueryDelegate(User.class)
  public static Expression<Boolean> simpleMethod(QDelegateTest_User user) {
    return ConstantImpl.create(true);
  }

  @QueryDelegate(DelegateTest.User.class)
  public static StringPath getName(QDelegateTest_User user) {
    return user.name;
  }

  @Test
  public void user() {
    var user = QDelegateTest_User.user;
    assertThat(user.isManagedBy(new User())).isNotNull();
    assertThat(user.isManagedBy(user)).isNotNull();
    assertThat(user.simpleMethod()).isNotNull();
    assertThat(user.getName()).isEqualTo(user.name);
  }

  @Test
  public void simpleUser() {
    var user = QDelegateTest_SimpleUser.simpleUser;
    assertThat(user.isManagedBy(new User())).isNotNull();
    assertThat(user.isManagedBy(user._super)).isNotNull();
    assertThat(user.getName()).isEqualTo(user.name);
  }

  @Test
  public void simpleUser2() {
    var user = QDelegateTest_SimpleUser2.simpleUser2;
    assertThat(user.isManagedBy(new User())).isNotNull();
    assertThat(user.isManagedBy(user._super._super)).isNotNull();
    assertThat(user.getName()).isEqualTo(user.name);
  }
}
