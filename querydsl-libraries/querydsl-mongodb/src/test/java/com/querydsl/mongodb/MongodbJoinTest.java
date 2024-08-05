package com.querydsl.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.querydsl.core.testutil.MongoDB;
import com.querydsl.core.types.Predicate;
import com.querydsl.mongodb.domain.Item;
import com.querydsl.mongodb.domain.QUser;
import com.querydsl.mongodb.domain.User;
import com.querydsl.mongodb.morphia.MorphiaQuery;
import java.net.UnknownHostException;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

@Category(MongoDB.class)
public class MongodbJoinTest {

  private final MongoClient mongo;
  private final Morphia morphia;
  private final Datastore ds;

  private final String dbname = "testdb";
  private final QUser user = QUser.user;
  private final QUser friend = new QUser("friend");
  private final QUser friend2 = new QUser("friend2");
  private final QUser enemy = new QUser("enemy");

  public MongodbJoinTest() throws UnknownHostException, MongoException {
    mongo = new MongoClient();
    morphia = new Morphia().map(User.class).map(Item.class);
    ds = morphia.createDatastore(mongo, dbname);
  }

  @Before
  public void before() throws UnknownHostException, MongoException {
    ds.delete(ds.createQuery(User.class));

    var friend1 = new User("Max", null);
    var friend2 = new User("Jack", null);
    var friend3 = new User("Bob", null);
    ds.save(friend1, friend2, friend3);

    var user1 = new User("Jane", null, friend1);
    var user2 = new User("Mary", null, user1);
    var user3 = new User("Ann", null, friend3);
    ds.save(user1, user2, user3);

    var user4 = new User("Mike", null);
    user4.setFriend(user2);
    user4.setEnemy(user3);
    ds.save(user4);

    var user5 = new User("Bart", null);
    user5.addFriend(user2);
    user5.addFriend(user3);
    ds.save(user5);
  }

  @Test
  public void count() {
    assertThat(where().join(user.friend(), friend).on(friend.firstName.eq("Max")).fetchCount())
        .isEqualTo(1);
    assertThat(
            where(user.firstName.eq("Jane"))
                .join(user.friend(), friend)
                .on(friend.firstName.eq("Max"))
                .fetchCount())
        .isEqualTo(1);
    assertThat(
            where(user.firstName.eq("Mary"))
                .join(user.friend(), friend)
                .on(friend.firstName.eq("Max"))
                .fetchCount())
        .isEqualTo(0);
    assertThat(
            where(user.firstName.eq("Jane"))
                .join(user.friend(), friend)
                .on(friend.firstName.eq("Jack"))
                .fetchCount())
        .isEqualTo(0);
  }

  @Test
  public void count_collection() {
    assertThat(where().join(user.friends, friend).on(friend.firstName.eq("Mary")).fetchCount())
        .isEqualTo(1);
    assertThat(where().join(user.friends, friend).on(friend.firstName.eq("Ann")).fetchCount())
        .isEqualTo(1);
    assertThat(
            where()
                .join(user.friends, friend)
                .on(friend.firstName.eq("Ann").or(friend.firstName.eq("Mary")))
                .fetchCount())
        .isEqualTo(1);
    assertThat(
            where(user.firstName.eq("Bart"))
                .join(user.friends, friend)
                .on(friend.firstName.eq("Mary"))
                .fetchCount())
        .isEqualTo(1);
    assertThat(where().join(user.friends, friend).on(friend.firstName.eq("Max")).fetchCount())
        .isEqualTo(0);
  }

  @Test
  public void exists() {
    assertThat(where().join(user.friend(), friend).on(friend.firstName.eq("Max")).fetchCount() > 0)
        .isTrue();
    assertThat(
            where(user.firstName.eq("Jane"))
                    .join(user.friend(), friend)
                    .on(friend.firstName.eq("Max"))
                    .fetchCount()
                > 0)
        .isTrue();
    assertThat(
            where(user.firstName.eq("Mary"))
                    .join(user.friend(), friend)
                    .on(friend.firstName.eq("Max"))
                    .fetchCount()
                > 0)
        .isFalse();
    assertThat(
            where(user.firstName.eq("Jane"))
                    .join(user.friend(), friend)
                    .on(friend.firstName.eq("Jack"))
                    .fetchCount()
                > 0)
        .isFalse();
  }

