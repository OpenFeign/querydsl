package fluentq.jpa.domain;

import fluentq.core.annotations.QueryProjection;

public class DoubleProjection {

  public double val;

  @QueryProjection
  public DoubleProjection(double val) {
    this.val = val;
  }
}
