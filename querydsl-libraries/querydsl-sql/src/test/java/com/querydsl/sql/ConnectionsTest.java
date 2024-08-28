package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import org.geolatte.geom.codec.Wkt;
import org.junit.Test;

public class ConnectionsTest {

  @Test
  public void valid_wkt() {
    for (String wkt : Connections.getSpatialData().values()) {
      assertThat(Wkt.newDecoder(Wkt.Dialect.POSTGIS_EWKT_1).decode(wkt)).isNotNull();
    }
  }
}
