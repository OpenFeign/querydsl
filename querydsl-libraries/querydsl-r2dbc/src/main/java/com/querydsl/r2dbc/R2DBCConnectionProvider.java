package com.querydsl.r2dbc;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import java.util.Objects;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** R2DBC connection provider */
public interface R2DBCConnectionProvider {

  /**
   * Returns the active connection of the current transaction. Does not create new connection.
   * Consuming the result outside of a transaction throws exception.
   *
   * @return the connection of the current transaction
   */
  Mono<Connection> getConnection();

  /**
   * Release the connection returned from {@link #getConnection()} once the consumer has finished
   * using it. Default implementation is a no-op which is suitable when the provider exposes
   * externally managed connections (for example, a transaction scoped connection).
   *
   * @param connection connection to release
   * @return completion signal for the release
   */
  default Mono<Void> release(Connection connection) {
    return Mono.empty();
  }

  /**
   * Execute the given callback with a managed connection and release it afterwards.
   *
   * @param callback work to perform with the managed connection
   * @param <T> result type
   * @return mono emitting the callback result
   */
  default <T> Mono<T> withConnection(Function<Connection, Mono<T>> callback) {
    Objects.requireNonNull(callback, "callback");
    return Mono.usingWhen(
        getConnection(),
        connection -> Mono.defer(() -> callback.apply(connection)),
        this::release,
        (connection, error) -> release(connection),
        connection -> release(connection));
  }

  /**
   * Execute the given callback that returns a {@link Publisher} sequence with a managed connection
   * and release the connection afterwards.
   *
   * @param callback work to perform with the managed connection
   * @param <T> element type emitted by the publisher
   * @return flux emitting the callback results
   */
  default <T> Flux<T> withConnectionMany(Function<Connection, Publisher<T>> callback) {
    Objects.requireNonNull(callback, "callback");
    return Flux.usingWhen(
        getConnection(),
        connection -> Flux.from(callback.apply(connection)),
        this::release,
        (connection, error) -> release(connection),
        connection -> release(connection));
  }

  /**
   * Create a {@link R2DBCConnectionProvider} backed by a {@link ConnectionFactory}. Each invocation
   * creates a new connection from the factory and ensures it is closed after use.
   *
   * @param connectionFactory source of connections
   * @return provider that creates and closes connections per use
   */
  static R2DBCConnectionProvider from(ConnectionFactory connectionFactory) {
    Objects.requireNonNull(connectionFactory, "connectionFactory");
    return new R2DBCConnectionProvider() {
      @Override
      public Mono<Connection> getConnection() {
        return Mono.from(connectionFactory.create());
      }

      @Override
      public Mono<Void> release(Connection connection) {
        return Mono.from(connection.close());
      }
    };
  }
}
