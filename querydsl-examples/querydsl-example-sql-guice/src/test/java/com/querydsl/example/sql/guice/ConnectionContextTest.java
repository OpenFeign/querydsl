package com.querydsl.example.sql.guice;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(GuiceTestRunner.class)
public class ConnectionContextTest {

  @Inject private ConnectionContext context;

  @After
  public void tearDown() {
    context.removeConnection();
  }

  @Test
  public void get_connection() {
    assertThat(context.getConnection(true)).isNotNull();
    assertThat(context.getConnection()).isNotNull();
    context.removeConnection();
    assertThat(context.getConnection()).isNull();
  }
}
