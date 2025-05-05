/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.mongodb.document;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.DBRef;
import com.mongodb.MongoException;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.testutil.MongoDB;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.Predicate;
import com.querydsl.mongodb.domain.AbstractEntity;
import com.querydsl.mongodb.domain.Address;
import com.querydsl.mongodb.domain.City;
import com.querydsl.mongodb.domain.Country;
import com.querydsl.mongodb.domain.Dates;
import com.querydsl.mongodb.domain.Item;
import com.querydsl.mongodb.domain.MapEntity;
import com.querydsl.mongodb.domain.QAddress;
import com.querydsl.mongodb.domain.QCountry;
import com.querydsl.mongodb.domain.QDates;
import com.querydsl.mongodb.domain.QItem;
import com.querydsl.mongodb.domain.QMapEntity;
import com.querydsl.mongodb.domain.QUser;
import com.querydsl.mongodb.domain.User;
import com.querydsl.mongodb.domain.User.Gender;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import dev.morphia.annotations.Reference;
import dev.morphia.mapping.Mapper;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(MongoDB.class)
public class MongodbQueryTest {

  private final MongoClient mongo;
  private final MongoDatabase database;
  private final Datastore ds;

  private final String dbname = "testdb";
  private final QUser user = QUser.user;
  private final QItem item = QItem.item;
  private final QAddress address = QAddress.address;
  private final QMapEntity mapEntity = QMapEntity.mapEntity;
  private final QDates dates = QDates.dates;
  private final QCountry country = QCountry.country;

  List<User> users = new ArrayList<>();
  List<Document> userDocuments = new ArrayList<>();
  User user1, user2, user3, user4;
  Document u1, u2, u3, u4;
  City tampere, helsinki;

  public MongodbQueryTest() throws UnknownHostException, MongoException {
    mongo = MongoClients.create();
    ds = Morphia.createDatastore(mongo, dbname);
    database = mongo.getDatabase(dbname);
    ds.getMapper().map(User.class, Item.class, MapEntity.class, Dates.class);
  }

  @Before
  public void before() throws UnknownHostException, MongoException {
    ds.getMapper().getCollection(Item.class).deleteMany(new org.bson.Document());
    ds.getMapper().getCollection(User.class).deleteMany(new org.bson.Document());
    ds.getMapper().getCollection(Country.class).deleteMany(new org.bson.Document());
    ds.getMapper().getCollection(MapEntity.class).deleteMany(new org.bson.Document());

    tampere = new City("Tampere", 61.30, 23.50);
    helsinki = new City("Helsinki", 60.15, 20.03);

    user1 =
        addUser(
            "Jaakko",
            "Jantunen",
            20,
            new Address("Aakatu", "00100", helsinki),
            new Address("Aakatu1", "00100", helsinki),
            new Address("Aakatu2", "00100", helsinki));
    user2 = addUser("Jaakki", "Jantunen", 30, new Address("Beekatu", "00200", helsinki));
    user3 = addUser("Jaana", "Aakkonen", 40, new Address("Ceekatu", "00300", tampere));
    user4 = addUser("Jaana", "BeekkoNen", 50, new Address("Deekatu", "00400", tampere));

    u1 = asDocument(user1);
    u2 = asDocument(user2);
    u3 = asDocument(user3);
    u4 = asDocument(user4);

    // order users by lastname, firstname
    userDocuments = Arrays.asList(u3, u4, u2, u1);
  }

  @Test
  public void query1() {
    assertThat(query(user).fetchCount()).isEqualTo(4L);
    assertThat(query(User.class).fetchCount()).isEqualTo(4L);
  }

  @Test
  public void list_keys() {
    var u =
        where(user.firstName.eq("Jaakko")).fetch(user.firstName, user.mainAddress().street).get(0);
    assertThat(u.get("firstName")).isEqualTo("Jaakko");
    assertThat(u.get("lastName")).isNull();
    assertThat(u.get("mainAddress", Document.class).get("street")).isEqualTo("Aakatu");
    assertThat(u.get("mainAddress", Document.class).get("postCode")).isNull();
  }

  @Test
  public void singleResult_keys() {
    var u = where(user.firstName.eq("Jaakko")).fetchFirst(user.firstName);
    assertThat(u.get("firstName")).isEqualTo("Jaakko");
    assertThat(u.get("lastName")).isNull();
  }