  @Test
  public void exists_collection() {
    assertThat(where().join(user.friends, friend).on(friend.firstName.eq("Mary")).fetchCount() > 0)
        .isTrue();
    assertThat(
            where(user.firstName.eq("Bart"))
                    .join(user.friends, friend)
                    .on(friend.firstName.eq("Mary"))
                    .fetchCount()
                > 0)
        .isTrue();
  }

  @Test
  public void list() {
    assertThat(where().join(user.friend(), friend).on(friend.firstName.eq("Max")).fetch())
        .hasSize(1);
    assertThat(
            where(user.firstName.eq("Jane"))
                .join(user.friend(), friend)
                .on(friend.firstName.eq("Max"))
                .fetch())
        .hasSize(1);
    assertThat(
            where(user.firstName.eq("Mary"))
                .join(user.friend(), friend)
                .on(friend.firstName.eq("Max"))
                .fetch())
        .isEmpty();
    assertThat(
            where(user.firstName.eq("Jane"))
                .join(user.friend(), friend)
                .on(friend.firstName.eq("Jack"))
                .fetch())
        .isEmpty();
  }

  public void list_collection() {
    assertThat(where().join(user.friends, friend).on(friend.firstName.eq("Mary")).fetch())
        .hasSize(1);
  }

  @Test
  public void single() {
    assertThat(
            where()
                .join(user.friend(), friend)
                .on(friend.firstName.eq("Max"))
                .fetchFirst()
                .getFirstName())
        .isEqualTo("Jane");
    assertThat(
            where(user.firstName.eq("Jane"))
                .join(user.friend(), friend)
                .on(friend.firstName.eq("Max"))
                .fetchFirst()
                .getFirstName())
        .isEqualTo("Jane");
    assertThat(
            where(user.firstName.eq("Mary"))
                .join(user.friend(), friend)
                .on(friend.firstName.eq("Max"))
                .fetchFirst())
        .isNull();
    assertThat(
            where(user.firstName.eq("Jane"))
                .join(user.friend(), friend)
                .on(friend.firstName.eq("Jack"))
                .fetchFirst())
        .isNull();
  }

  @Test
  public void single_collection() {
    assertThat(
            where()
                .join(user.friends, friend)
                .on(friend.firstName.eq("Mary"))
                .fetchFirst()
                .getFirstName())
        .isEqualTo("Bart");
  }

  @Test
  public void double1() {
    assertThat(
            where()
                .join(user.friend(), friend)
                .on(friend.firstName.isNotNull())
                .join(user.enemy(), enemy)
                .on(enemy.firstName.isNotNull())
                .fetchFirst()
                .getFirstName())
        .isEqualTo("Mike");
  }

  @Test
  public void double2() {
    assertThat(
            where()
                .join(user.friend(), friend)
                .on(friend.firstName.eq("Mary"))
                .join(user.enemy(), enemy)
                .on(enemy.firstName.eq("Ann"))
                .fetchFirst()
                .getFirstName())
        .isEqualTo("Mike");
  }

  @Test
  public void deep() {
    // Mike -> Mary -> Jane
    assertThat(
            where()
                .join(user.friend(), friend)
                .on(friend.firstName.isNotNull())
                .join(friend.friend(), friend2)
                .on(friend2.firstName.eq("Jane"))
                .fetchFirst()
                .getFirstName())
        .isEqualTo("Mike");
  }

  private MorphiaQuery<User> query() {
    return new MorphiaQuery<>(morphia, ds, user);
  }

  private MorphiaQuery<User> where(Predicate... e) {
    return query().where(e);
  }
}
