package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.MappingProjection;
import org.junit.Test;

@SuppressWarnings("serial")
public class MappingProjectionTest extends AbstractQueryTest {

  public class ResultPart {}

  public class ResultObject {}

  @Test
  public void test() {
    final MappingProjection<ResultPart> key =
        new MappingProjection<>(ResultPart.class, cat.name) {

          @Override
          protected ResultPart map(Tuple row) {
            return new ResultPart();
          }
        };

    var list =
        query()
            .from(cat, cats)
            .select(
                new MappingProjection<>(ResultObject.class, key) {

                  @Override
                  protected ResultObject map(Tuple row) {
                    ResultPart consolidationKey = row.get(key);
                    return new ResultObject();
                  }
                })
            .fetch();

    assertThat(list).hasSize(cats.size());
  }
}
