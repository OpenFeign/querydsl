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

import static org.junit.Assert.assertNotNull;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;

public class PropertiesTest {

  @Entity
  public abstract static class AbstractEntity {}

  @Entity
  @Table(name = "Customer")
  public static class Customer extends AbstractEntity {

    private String name;
    private List<Pizza> pizzas = new ArrayList<Pizza>(0);

    @Column
    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @OneToMany(mappedBy = "customer")
    public List<Pizza> getPizzas() {
      return pizzas;
    }

    public void setPizzas(List<Pizza> pizzas) {
      this.pizzas = pizzas;
    }
  }

  @Entity
  @Table(name = "Pizza")
  public static class Pizza extends AbstractEntity {

    private Date orderTime;
    private Customer customer;
    private List<Topping> toppings = new ArrayList<Topping>(0);

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    public Date getOrderTime() {
      return new Date(orderTime.getTime());
    }

    public void setOrderTime(Date orderTime) {
      this.orderTime = new Date(orderTime.getTime());
    }

    @ManyToOne
    @JoinColumn(name = "customerId")
    public Customer getCustomer() {
      return customer;
    }

    public void setCustomer(Customer customer) {
      this.customer = customer;
    }

    @OneToMany(mappedBy = "pizza")
    public List<Topping> getToppings() {
      return toppings;
    }

    public void setToppings(List<Topping> toppings) {
      this.toppings = toppings;
    }
  }

  @Entity
  public static class Topping {}

  @Test
  public void customer() {
    assertNotNull(QPropertiesTest_Customer.customer.name);
    assertNotNull(QPropertiesTest_Customer.customer.pizzas);
  }

  @Test
  public void pizza() {
    assertNotNull(QPropertiesTest_Pizza.pizza.orderTime);
    assertNotNull(QPropertiesTest_Pizza.pizza.customer);
    assertNotNull(QPropertiesTest_Pizza.pizza.toppings);
  }
}
