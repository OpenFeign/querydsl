package fluentq.maven;

import fluentq.core.annotations.QueryEntity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@jakarta.persistence.Entity
@QueryEntity
public class Entity {

  String property;

  @Temporal(TemporalType.TIMESTAMP)
  Date annotatedProperty;
}
