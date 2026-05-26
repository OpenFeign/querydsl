package com.querydsl.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClients;
import com.querydsl.core.types.Predicate;
import com.querydsl.mongodb.domain.Chips;
import com.querydsl.mongodb.domain.Fish;
import com.querydsl.mongodb.domain.Food;
import com.querydsl.mongodb.domain.QFish;
import com.querydsl.mongodb.domain.QFood;
import com.querydsl.mongodb.morphia.MorphiaQuery;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import java.net.UnknownHostException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("com.querydsl.core.testutil.MongoDB")
public class MongodbPolymorphicCollectionTest {

  private final Datastore ds;
  private final Fish f1 = new Fish("f1");
  private final Fish f2 = new Fish("f2");
  private final Chips c1 = new Chips("c1");

  public MongodbPolymorphicCollectionTest() throws UnknownHostException, MongoException {
    final var mongo = MongoClients.create();
    ds = Morphia.createDatastore(mongo, "testdb");
    ds.getMapper().map(Food.class);
  }

  @BeforeEach
  void before() throws UnknownHostException, MongoException {
    ds.getCollection(Food.class).deleteMany(new org.bson.Document());
    ds.getCollection(Chips.class).deleteMany(new org.bson.Document());
    ds.getCollection(Fish.class).deleteMany(new org.bson.Document());

    ds.save(List.of(f1, f2, c1));
  }

  @Test
  void basicCount() {
    assertThat(3).isEqualTo(where().fetchCount());
  }

  @Test
  void countFishFromName() {
    assertThat(1).isEqualTo(where(QFood.food.name.eq("f1")).fetchCount());
  }

  @Test
  void countFishFromNameAndBreed() {
    assertThat(1)
        .isEqualTo(
            where(QFood.food.name.eq("f1").and(QFish.fish.breed.eq("unknown"))).fetchCount());
  }

  @Test
  void countFishFromNameAndBreedWithCast() {
    assertThat(1)
        .isEqualTo(
            where(QFood.food.name.eq("f1").and(QFood.food.as(QFish.class).breed.eq("unknown")))
                .fetchCount());
  }

  @Test
  void countFishes() {
    assertThat(2).isEqualTo(where(isFish()).fetchCount());
  }

  private Predicate isFish() {
    return QFood.food.name.startsWith("f");
  }

  private MorphiaQuery<Food> query() {
    return new MorphiaQuery<>(ds, QFood.food);
  }

  private MorphiaQuery<Food> where(final Predicate... e) {
    return query().where(e);
  }
}
