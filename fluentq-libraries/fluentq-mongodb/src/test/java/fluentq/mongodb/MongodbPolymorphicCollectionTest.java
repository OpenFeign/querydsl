package fluentq.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import fluentq.core.types.Predicate;
import fluentq.mongodb.domain.Chips;
import fluentq.mongodb.domain.Fish;
import fluentq.mongodb.domain.Food;
import fluentq.mongodb.domain.QFish;
import fluentq.mongodb.domain.QFood;
import fluentq.mongodb.morphia.MorphiaQuery;
import java.net.UnknownHostException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("fluentq.core.testutil.MongoDB")
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
