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

import com.querydsl.mongodb.document.DocumentUtils;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

class MongodbUserTest {

  private static final Datastore morphia;

  static {
    morphia = Morphia.createDatastore("db");
    morphia.getMapper().map(User.class);
  }

  @Test
  void map() {
    var tampere = new City("Tampere", 61.30, 23.50);

    var user = new User();
    user.setAge(12);
    user.setFirstName("Jaakko");
    user.addAddress("Aakatu", "00300", tampere);

    assertThat(DocumentUtils.getAsDocument(morphia, user)).isNotNull();
  }

  @Test
  void friend() {
    var friend = new User();
    friend.setId(new ObjectId(1, 2));

    var user = new User();
    user.setFriend(friend);

    assertThat(DocumentUtils.getAsDocument(morphia, user)).isNotNull();
  }

  @Test
  void friends() {
    var friend = new User();
    friend.setId(new ObjectId(1, 2));

    var user = new User();
    user.addFriend(friend);

    assertThat(DocumentUtils.getAsDocument(morphia, user)).isNotNull();
  }
}
