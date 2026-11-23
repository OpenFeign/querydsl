package fluentq.apt.domain;

import fluentq.core.annotations.QueryEntity;

@QueryEntity
public class Subclass extends fluentq.core.domain.Superclass {

  private int number;

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }
}
