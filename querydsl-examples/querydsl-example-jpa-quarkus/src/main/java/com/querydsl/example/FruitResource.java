package com.querydsl.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import org.jboss.logging.Logger;

@Path("fruits")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FruitResource {

  private static final QFruit f = new QFruit("f");

  private static final Logger LOGGER = Logger.getLogger(FruitResource.class.getName());
  private final JPAQueryFactory queryFactory;

  private final EntityManager entityManager;

  @Inject
  public FruitResource(EntityManager entityManager) {
    this.entityManager = entityManager;
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  @GET
  public List<Fruit> get() {
    return queryFactory.selectFrom(f).fetch();
  }

  @GET
  @Path("{id}")
  public Fruit getSingle(Integer id) {
    var entity = queryFactory.selectFrom(f).where(f.id.eq(id)).fetchOne();
    if (entity == null) {
      throw new WebApplicationException("Fruit with id of " + id + " does not exist.", 404);
    }
    return entity;
  }

  @POST
  @Transactional
  public Response create(Fruit fruit) {
    if (fruit.getId() != null) {
      throw new WebApplicationException("Id was invalidly set on request.", 422);
    }

    entityManager.persist(fruit);
    return Response.ok(fruit).status(201).build();
  }

  @PUT
  @Path("{id}")
  @Transactional
  public Fruit update(Integer id, Fruit fruit) {
    if (fruit.getName() == null) {
      throw new WebApplicationException("Fruit Name was not set on request.", 422);
    }

    var entity = getSingle(id);
    entity.setName(fruit.getName());

    return entity;
  }

  @DELETE
  @Path("{id}")
  @Transactional
  public Response delete(Integer id) {
    var modified = queryFactory.delete(f).where(f.id.eq(id)).execute();
    if (modified == 0L) {
      throw new WebApplicationException("Fruit with id of " + id + " does not exist.", 404);
    }
    return Response.status(204).build();
  }

  @Provider
  public static class ErrorMapper implements ExceptionMapper<Exception> {

    @Inject ObjectMapper objectMapper;

    @Override
    public Response toResponse(Exception exception) {
      LOGGER.error("Failed to handle request", exception);

      var code = 500;
      if (exception instanceof WebApplicationException applicationException) {
        code = applicationException.getResponse().getStatus();
      }

      var exceptionJson = objectMapper.createObjectNode();
      exceptionJson.put("exceptionType", exception.getClass().getName());
      exceptionJson.put("code", code);

      if (exception.getMessage() != null) {
        exceptionJson.put("error", exception.getMessage());
      }

      return Response.status(code).entity(exceptionJson).build();
    }
  }
}
