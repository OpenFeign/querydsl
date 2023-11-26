package com.querydsl.apt.domain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.Test;

public class MonitoredCompanyTest {

  @Test
  public void test() {
    QMonitoredCompany monitoredCompany = QMonitoredCompany.monitoredCompany;
    assertNotNull(monitoredCompany.companyGroup);
    assertNotNull(monitoredCompany.companyGroup.mainCompany);
  }
}
