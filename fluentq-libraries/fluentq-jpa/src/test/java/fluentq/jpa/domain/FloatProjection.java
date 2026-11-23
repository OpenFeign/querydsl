package fluentq.jpa.domain;

import fluentq.core.annotations.QueryProjection;

public class FloatProjection {

  public float val;

  @QueryProjection
  public FloatProjection(float val) {
    this.val = val;
  }
}
