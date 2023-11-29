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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.util.Date;
import org.junit.Test;

public class Superclass2Test {

  @MappedSuperclass
  public static class CommonPersistence {

    @Column(name = "created_on")
    private Date createdOn;

    @PrePersist
    protected void onCreate() {
      createdOn = new Date();
    }

    public Date getCreatedOn() {
      return new Date(createdOn.getTime());
    }
  }

  @Entity
  public static class Subtype extends CommonPersistence {}

  @Test
  public void defaultInstance() {
    assertThat(QSuperclass2Test_CommonPersistence.commonPersistence).isNotNull();
  }

  @Test
  public void test() {
    assertThat(QSuperclass2Test_Subtype.subtype.createdOn).isNotNull();
  }
}