  @Test
  public void uniqueResult_keys() {
    var u = where(user.firstName.eq("Jaakko")).fetchOne(user.firstName);
    assertThat(u.get("firstName")).isEqualTo("Jaakko");
    assertThat(u.get("lastName")).isNull();
  }

  @Test
  public void list_deep_keys() {
    var u = where(user.firstName.eq("Jaakko")).fetchFirst(user.addresses.any().street);
    List<Document> addresses = u.get("addresses", List.class);
    for (Document a : addresses) {
      assertThat(a.get("street")).isNotNull();
      assertThat(a.get("city")).isNull();
    }
  }

  @Test
  public void between() {
    assertQuery(user.age.between(20, 30), u2, u1);
    assertQuery(user.age.goe(20).and(user.age.loe(30)), u2, u1);
  }

  @Test
  public void between_not() {
    assertQuery(user.age.between(20, 30).not(), u3, u4);
    assertQuery(user.age.goe(20).and(user.age.loe(30)).not(), u3, u4);
  }

  @Test
  public void contains_key() {
    var entity = new MapEntity();
    entity.getProperties().put("key", "value");
    ds.save(entity);

    assertThat(query(mapEntity).where(mapEntity.properties.get("key").isNotNull()).fetchCount() > 0)
        .isTrue();
    assertThat(
            query(mapEntity).where(mapEntity.properties.get("key2").isNotNull()).fetchCount() > 0)
        .isFalse();

    assertThat(query(mapEntity).where(mapEntity.properties.containsKey("key")).fetchCount() > 0)
        .isTrue();
    assertThat(query(mapEntity).where(mapEntity.properties.containsKey("key2")).fetchCount() > 0)
        .isFalse();
  }

  @Test
  public void contains_key_not() {
    var entity = new MapEntity();
    entity.getProperties().put("key", "value");
    ds.save(entity);

    assertThat(
            query(mapEntity).where(mapEntity.properties.get("key").isNotNull().not()).fetchCount()
                > 0)
        .isFalse();
    assertThat(
            query(mapEntity).where(mapEntity.properties.get("key2").isNotNull().not()).fetchCount()
                > 0)
        .isTrue();

    assertThat(
            query(mapEntity).where(mapEntity.properties.containsKey("key").not()).fetchCount() > 0)
        .isFalse();
    assertThat(
            query(mapEntity).where(mapEntity.properties.containsKey("key2").not()).fetchCount() > 0)
        .isTrue();
  }

  @Test
  public void equals_ignore_case() {
    assertThat(where(user.firstName.equalsIgnoreCase("jAaKko")).fetchCount() > 0).isTrue();
    assertThat(where(user.firstName.equalsIgnoreCase("AaKk")).fetchCount() > 0).isFalse();
  }

  @Test
  public void equals_ignore_case_not() {
    assertThat(where(user.firstName.equalsIgnoreCase("jAaKko").not()).fetchCount() > 0).isTrue();
    assertThat(where(user.firstName.equalsIgnoreCase("AaKk").not()).fetchCount() > 0).isTrue();
  }

  @Test
  public void equals_and_between() {
    assertQuery(user.firstName.startsWith("Jaa").and(user.age.between(20, 30)), u2, u1);
    assertQuery(
        user.firstName.startsWith("Jaa").and(user.age.goe(20).and(user.age.loe(30))), u2, u1);
  }

  @Test
  public void equals_and_between_not() {
    assertQuery(user.firstName.startsWith("Jaa").and(user.age.between(20, 30)).not(), u3, u4);
    assertQuery(
        user.firstName.startsWith("Jaa").and(user.age.goe(20).and(user.age.loe(30))).not(), u3, u4);
  }

  @Test
  public void exists() {
    assertThat(where(user.firstName.eq("Jaakko")).fetchCount() > 0).isTrue();
    assertThat(where(user.firstName.eq("JaakkoX")).fetchCount() > 0).isFalse();
  }

  @Test
  public void find_by_id() {
    assertThat(where(user.id.eq(user1.getId())).fetchFirst() != null).isNotNull();
  }

