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
package com.querydsl.mongodb.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.types.ObjectId;
import org.junit.Test;
import org.mongodb.morphia.Morphia;

public class MongodbUserTest {

  private static final Morphia morphia = new Morphia().map(User.class);

  @Test
  public void map() {
    var tampere = new City("Tampere", 61.30, 23.50);

    var user = new User();
    user.setAge(12);
    user.setFirstName("Jaakko");
    user.addAddress("Aakatu", "00300", tampere);

    assertThat(morphia.toDBObject(user)).isNotNull();
  }

  @Test
  public void friend() {
    var friend = new User();
    friend.setId(ObjectId.createFromLegacyFormat(1, 2, 3));

    var user = new User();
    user.setFriend(friend);

    assertThat(morphia.toDBObject(user)).isNotNull();
  }

  @Test
  public void friends() {
    var friend = new User();
    friend.setId(ObjectId.createFromLegacyFormat(1, 2, 3));

    var user = new User();
    user.addFriend(friend);

    assertThat(morphia.toDBObject(user)).isNotNull();
  }
}
