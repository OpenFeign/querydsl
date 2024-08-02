package com.querydsl.example.sql.guice;

import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

public class ConnectionContext {

  private final DataSource dataSource;

  private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

  @Inject
  public ConnectionContext(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Connection getConnection(boolean create) {
    var connection = connectionHolder.get();
    if (!create || connection != null) {
      return connection;
    }
    try {
      connection = dataSource.getConnection();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    connectionHolder.set(connection);
    return connection;
  }

  public Connection getConnection() {
    return connectionHolder.get();
  }

  public void removeConnection() {
    connectionHolder.remove();
  }
}