  @Test
  public void notExists() {
    assertThat(where(user.firstName.eq("Jaakko")).fetchCount() == 0).isFalse();
    assertThat(where(user.firstName.eq("JaakkoX")).fetchCount() == 0).isTrue();
  }

  @Test
  public void uniqueResult() {
    assertThat(where(user.firstName.eq("Jaakko")).fetchOne().get("lastName")).isEqualTo("Jantunen");
  }

  @Test(expected = NonUniqueResultException.class)
  public void uniqueResultContract() {
    where(user.firstName.isNotNull()).fetchOne();
  }

  @Test
  public void singleResult() {
    where(user.firstName.isNotNull()).fetchFirst();
  }

  @Test
  public void longPath() {
    assertThat(query().where(user.mainAddress().city().name.eq("Helsinki")).fetchCount())
        .isEqualTo(2);
    assertThat(query().where(user.mainAddress().city().name.eq("Tampere")).fetchCount())
        .isEqualTo(2);
  }

  @Test
  public void collectionPath() {
    assertThat(query().where(user.addresses.any().street.eq("Aakatu1")).fetchCount()).isEqualTo(1);
    assertThat(query().where(user.addresses.any().street.eq("akatu")).fetchCount()).isEqualTo(0);
  }

  @Test
  public void dates() {
    var current = System.currentTimeMillis();
    var dayInMillis = 24 * 60 * 60 * 1000;
    var start = new Date(current);
    ds.getMapper().getCollection(Dates.class).deleteMany(new org.bson.Document());
    var d = new Dates();
    d.setDate(new Date(current + dayInMillis));
    ds.save(d);
    var end = new Date(current + 2 * dayInMillis);

    var datesDocument = asDocument(d);

    assertThat(query(dates).where(dates.date.between(start, end)).fetchFirst())
        .isEqualTo(datesDocument);
    assertThat(query(dates).where(dates.date.between(new Date(0), start)).fetchCount())
        .isEqualTo(0);
  }

  @Test
  public void elemMatch() {
    //      { "addresses" : { "$elemMatch" : { "street" : "Aakatu1"}}}
    assertThat(
            query()
                .anyEmbedded(user.addresses, address)
                .on(address.street.eq("Aakatu1"))
                .fetchCount())
        .isEqualTo(1);
    //      { "addresses" : { "$elemMatch" : { "street" : "Aakatu1" , "postCode" : "00100"}}}
    assertThat(
            query()
                .anyEmbedded(user.addresses, address)
                .on(address.street.eq("Aakatu1"), address.postCode.eq("00100"))
                .fetchCount())
        .isEqualTo(1);
    //      { "addresses" : { "$elemMatch" : { "street" : "akatu"}}}
    assertThat(
            query()
                .anyEmbedded(user.addresses, address)
                .on(address.street.eq("akatu"))
                .fetchCount())
        .isEqualTo(0);
    //      { "addresses" : { "$elemMatch" : { "street" : "Aakatu1" , "postCode" : "00200"}}}
    assertThat(
            query()
                .anyEmbedded(user.addresses, address)
                .on(address.street.eq("Aakatu1"), address.postCode.eq("00200"))
                .fetchCount())
        .isEqualTo(0);
  }

  @Test
  public void indexedAccess() {
    assertThat(query().where(user.addresses.get(0).street.eq("Aakatu1")).fetchCount()).isEqualTo(1);
    assertThat(query().where(user.addresses.get(1).street.eq("Aakatu1")).fetchCount()).isEqualTo(0);
  }

  @Test
  public void count() {
    assertThat(query().fetchCount()).isEqualTo(4);
  }

  @Test
  public void order() {
    var users = query().orderBy(user.age.asc()).fetch();
    assertThat(users).isEqualTo(asList(u1, u2, u3, u4));

    users = query().orderBy(user.age.desc()).fetch();
    assertThat(users).isEqualTo(asList(u4, u3, u2, u1));
  }

  @Test
  public void restrict() {
    assertThat(query().limit(2).orderBy(user.age.asc()).fetch()).isEqualTo(asList(u1, u2));
    assertThat(query().limit(2).offset(1).orderBy(user.age.asc()).fetch())
        .isEqualTo(asList(u2, u3));
  }

