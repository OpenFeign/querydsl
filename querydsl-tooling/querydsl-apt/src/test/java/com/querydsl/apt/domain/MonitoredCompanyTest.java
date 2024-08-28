package com.querydsl.apt.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class MonitoredCompanyTest {

  @Test
  public void test() {
    var monitoredCompany = QMonitoredCompany.monitoredCompany;
    assertThat(monitoredCompany.companyGroup).isNotNull();
    assertThat(monitoredCompany.companyGroup.mainCompany).isNotNull();
  }
}
