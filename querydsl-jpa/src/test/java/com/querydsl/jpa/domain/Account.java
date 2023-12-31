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
package com.querydsl.jpa.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.querydsl.core.annotations.QueryInit;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import org.junit.Test;

/** The Class Account. */
@SuppressWarnings("serial")
@Entity
@Table(name = "account_")
public class Account implements Serializable {

  @Transient public int transientField;

  @Id long id;

  @ManyToOne
  @QueryInit("pid")
  Person owner;

  @Embedded EmbeddedType embeddedData;

  @Test
  public void test() {
    assertThatExceptionOfType(Exception.class)
        .isThrownBy(() -> QAccount.class.getField("serialVersionUID"));
    assertThatExceptionOfType(Exception.class)
        .isThrownBy(() -> QAccount.class.getField("transientField"));
  }
}
