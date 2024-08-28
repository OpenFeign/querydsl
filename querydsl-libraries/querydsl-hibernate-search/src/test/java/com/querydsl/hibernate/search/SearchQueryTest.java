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
package com.querydsl.hibernate.search;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hibernate.Session;
import org.junit.Ignore;
import org.junit.Test;

public class SearchQueryTest extends AbstractQueryTest {

  private final QUser user = new QUser("user");

  @Override
  public void setUp() {
    super.setUp();
    createUser("Bob", "Stewart", "Smith", "bob@example.com");

    createUser("Barbara", "X", "Lock", "barbara@a.com");
    createUser("Anton", "X", "Bruckner", "anton@b.com");
    createUser("Robert", "X", "Downing", "bob@c.com");
    createUser("John", "X", "Stewart", "john@d.com");

    Session session = getSession();
    session.flush();
    session.getTransaction().commit();
    session.beginTransaction();
  }

  @Test
  public void exists() {
    assertThat(query().where(user.emailAddress.eq("bob@example.com")).fetchCount() > 0).isTrue();
    assertThat(query().where(user.emailAddress.eq("bobby@example.com")).fetchCount() > 0).isFalse();
  }

  @Test
  public void notExists() {
    assertThat(query().where(user.emailAddress.eq("bob@example.com")).fetchCount() == 0).isFalse();
    assertThat(query().where(user.emailAddress.eq("bobby@example.com")).fetchCount() == 0).isTrue();
  }

  @Test
  public void count() {
    BooleanExpression filter = user.emailAddress.eq("bob@example.com");
    assertThat(query().where(filter).fetchCount()).isEqualTo(1);
  }

  @Test
  public void uniqueResult() {
    BooleanExpression filter = user.emailAddress.eq("bob@example.com");
    User u = query().where(filter).fetchOne();
    assertThat(u).isNotNull();
    assertThat(u.getEmailAddress()).isEqualTo("bob@example.com");
  }

  @Test
  public void list() {
    BooleanExpression filter = user.emailAddress.eq("bob@example.com");
    List<User> list = query().where(filter).fetch();
    assertThat(list).hasSize(1);
    User u = query().where(filter).fetchOne();
    assertThat(list.getFirst()).isEqualTo(u);
  }

  @Test(expected = NonUniqueResultException.class)
  public void unique_result_throws_exception_on_multiple_results() {
    query().where(user.middleName.eq("X")).fetchOne();
  }

  @Test
  public void singleResult() {
    assertThat(query().where(user.middleName.eq("X")).fetchFirst()).isNotNull();
  }

  @Test
  public void ordering() {
    BooleanExpression filter = user.middleName.eq("X");
    // asc
    List<String> asc = getFirstNames(query().where(filter).orderBy(user.firstName.asc()).fetch());
    assertThat(asc).isEqualTo(Arrays.asList("Anton", "Barbara", "John", "Robert"));

    // desc
    List<String> desc = getFirstNames(query().where(filter).orderBy(user.firstName.desc()).fetch());
    assertThat(desc).isEqualTo(Arrays.asList("Robert", "John", "Barbara", "Anton"));
  }

  @Test
  public void paging() {
    BooleanExpression filter = user.middleName.eq("X");
    OrderSpecifier<?> order = user.firstName.asc();

    // limit
    List<String> limit = getFirstNames(query().where(filter).orderBy(order).limit(2).fetch());
    assertThat(limit).isEqualTo(Arrays.asList("Anton", "Barbara"));

    // offset
    List<String> offset = getFirstNames(query().where(filter).orderBy(order).offset(1).fetch());
    assertThat(offset).isEqualTo(Arrays.asList("Barbara", "John", "Robert"));

    // limit + offset
    List<String> limitAndOffset =
        getFirstNames(query().where(filter).orderBy(order).limit(2).offset(1).fetch());
    assertThat(limitAndOffset).isEqualTo(Arrays.asList("Barbara", "John"));
  }

  @Test
  public void listResults() {
    BooleanExpression filter = user.middleName.eq("X");
    QueryResults<User> users =
        query().where(filter).orderBy(user.firstName.asc()).limit(2).fetchResults();
    List<String> asc = getFirstNames(users.getResults());
    assertThat(asc).isEqualTo(Arrays.asList("Anton", "Barbara"));
    assertThat(users.getTotal()).isEqualTo(4);
  }

  @Test
  public void no_where() {
    assertThat(query().fetch()).hasSize(5);
  }

  @Test
  @Ignore // OufOfMemoryError
  public void limit_max_value() {
    assertThat(query().limit(Long.MAX_VALUE).fetch()).hasSize(5);
  }

  private List<String> getFirstNames(List<User> users) {
    List<String> rv = new ArrayList<String>(users.size());
    for (User user : users) {
      rv.add(user.getFirstName());
    }
    return rv;
  }

  private User createUser(String firstName, String middleName, String lastName, String email) {
    User user = new User();
    user.setFirstName(firstName);
    user.setMiddleName(middleName);
    user.setLastName(lastName);
    user.setEmailAddress(email);
    getSession().save(user);
    return user;
  }

  private SearchQuery<User> query() {
    return new SearchQuery<User>(getSession(), user);
  }
}
