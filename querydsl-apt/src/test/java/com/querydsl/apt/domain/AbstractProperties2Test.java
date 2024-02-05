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

import com.querydsl.core.annotations.QueryEntity;
import java.io.Serializable;
import org.junit.Test;

public class AbstractProperties2Test {

  public abstract static class GenericEntity<
      K extends Serializable & Comparable<K>, E extends GenericEntity<K, ?>> {

    public abstract K getId();

    public abstract void setId(K id);
  }

  public abstract static class AbstractEntity<P extends AbstractEntity<P>>
      extends GenericEntity<Integer, P> {

    private Integer id;

    @Override
    public Integer getId() {
      return id;
    }

    @Override
    public void setId(Integer id) {
      this.id = id;
    }
  }

  @QueryEntity
  public static class User extends AbstractEntity<User> {}

  @Test
  public void genericEntity_id_is_available() {
    assertThat(QAbstractProperties2Test_GenericEntity.genericEntity.id).isNotNull();
  }

  @Test
  public void abstractEntity_is_available() {
    assertThat(QAbstractProperties2Test_AbstractEntity.abstractEntity.id).isNotNull();
  }

  @Test
  public void abstractEntity_super_is_available() {
    assertThat(QAbstractProperties2Test_AbstractEntity.abstractEntity._super.getClass())
        .isEqualTo(QAbstractProperties2Test_GenericEntity.class);
  }

  @Test
  public void user_is_available() {
    assertThat(QAbstractProperties2Test_User.user.id).isNotNull();
  }

  @Test
  public void user_super_is_available() {
    assertThat(QAbstractProperties2Test_User.user._super.getClass())
        .isEqualTo(QAbstractProperties2Test_AbstractEntity.class);
  }
}
