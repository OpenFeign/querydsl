package com.querydsl.example.dao;

import com.querydsl.example.config.TestConfiguration;
import com.querydsl.example.config.TestDataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public abstract class AbstractDaoTest {

  @Autowired TestDataService testDataService;

  @BeforeEach
  public void setUp() {
    testDataService.addTestData();
  }

  @AfterEach
  public void tearDown() throws Exception {
    testDataService.removeTestData();
  }
}
