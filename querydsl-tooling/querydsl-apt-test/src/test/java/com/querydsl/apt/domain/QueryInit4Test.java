package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.annotations.QueryEntity;
import com.querydsl.core.annotations.QueryInit;
import java.sql.Date;
import java.util.Set;
import org.junit.Test;

public class QueryInit4Test {

  @QueryEntity
  public static class Organization {}

  @QueryEntity
  public static class Application {}

  @QueryEntity
  public static class Tenant {
    Long id;

    String tenantBusinessKey;

    String sourceSystemKey;

    String tenantName;

    @QueryInit({"user.primaryTenant", "tenant"})
    Set<UserTenantApplication> userTenantApplications;

    Set<Organization> organizations;

    Date lastModifiedDate;

    Long lastModifiedUserId;
  }

  @QueryEntity
  public static class UserTenantApplication {

    User user;

    Tenant tenant;

    Application application;

    Date lastModifiedDate;

    Long lastModifiedUserId;
  }

  @QueryEntity
  public static class User {
    Long id;

    Tenant primaryTenant;

    Set<UserTenantApplication> userTenantApplications;
  }

  @Test
  public void test() {
    var tenant = QQueryInit4Test_Tenant.tenant;
    assertThat(tenant.userTenantApplications.any().user.id).isNotNull();
    assertThat(tenant.userTenantApplications.any().tenant.id).isNotNull();
    assertThat(tenant.userTenantApplications.any().user.primaryTenant.id).isNotNull();
  }
}
