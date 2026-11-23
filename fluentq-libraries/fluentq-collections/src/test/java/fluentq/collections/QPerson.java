package fluentq.collections;

import fluentq.core.types.PathMetadata;
import fluentq.core.types.dsl.BeanPath;
import fluentq.core.types.dsl.StringPath;

public class QPerson extends BeanPath<Person> {

  public final StringPath name = createString("name");

  public static QPerson car = new QPerson(new BeanPath<>(Person.class, "person"));

  public QPerson(BeanPath<? extends Person> entity) {
    super(entity.getType(), entity.getMetadata());
  }

  public QPerson(PathMetadata metadata) {
    super(Person.class, metadata);
  }
}
