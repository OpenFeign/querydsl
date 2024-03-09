/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.r2dbc;

import static com.querydsl.r2dbc.Constants.survey;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class LikeEscapeBase extends AbstractBaseTest {

  @Before
  public void setUp() {
    delete(survey).execute().block();
    insert(survey)
        .set(survey.id, 5)
        .set(survey.name, "aaa")
        .execute()
        .then(insert(survey).set(survey.id, 6).set(survey.name, "a_").execute())
        .then(insert(survey).set(survey.id, 7).set(survey.name, "a%").execute())
        .block();
  }

  @After
  public void tearDown() {
    delete(survey).execute().block();
    insert(survey).values(1, "Hello World", "Hello").execute().block();
  }

  @Test
  public void like() {
    assertThat((long) query().from(survey).where(survey.name.like("a!%")).fetchCount().block())
        .isEqualTo(0);
    assertThat((long) query().from(survey).where(survey.name.like("a!_")).fetchCount().block())
        .isEqualTo(0);
    assertThat((long) query().from(survey).where(survey.name.like("a%")).fetchCount().block())
        .isEqualTo(3);
    assertThat((long) query().from(survey).where(survey.name.like("a_")).fetchCount().block())
        .isEqualTo(2);

    assertThat((long) query().from(survey).where(survey.name.startsWith("a_")).fetchCount().block())
        .isEqualTo(1);
    assertThat((long) query().from(survey).where(survey.name.startsWith("a%")).fetchCount().block())
        .isEqualTo(1);
  }

  @Test
  public void like_with_escape() {
    assertThat((long) query().from(survey).where(survey.name.like("a!%", '!')).fetchCount().block())
        .isEqualTo(1);
    assertThat((long) query().from(survey).where(survey.name.like("a!_", '!')).fetchCount().block())
        .isEqualTo(1);
    assertThat((long) query().from(survey).where(survey.name.like("a%", '!')).fetchCount().block())
        .isEqualTo(3);
    assertThat((long) query().from(survey).where(survey.name.like("a_", '!')).fetchCount().block())
        .isEqualTo(2);
  }

  @Test
  public void like_escaping_conclusion() {
    assertThat(
            query().from(survey).where(survey.name.like("a!%")).fetchCount().block()
                < query().from(survey).where(survey.name.like("a!%", '!')).fetchCount().block())
        .as("Escaped like construct must return more results")
        .isTrue();
    assertThat(
            query().from(survey).where(survey.name.like("a!_")).fetchCount().block()
                < query().from(survey).where(survey.name.like("a!_", '!')).fetchCount().block())
        .as("Escaped like construct must return more results")
        .isTrue();
  }
}
