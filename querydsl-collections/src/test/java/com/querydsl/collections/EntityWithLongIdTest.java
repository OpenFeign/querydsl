package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class EntityWithLongIdTest {

  private List<EntityWithLongId> entities =
      Arrays.asList(
          new EntityWithLongId(999L),
          new EntityWithLongId(1000L),
          new EntityWithLongId(1001L),
          new EntityWithLongId(1003L));

  @Test
  public void simpleEquals() {
    QEntityWithLongId root = QEntityWithLongId.entityWithLongId;
    CollQuery<?> query = new CollQuery<Void>().from(root, entities);
    query.where(root.id.eq(1000L));

    Long found = query.select(root.id).fetchFirst();
    assertThat(found).isNotNull();
    assertThat(1000).isEqualTo(found.longValue());
  }

  @Test
  public void cartesianEquals() {
    QEntityWithLongId root = new QEntityWithLongId("root1");
    QEntityWithLongId root2 = new QEntityWithLongId("root2");
    assertThat(
            new CollQuery<Void>()
                .from(root, entities)
                .from(root2, entities)
                .where(root2.id.eq(root.id))
                .fetchCount())
        .isEqualTo(entities.size());
  }

  @Test
  public void cartesianPlus1() {
    QEntityWithLongId root = new QEntityWithLongId("root1");
    QEntityWithLongId root2 = new QEntityWithLongId("root2");
    assertThat(
            new CollQuery<Void>()
                .from(root, entities)
                .from(root2, entities)
                .where(root2.id.eq(root.id.add(1)))
                .fetchCount())
        .isEqualTo(2);
  }
}
