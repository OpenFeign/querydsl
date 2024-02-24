package com.querydsl.example.dao;

import com.querydsl.example.config.TestConfiguration;
import com.querydsl.example.config.TestDataService;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public abstract class AbstractDaoTest {

  @Autowired TestDataService testDataService;

  @Before
  public void setUp() {
    testDataService.addTestData();
  }

  @After
  public void tearDown() throws Exception {
    testDataService.removeTestData();
  }
}