  @Test
  public void listResults() {
    var results = query().limit(2).orderBy(user.age.asc()).fetchResults();
    assertThat(results.getTotal()).isEqualTo(4L);
    assertThat(results.getResults()).hasSize(2);

    results = query().offset(2).orderBy(user.age.asc()).fetchResults();
    assertThat(results.getTotal()).isEqualTo(4L);
    assertThat(results.getResults()).hasSize(2);
  }

  @Test
  public void emptyResults() {
    var results = query().where(user.firstName.eq("XXX")).fetchResults();
    assertThat(results.getTotal()).isEqualTo(0L);
    assertThat(results.getResults()).isEqualTo(Collections.emptyList());
  }

  @Test
  public void eqInAndOrderByQueries() {
    assertQuery(user.firstName.eq("Jaakko"), u1);
    assertQuery(user.firstName.equalsIgnoreCase("jaakko"), u1);
    assertQuery(user.lastName.eq("Aakkonen"), u3);

    assertQuery(user.firstName.in("Jaakko", "Teppo"), u1);
    assertQuery(user.lastName.in("Aakkonen", "BeekkoNen"), u3, u4);

    assertQuery(user.firstName.eq("Jouko"));

    assertQuery(user.firstName.eq("Jaana"), user.lastName.asc(), u3, u4);
    assertQuery(user.firstName.eq("Jaana"), user.lastName.desc(), u4, u3);
    assertQuery(user.lastName.eq("Jantunen"), user.firstName.asc(), u2, u1);
    assertQuery(user.lastName.eq("Jantunen"), user.firstName.desc(), u1, u2);

    assertQuery(user.firstName.eq("Jaana").and(user.lastName.eq("Aakkonen")), u3);
    // This should produce 'and' also
    assertQuery(where(user.firstName.eq("Jaana"), user.lastName.eq("Aakkonen")), u3);

    assertQuery(user.firstName.ne("Jaana"), u2, u1);
  }

  @Test
  public void regexQueries() {
    assertQuery(user.firstName.startsWith("Jaan"), u3, u4);
    assertQuery(user.firstName.startsWith("jaan"));
    assertQuery(user.firstName.startsWithIgnoreCase("jaan"), u3, u4);

    assertQuery(user.lastName.endsWith("unen"), u2, u1);

    assertQuery(user.lastName.endsWithIgnoreCase("onen"), u3, u4);

    assertQuery(user.lastName.contains("oN"), u4);
    assertQuery(user.lastName.containsIgnoreCase("on"), u3, u4);

    assertQuery(user.firstName.matches(".*aa.*[^i]$"), u3, u4, u1);
  }

  @Test
  public void regexQueries_not() {
    assertQuery(user.firstName.startsWith("Jaan").not(), u2, u1);
    assertQuery(user.firstName.startsWith("jaan").not(), u3, u4, u2, u1);
    assertQuery(user.firstName.startsWithIgnoreCase("jaan").not(), u2, u1);

    assertQuery(user.lastName.endsWith("unen").not(), u3, u4);

    assertQuery(user.lastName.endsWithIgnoreCase("onen").not(), u2, u1);

    assertQuery(user.lastName.contains("oN").not(), u3, u2, u1);
    assertQuery(user.lastName.containsIgnoreCase("on").not(), u2, u1);

    assertQuery(user.firstName.matches(".*aa.*[^i]$").not(), u2);
  }

  @Test
  public void like() {
    assertQuery(user.firstName.like("Jaan"));
    assertQuery(user.firstName.like("Jaan%"), u3, u4);
    assertQuery(user.firstName.like("jaan%"));

    assertQuery(user.lastName.like("%unen"), u2, u1);
  }

  @Test
  public void like_not() {
    assertQuery(user.firstName.like("Jaan").not(), u3, u4, u2, u1);
    assertQuery(user.firstName.like("Jaan%").not(), u2, u1);
    assertQuery(user.firstName.like("jaan%").not(), u3, u4, u2, u1);

    assertQuery(user.lastName.like("%unen").not(), u3, u4);
  }

