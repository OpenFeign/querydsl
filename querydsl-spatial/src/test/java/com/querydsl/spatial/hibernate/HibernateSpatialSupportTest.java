package com.querydsl.spatial.hibernate;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Operator;
import com.querydsl.spatial.SpatialOps;
import java.util.Map;
import org.junit.Test;

public class HibernateSpatialSupportTest {

  @Test
  public void allMapped() {
    Map<Operator, String> mapping = HibernateSpatialSupport.getSpatialOps();
    for (Operator operator : SpatialOps.values()) {
      assertThat(mapping.containsKey(operator)).as(operator + " missing").isTrue();
    }
  }
}
