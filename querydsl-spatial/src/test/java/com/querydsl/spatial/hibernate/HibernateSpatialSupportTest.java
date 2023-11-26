package com.querydsl.spatial.hibernate;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.querydsl.core.types.Operator;
import com.querydsl.spatial.SpatialOps;
import java.util.Map;
import org.junit.Test;

public class HibernateSpatialSupportTest {

  @Test
  public void allMapped() {
    Map<Operator, String> mapping = HibernateSpatialSupport.getSpatialOps();
    for (Operator operator : SpatialOps.values()) {
      assertTrue(mapping.containsKey(operator), operator + " missing");
    }
  }
}
