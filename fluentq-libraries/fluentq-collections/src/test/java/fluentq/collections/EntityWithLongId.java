package fluentq.collections;

import fluentq.core.annotations.QueryEntity;
import fluentq.core.annotations.QueryProjection;

@QueryEntity
public class EntityWithLongId {

  private Long id;

  @QueryProjection
  public EntityWithLongId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
