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

import com.querydsl.jpa.domain4.BookID;
import com.querydsl.jpa.domain4.BookMark;
import com.querydsl.jpa.domain4.BookVersion;
import com.querydsl.jpa.domain4.Library;
import com.querydsl.jpa.domain5.MyEmbeddedAttribute;
import com.querydsl.jpa.domain5.MyEntity;
import com.querydsl.jpa.domain5.MyMappedSuperclass;
import com.querydsl.jpa.domain5.MyOtherEntity;
import java.util.Arrays;
import java.util.List;

public final class Domain {

  private Domain() {}

  public static final List<Class<?>> classes =
      Arrays.<Class<?>>asList(
          Account.class,
          Animal.class,
          Author.class,
          AuditLog.class,
          Bar.class,
          Book.class,
          Calendar.class,
          Cat.class,
          Catalog.class,
          Child.class,
          Color.class,
          Company.class,
          Customer.class,
          Department.class,
          Document.class,
          DomesticCat.class,
          Dolphin.class,
          EmbeddedType.class,
          Employee.class,
          Entity1.class,
          Entity2.class,
          Family.class,
          Foo.class,
          Formula.class,
          Group.class,
          Human.class,
          InheritedProperties.class,
          Item.class,
          Location.class,
          Mammal.class,
          Name.class,
          Named.class,
          NameList.class,
          Nationality.class,
          Novel.class,
          Numeric.class,
          Order.class,
          Parameter.class,
          Parent.class,
          Payment.class,
          PaymentStatus.class,
          Person.class,
          PersonId.class,
          Player.class,
          Price.class,
          Product.class,
          Show.class,
          SimpleTypes.class,
          Status.class,
          StatusChange.class,
          Store.class,
          Superclass.class,
          User.class,
          World.class,
          BookID.class,
          BookMark.class,
          BookVersion.class,
          Library.class,
          MyEmbeddedAttribute.class,
          MyEntity.class,
          MyMappedSuperclass.class,
          MyOtherEntity.class);
}