  @Test
  public void likeIgnoreCase() {
    assertQuery(user.firstName.likeIgnoreCase("JAAN"));
    assertQuery(user.firstName.likeIgnoreCase("Jaan%"), u3, u4);
    assertQuery(user.firstName.likeIgnoreCase("JAAN%"), u3, u4);
    assertQuery(user.firstName.likeIgnoreCase("jaan%"), u3, u4);

    assertQuery(user.lastName.likeIgnoreCase("%unen"), u2, u1);
    assertQuery(user.lastName.likeIgnoreCase("%UNEN"), u2, u1);
  }

  @Test
  public void likeIgnoreCase_not() {
    assertQuery(user.firstName.likeIgnoreCase("Jaan").not(), u3, u4, u2, u1);
    assertQuery(user.firstName.likeIgnoreCase("Jaan%").not(), u2, u1);
    assertQuery(user.firstName.likeIgnoreCase("JAAN%").not(), u2, u1);
    assertQuery(user.firstName.likeIgnoreCase("jaan%").not(), u2, u1);

    assertQuery(user.lastName.likeIgnoreCase("%unen").not(), u3, u4);
    assertQuery(user.lastName.likeIgnoreCase("%UNEN").not(), u3, u4);
  }

  @Test
  public void isNotNull() {
    assertQuery(user.firstName.isNotNull(), u3, u4, u2, u1);
  }

  @Test
  public void isNotNull_not() {
    assertQuery(user.firstName.isNotNull().not());
  }

  @Test
  public void isNull() {
    assertQuery(user.firstName.isNull());
  }

  @Test
  public void isNull_not() {
    assertQuery(user.firstName.isNull().not(), u3, u4, u2, u1);
  }

  @Test
  public void isEmpty() {
    assertQuery(user.firstName.isEmpty());
    assertQuery(user.friends.isEmpty(), u1);
  }

  @Test
  public void isEmpty_not() {
    assertQuery(user.firstName.isEmpty().not(), u3, u4, u2, u1);
    assertQuery(user.friends.isEmpty().not(), u3, u4, u2);
  }

  @Test
  public void not() {
    assertQuery(user.firstName.eq("Jaakko").not(), u3, u4, u2);
    assertQuery(user.firstName.ne("Jaakko").not(), u1);
    assertQuery(user.firstName.matches("Jaakko").not(), u3, u4, u2);
    assertQuery(user.friends.isNotEmpty(), u3, u4, u2);
  }

  @Test
  public void or() {
    assertQuery(user.lastName.eq("Aakkonen").or(user.lastName.eq("BeekkoNen")), u3, u4);
  }

  @Test
  public void or_not() {
    assertQuery(user.lastName.eq("Aakkonen").or(user.lastName.eq("BeekkoNen")).not(), u2, u1);
  }

  @Test
  public void iterate() {
    var a = addUser("A", "A");
    var b = addUser("A1", "B");
    var c = addUser("A2", "C");

    Iterator<Document> i =
        where(user.firstName.startsWith("A")).orderBy(user.firstName.asc()).iterate();

    assertThat(i.next().get("_id")).isEqualTo(a.getId());
    assertThat(i.next().get("_id")).isEqualTo(b.getId());
    assertThat(i.next().get("_id")).isEqualTo(c.getId());
    assertThat(i).isExhausted();
  }

  @Test
  public void uniqueResultAndLimitAndOffset() {
    var q = query().where(user.firstName.startsWith("Ja")).orderBy(user.age.asc());
    assertThat(q.fetch()).hasSize(4);
    assertThat(q.fetch().get(0)).isEqualTo(u1);
  }

  @Test
  public void references() {
    for (User u : users) {
      if (u.getFriend() != null) {
        assertQuery(user.friend().eq(u.getFriend()), asDocument(u));
      }
    }
  }

  @Test
  public void references2() {
    for (User u : users) {
      if (u.getFriend() != null) {
        assertQuery(user.enemy().eq(u.getEnemy()), asDocument(u));
      }
    }
  }

