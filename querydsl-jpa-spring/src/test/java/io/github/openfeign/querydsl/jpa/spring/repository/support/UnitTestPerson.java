/*
 * Copyright 2016-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.openfeign.querydsl.jpa.spring.repository.support;

import jakarta.persistence.*;

/**
 * @author Mattias Hellborg Arthursson
 */
@Entity
public class UnitTestPerson {
  @Id private Long dn;

  private String fullName;

  private String lastName;

  private String country;

  private String company;

  private String telephoneNumber;

  public UnitTestPerson() {}

  public UnitTestPerson(
      Long dn,
      String fullName,
      String lastName,
      String country,
      String company,
      String telephoneNumber) {
    this.dn = dn;
    this.fullName = fullName;
    this.lastName = lastName;
    this.country = country;
    this.company = company;
    this.telephoneNumber = telephoneNumber;
  }

  public Long getDn() {
    return dn;
  }

  public void setDn(Long dn) {
    this.dn = dn;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getTelephoneNumber() {
    return telephoneNumber;
  }

  public void setTelephoneNumber(String telephoneNumber) {
    this.telephoneNumber = telephoneNumber;
  }
}
