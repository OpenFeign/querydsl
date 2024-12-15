package com.querydsl.jpa;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;
import jakarta.annotation.Generated;

/** QPerson is a Querydsl query type for Person */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QPerson extends EntityPathBase<Person> {

  private static final long serialVersionUID = -219463259L;

  public static final QPerson person = new QPerson("person");

  public final StringPath firstName = createString("firstName");

  public final StringPath lastName = createString("lastName");

  public QPerson(String variable) {
    super(Person.class, forVariable(variable));
  }

  public QPerson(Path<? extends Person> path) {
    super(path.getType(), path.getMetadata());
  }

  public QPerson(PathMetadata metadata) {
    super(Person.class, metadata);
  }
}