  @Test
  public void various() {
    var list = user.addresses;
    var str = user.lastName;
    List<Predicate> predicates = new ArrayList<>();
    predicates.add(str.between("a", "b"));
    predicates.add(str.contains("a"));
    predicates.add(str.containsIgnoreCase("a"));
    predicates.add(str.endsWith("a"));
    predicates.add(str.endsWithIgnoreCase("a"));
    predicates.add(str.eq("a"));
    predicates.add(str.equalsIgnoreCase("a"));
    predicates.add(str.goe("a"));
    predicates.add(str.gt("a"));
    predicates.add(str.in("a", "b", "c"));
    predicates.add(str.isEmpty());
    predicates.add(str.isNotNull());
    predicates.add(str.isNull());
    predicates.add(str.like("a"));
    predicates.add(str.loe("a"));
    predicates.add(str.lt("a"));
    predicates.add(str.matches("a"));
    predicates.add(str.ne("a"));
    predicates.add(str.notBetween("a", "b"));
    predicates.add(str.notIn("a", "b", "c"));
    predicates.add(str.startsWith("a"));
    predicates.add(str.startsWithIgnoreCase("a"));
    predicates.add(list.isEmpty());
    predicates.add(list.isNotEmpty());

    for (Predicate predicate : predicates) {
      var count1 = where(predicate).fetchCount();
      var count2 = where(predicate.not()).fetchCount();
      assertThat(count1 + count2).as(predicate.toString()).isEqualTo(4);
    }
  }

  @Test
  public void enum_eq() {
    assertQuery(user.gender.eq(Gender.MALE), u3, u4, u2, u1);
  }

  @Test
  public void enum_ne() {
    assertQuery(user.gender.ne(Gender.MALE));
  }

  @Test
  public void in_objectIds() {
    var i = new Item();
    i.setCtds(Arrays.asList(ObjectId.get(), ObjectId.get(), ObjectId.get()));
    ds.save(i);

    assertThat(where(item, item.ctds.contains(i.getCtds().getFirst())).fetchCount() > 0).isTrue();
    assertThat(where(item, item.ctds.contains(ObjectId.get())).fetchCount() == 0).isTrue();
  }

  @Test
  public void in_objectIds2() {
    var i = new Item();
    i.setCtds(Arrays.asList(ObjectId.get(), ObjectId.get(), ObjectId.get()));
    ds.save(i);

    assertThat(where(item, item.ctds.any().in(i.getCtds())).fetchCount() > 0).isTrue();
    assertThat(
            where(item, item.ctds.any().in(Arrays.asList(ObjectId.get(), ObjectId.get())))
                    .fetchCount()
                == 0)
        .isTrue();
  }

  @Test
  public void size() {
    assertQuery(user.addresses.size().eq(2), u1);
  }

  @Test
  public void size_not() {
    assertQuery(user.addresses.size().eq(2).not(), u3, u4, u2);
  }

  @Test
  public void readPreference() {
    var query = query();
    query.setReadPreference(ReadPreference.primary());
    assertThat(query.fetchCount()).isEqualTo(4);
  }

  @Test
  public void asDBObject() {
    var query = query();
    query.where(user.firstName.eq("Bob"), user.lastName.eq("Wilson"));
    assertThat(query.asDocument())
        .isEqualTo(new Document().append("firstName", "Bob").append("lastName", "Wilson"));
  }

  private Document asDocument(AbstractEntity entity) {
    return database
        .getCollection(ds.getMapper().getEntityModel(entity.getClass()).getCollectionName())
        .find(new Document("_id", entity.getId()))
        .first();
  }

  private void assertQuery(Predicate e, Document... expected) {
    assertQuery(where(e).orderBy(user.lastName.asc(), user.firstName.asc()), expected);
  }

  private void assertQuery(Predicate e, OrderSpecifier<?> orderBy, Document... expected) {
    assertQuery(where(e).orderBy(orderBy), expected);
  }

  private <T> SimpleMongodbQuery where(EntityPath<T> entity, Predicate... e) {
    return new SimpleMongodbQuery(ds, entity.getType(), database).where(e);
  }

  private SimpleMongodbQuery where(Predicate... e) {
    return query().where(e);
  }

  private SimpleMongodbQuery query() {
    return new SimpleMongodbQuery(ds, user.getType(), database);
  }

  private <T> SimpleMongodbQuery query(EntityPath<T> path) {
    return new SimpleMongodbQuery(ds, path.getType(), database);
  }

  private <T> SimpleMongodbQuery query(Class<? extends T> clazz) {
    return new SimpleMongodbQuery(ds, clazz, database);
  }

