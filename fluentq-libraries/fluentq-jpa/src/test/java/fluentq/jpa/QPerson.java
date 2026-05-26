package fluentq.jpa;

import static fluentq.core.types.PathMetadataFactory.forVariable;

import fluentq.core.types.Path;
import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.EntityPathBase;
import fluentq.core.types.dsl.StringPath;
import jakarta.annotation.Generated;

/** QPerson is a FluentQ query type for Person */
@Generated("fluentq.codegen.EntitySerializer")
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
