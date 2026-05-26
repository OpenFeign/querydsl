package fluentq.core.domain2;

import fluentq.core.annotations.QuerySupertype;

@QuerySupertype
public abstract class ABase {
  private Long id;

  private TenantImpl tenant;

  public Long getId() {
    return id;
  }

  protected void setId(Long id) {
    this.id = id;
  }

  public TenantImpl getTenant() {
    return tenant;
  }

  public void setTenant(TenantImpl tenant) {}
}
