package com.querydsl.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.querydsl.core.testutil.MongoDB;
import com.querydsl.core.types.Predicate;
import com.querydsl.mongodb.domain.Chips;
import com.querydsl.mongodb.domain.Fish;
import com.querydsl.mongodb.domain.Food;
import com.querydsl.mongodb.domain.QFish;
import com.querydsl.mongodb.domain.QFood;
import com.querydsl.mongodb.morphia.MorphiaQuery;
import java.net.UnknownHostException;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

@Category(MongoDB.class)
public class MongodbPolymorphicCollectionTest {

  private final Morphia morphia;
  private final Datastore ds;
  private final Fish f1 = new Fish("f1");
  private final Fish f2 = new Fish("f2");
  private final Chips c1 = new Chips("c1");

  public MongodbPolymorphicCollectionTest() throws UnknownHostException, MongoException {
    final var mongo = new MongoClient();
    morphia = new Morphia().map(Food.class);
    ds = morphia.createDatastore(mongo, "testdb");
  }

  @Before
  public void before() throws UnknownHostException, MongoException {
    ds.delete(ds.createQuery(Food.class));
    ds.save(f1, f2, c1);
  }

  @Test
  public void basicCount() {
    assertThat(3).isEqualTo(where().fetchCount());
  }

  @Test
  public void countFishFromName() {
    assertThat(1).isEqualTo(where(QFood.food.name.eq("f1")).fetchCount());
  }

  @Test
  public void countFishFromNameAndBreed() {
    assertThat(1)
        .isEqualTo(
            where(QFood.food.name.eq("f1").and(QFish.fish.breed.eq("unknown"))).fetchCount());
  }

  @Test
  public void countFishFromNameAndBreedWithCast() {
    assertThat(1)
        .isEqualTo(
            where(QFood.food.name.eq("f1").and(QFood.food.as(QFish.class).breed.eq("unknown")))
                .fetchCount());
  }

  @Test
  public void countFishes() {
    assertThat(2).isEqualTo(where(isFish()).fetchCount());
  }

  private Predicate isFish() {
    return QFood.food.name.startsWith("f");
  }

  private MorphiaQuery<Food> query() {
    return new MorphiaQuery<>(morphia, ds, QFood.food);
  }

  private MorphiaQuery<Food> where(final Predicate... e) {
    return query().where(e);
  }
}