  private void assertQuery(SimpleMongodbQuery query, Document... expected) {
    var toString = query.toString();
    var results = query.fetch();

    assertThat(results).as(toString).isNotNull();
    if (expected == null) {
      assertThat(results.size()).as("Should get empty result").isEqualTo(0);
      return;
    }
    assertThat(results.size()).as(toString).isEqualTo(expected.length);
    var i = 0;
    for (Document u : expected) {
      assertThat(results.get(i++)).as(toString).isEqualTo(u);
    }
  }

  private User addUser(String first, String last) {
    var user = new User(first, last);
    ds.save(user);
    return user;
  }

  private User addUser(
      String first, String last, int age, Address mainAddress, Address... addresses) {
    var user = new User(first, last, age, new Date());
    user.setGender(Gender.MALE);
    user.setMainAddress(mainAddress);
    for (Address address : addresses) {
      user.addAddress(address);
    }
    for (User u : users) {
      user.addFriend(u);
    }
    if (!users.isEmpty()) {
      user.setFriend(users.getLast());
      user.setEnemy(users.getLast());
    }
    ds.save(user);
    users.add(user);

    return user;
  }

  private static class SimpleMongodbQuery
      extends AbstractFetchableMongodbQuery<Document, SimpleMongodbQuery> {

    private final Datastore datastore;
    private final MongoDatabase database;

    SimpleMongodbQuery(
        final Datastore datastore, final Class<?> entityType, final MongoDatabase database) {
      super(
          database.getCollection(
              datastore.getMapper().getEntityModel(entityType).getCollectionName()),
          Function.identity(),
          new SampleSerializer(datastore));
      this.datastore = datastore;
      this.database = database;
    }

    @Override
    protected MongoCollection<Document> getCollection(Class<?> type) {
      return database.getCollection(datastore.getMapper().getEntityModel(type).getCollectionName());
    }
  }

  static class SampleSerializer extends MongodbDocumentSerializer {
    private final Datastore morphia;

    SampleSerializer(Datastore morphia) {
      this.morphia = morphia;
    }

    @Override
    protected boolean isReference(Path<?> arg) {
      return arg.getAnnotatedElement().isAnnotationPresent(Reference.class);
    }

    @Override
    protected DBRef asReference(Object entity) {
      Object key = morphia.getMapper().getId(entity);
      return new DBRef(
          morphia.getMapper().getEntityModel(entity.getClass()).getCollectionName(), key);
    }

    @Override
    protected DBRef asReferenceKey(Class<?> entity, Object id) {
      var collection = morphia.getMapper().getEntityModel(entity).getCollectionName();
      return new DBRef(collection, id);
    }

    @Override
    protected String getKeyForPath(Path<?> expr, PathMetadata metadata) {
      var annotations = expr.getAnnotatedElement();
      if (annotations.isAnnotationPresent(Id.class)) {
        Path<?> parent = expr.getMetadata().getParent();
        if (parent.getAnnotatedElement().isAnnotationPresent(Reference.class)) {
          return null; // go to parent
        } else {
          return "_id";
        }
      } else if (annotations.isAnnotationPresent(Property.class)) {
        var property = annotations.getAnnotation(Property.class);
        if (!property.value().equals(Mapper.IGNORED_FIELDNAME)) {
          return property.value();
        }
      } else if (annotations.isAnnotationPresent(Reference.class)) {
        var reference = annotations.getAnnotation(Reference.class);
        if (!reference.value().equals(Mapper.IGNORED_FIELDNAME)) {
          return reference.value();
        }
      }
      return super.getKeyForPath(expr, metadata);
    }
  }

  @Test
  public void userJoinTest() {
    final QUser friend = new QUser("friend");

    // Setup: Create and save users with friends
    User user1 = addUser("Alice", "Johnson");
    User user2 = addUser("Bob", "Smith");
    User user3 = addUser("Charlie", "Brown");

    user1.addFriend(user2);
    user2.addFriend(user3);

    ds.save(user1);
    ds.save(user2);
    ds.save(user3);

    // Query: Find users who have a friend named "Bob" using join
    var results = where().join(user.friends, friend).on(friend.firstName.eq("Bob")).fetch();

    // Assert: Verify the results
    assertThat(results).hasSize(1);
    assertThat(results.get(0).get("firstName")).isEqualTo("Alice");
  }
}
