package com.querydsl.example.reactive;

import static io.quarkus.vertx.web.Route.HandlerType.FAILURE;
import static io.quarkus.vertx.web.Route.HttpMethod.DELETE;
import static io.quarkus.vertx.web.Route.HttpMethod.GET;
import static io.quarkus.vertx.web.Route.HttpMethod.POST;
import static io.quarkus.vertx.web.Route.HttpMethod.PUT;

import com.querydsl.jpa.hibernate.MutinyQueryFactory;
import io.quarkus.vertx.web.Body;
import io.quarkus.vertx.web.Param;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RouteBase;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import java.util.List;
import java.util.NoSuchElementException;
import org.hibernate.reactive.mutiny.Mutiny;
import org.jboss.logging.Logger;

/** An example using Vert.x Web, Hibernate Reactive and Mutiny. */
@RouteBase(path = "/fruits", produces = "application/json")
public class FruitsRoutes {

  private static final Logger LOGGER = Logger.getLogger(FruitsRoutes.class.getName());

  private final Mutiny.SessionFactory sf;
  private final MutinyQueryFactory qf;

  @Inject
  public FruitsRoutes(Mutiny.SessionFactory sf) {
    this.sf = sf;
    qf = new MutinyQueryFactory(sf);
  }

  private static final QFruit f = new QFruit("f");

  @Route(methods = GET, path = "/")
  public Uni<List<Fruit>> getAll() {
    return qf.withQuery(query -> query.select(f).from(f).orderBy(f.name.asc()).fetch());
  }

  @Route(methods = GET, path = "/:id")
  public Uni<Fruit> getSingle(@Param Integer id) {
    return qf.withQuery(query -> query.select(f).from(f).where(f.id.eq(id)).fetchOne());
  }

  @Route(methods = POST, path = "/")
  public Uni<Fruit> create(@Body Fruit fruit, HttpServerResponse response) {
    if (fruit == null || fruit.getId() != null) {
      return Uni.createFrom()
          .failure(new IllegalArgumentException("Fruit id invalidly set on request."));
    }
    return sf.withTransaction((session, tx) -> session.persist(fruit))
        .invoke(() -> response.setStatusCode(201))
        .replaceWith(fruit);
  }

  @Route(methods = PUT, path = "/:id")
  public Uni<Fruit> update(@Body Fruit fruit, @Param Integer id) {
    if (fruit == null || fruit.getName() == null) {
      return Uni.createFrom()
          .failure(new IllegalArgumentException("Fruit name was not set on request."));
    }
    return qf.withUpdate(
            f, update -> update.where(f.id.eq(id)).set(f.name, fruit.getName()).execute())
        .onItem()
        .ifNull()
        .fail()
        .flatMap(a -> qf.withSelectFrom(f, select -> select.where(f.id.eq(id)).fetchOne()));
  }

  @Route(methods = DELETE, path = "/:id")
  public Uni<Integer> delete(@Param Integer id, HttpServerResponse response) {
    return qf.withDelete(f, delete -> delete.where(f.id.eq(id)).execute())
        .invoke(() -> response.setStatusCode(204))
        .onItem()
        .ifNull()
        .fail();
  }

  @Route(path = "/*", type = FAILURE)
  public void error(RoutingContext context) {
    Throwable t = context.failure();
    if (t != null) {
      LOGGER.error("Failed to handle request", t);
      int status = context.statusCode();
      String chunk = "";
      if (t instanceof NoSuchElementException) {
        status = 404;
      } else if (t instanceof IllegalArgumentException) {
        status = 422;
        chunk =
            new JsonObject()
                .put("code", status)
                .put("exceptionType", t.getClass().getName())
                .put("error", t.getMessage())
                .encode();
      }
      context.response().setStatusCode(status).end(chunk);
    } else {
      // Continue with the default error handler
      context.next();
    }
  }
}
