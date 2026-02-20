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

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.Test;

public class AnyUsageTest {

  @Entity
  public static class DealerGroup implements Serializable {
    private static final long serialVersionUID = 8001287260658920066L;

    @Id @GeneratedValue public Long id;

    @OneToMany(mappedBy = "dealerGroup")
    public Set<Dealer> dealers;
  }

  @Entity
  public static class Dealer implements Serializable {
    private static final long serialVersionUID = -6832045219902674887L;

    @Id @GeneratedValue public Long id;

    @ManyToOne public DealerGroup dealerGroup;

    @ManyToOne public Company company;
  }

  @Entity
  public static class Company implements Serializable {
    private static final long serialVersionUID = -5369301332567282659L;

    @Id @GeneratedValue public Long id;
  }

  @Test
  public void test() {
    var dealer = QAnyUsageTest_DealerGroup.dealerGroup.dealers.any();
    assertThat(dealer).isNotNull();
    assertThat(dealer.company).isNotNull();
  }

  @Test
  public void withQDealer() {
    List<Company> companies = new LinkedList<>();
    companies.add(new Company());
    var qDealer = QAnyUsageTest_Dealer.dealer;
    var expression = qDealer.company.in(companies);
    assertThat(expression).isNotNull();
  }

  @Test
  public void withQDealerGroup() {
    List<Company> companies = new LinkedList<>();
    companies.add(new Company());
    var qDealer = QAnyUsageTest_DealerGroup.dealerGroup.dealers.any();
    var expression = qDealer.company.in(companies);
    assertThat(expression).isNotNull();
  }
}
