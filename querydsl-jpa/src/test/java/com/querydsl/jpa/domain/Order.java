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

import jakarta.persistence.*;
import java.util.List;
import java.util.Map;

/** The Class Order. */
@Entity
@Table(name = "order_")
public class Order {
  @ManyToOne Customer customer;

  @ElementCollection
  @OrderColumn(name = "_index")
  List<Integer> deliveredItemIndices;

  @Id long id;

  @OneToMany
  @OrderColumn(name = "_index")
  List<Item> items;

  @OneToMany
  @JoinTable(name = "LineItems")
  @OrderColumn(name = "_index")
  List<Item> lineItems;

  @OneToMany
  @JoinTable(name = "LineItems2")
  @MapKey(name = "id")
  Map<Integer, Item> lineItemsMap;

  boolean paid;
}
