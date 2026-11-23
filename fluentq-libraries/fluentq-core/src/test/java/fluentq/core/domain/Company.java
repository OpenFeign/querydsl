package fluentq.core.domain;

import fluentq.core.annotations.QueryEntity;

@QueryEntity
public class Company {

  private CompanyPK key;

  public CompanyPK getKey() {
    return key;
  }

  public void setKey(final CompanyPK aKey) {
    this.key = aKey;
  }
}
