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

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.List;

/** The Class Foo. */
@Entity
@Table(name = "foo_")
public class Foo {
  public String bar;

  @Id
  // @GeneratedValue(strategy=GenerationType.AUTO)
  public int id;

  @ElementCollection
  @CollectionTable(
      name = "foo_names",
      joinColumns = {@JoinColumn(name = "foo_id")})
  public List<String> names;

  @Temporal(TemporalType.DATE)
  public java.util.Date startDate;
}
