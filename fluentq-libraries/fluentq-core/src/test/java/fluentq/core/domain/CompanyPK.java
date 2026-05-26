package fluentq.core.domain;

import fluentq.core.annotations.QueryEmbeddable;

@QueryEmbeddable
public class CompanyPK {

  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
