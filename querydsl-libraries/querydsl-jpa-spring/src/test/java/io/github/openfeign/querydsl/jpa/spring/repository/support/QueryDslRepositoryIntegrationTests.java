/*
 * Copyright 2018-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.openfeign.querydsl.jpa.spring.repository.support;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.openfeign.querydsl.jpa.spring.repository.Config;
import io.github.openfeign.querydsl.jpa.spring.repository.sample.Country;
import io.github.openfeign.querydsl.jpa.spring.repository.sample.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Config.class)
class QueryDslRepositoryIntegrationTests {

  @Autowired CountryRepository countryRepository;

  @BeforeEach
  void setUp() {
    countryRepository.deleteAll();
  }

  @Test
  void testWithQueryDsl() {

    var de = new Country();
    de.code = "de";
    de.name = "Deutschland";

    countryRepository.save(de);

    var found = countryRepository.findOneBy("Deutschland");

    assertThat(found).isNotNull();
    assertThat(found.id).isEqualTo(de.id);
  }
}
