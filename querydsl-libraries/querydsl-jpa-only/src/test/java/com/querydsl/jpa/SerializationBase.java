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
package com.querydsl.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.testutil.Serialization;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.domain.QCat;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.testutil.JPATestRunner;
import jakarta.persistence.EntityManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JPATestRunner.class)
public class SerializationBase implements JPATest {

  private QCat cat = QCat.cat;

  private EntityManager entityManager;

  @Test
  public void test() throws IOException, ClassNotFoundException {
    // create query
    JPAQuery<?> query = query();
    query.from(cat).where(cat.name.eq("Kate")).select(cat).fetch();

    var metadata = query.getMetadata();
    assertThat(metadata.getJoins()).isNotEmpty();
    assertThat(metadata.getWhere() != null).isTrue();
    assertThat(metadata.getProjection() != null).isTrue();
    var metadata2 = Serialization.serialize(metadata);

    // validate it
    assertThat(metadata2.getJoins()).isEqualTo(metadata.getJoins());
    assertThat(metadata2.getWhere()).isEqualTo(metadata.getWhere());
    assertThat(metadata2.getProjection()).isEqualTo(metadata.getProjection());

    // create new query
    JPAQuery<?> query2 = new JPAQuery<Void>(entityManager, metadata2);
    assertThat(query2).hasToString("select cat\nfrom Cat cat\nwhere cat.name = ?1");
    query2.select(cat).fetch();
  }

  @Test
  public void any_serialized() throws Exception {
    Predicate where = cat.kittens.any().name.eq("Ruth234");
    var where2 = Serialization.serialize(where);

    assertThat(query().from(cat).where(where).fetchCount()).isEqualTo(0);
    assertThat(query().from(cat).where(where2).fetchCount()).isEqualTo(0);
  }

  @Test
  public void any_serialized2() throws Exception {
    Predicate where = cat.kittens.any().name.eq("Ruth234");

    var file = new File("target", "predicate.ser");
    if (!file.exists()) {
      // serialize predicate on first run
      var fileOutputStream = new FileOutputStream(file);
      var out = new ObjectOutputStream(fileOutputStream);
      out.writeObject(where);
      out.close();
      assertThat(query().from(cat).where(where).fetchCount()).isEqualTo(0);
    } else {
      // deserialize predicate on second run
      var fileInputStream = new FileInputStream(file);
      var in = new ObjectInputStream(fileInputStream);
      var where2 = (Predicate) in.readObject();
      in.close();
      assertThat(query().from(cat).where(where2).fetchCount()).isEqualTo(0);
    }
  }

  private JPAQuery<?> query() {
    return new JPAQuery<Void>(entityManager);
  }

  @Override
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }
}
