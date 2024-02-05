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

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author Arash
 */
@Entity
@Table(name = "Person", catalog = "TestDB", schema = "dbo")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
  @NamedQuery(
      name = "Person.findByPersonId",
      query = "SELECT p FROM Person p WHERE p.personId = :personId"),
  @NamedQuery(
      name = "Person.findByPersonName",
      query = "SELECT p FROM Person p WHERE p.personName = :personName"),
  @NamedQuery(
      name = "Person.findByPersonFamily",
      query = "SELECT p FROM Person p WHERE p.personFamily = :personFamily"),
  @NamedQuery(
      name = "Person.findByPersonReference",
      query = "SELECT p FROM Person p WHERE p.personReference = :personReference")
})
public class Person implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @Column(name = "person_id", nullable = false)
  private Integer personId;

  @Column(name = "person_name", length = 50)
  private String personName;

  @Column(name = "person_family", length = 50)
  private String personFamily;

  @Column(name = "person_reference")
  private Integer personReference;

  @OneToOne(cascade = CascadeType.ALL, mappedBy = "person1", fetch = FetchType.LAZY)
  private Person person;

  @JoinColumn(
      name = "person_id",
      referencedColumnName = "person_id",
      nullable = false,
      insertable = false,
      updatable = false)
  @OneToOne(optional = false, fetch = FetchType.LAZY)
  private Person person1;

  public Person() {}

  public Person(Integer personId) {
    this.personId = personId;
  }

  public Integer getPersonId() {
    return personId;
  }

  public void setPersonId(Integer personId) {
    this.personId = personId;
  }

  public String getPersonName() {
    return personName;
  }

  public void setPersonName(String personName) {
    this.personName = personName;
  }

  public String getPersonFamily() {
    return personFamily;
  }

  public void setPersonFamily(String personFamily) {
    this.personFamily = personFamily;
  }

  public Integer getPersonReference() {
    return personReference;
  }

  public void setPersonReference(Integer personReference) {
    this.personReference = personReference;
  }

  public Person getPerson() {
    return person;
  }

  public void setPerson(Person person) {
    this.person = person;
  }

  public Person getPerson1() {
    return person1;
  }

  public void setPerson1(Person person1) {
    this.person1 = person1;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (personId != null ? personId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Person)) {
      return false;
    }
    Person other = (Person) object;
    if ((this.personId == null && other.personId != null)
        || (this.personId != null && !this.personId.equals(other.personId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "newpackage.Person[ personId=" + personId + " ]";
  }
}
