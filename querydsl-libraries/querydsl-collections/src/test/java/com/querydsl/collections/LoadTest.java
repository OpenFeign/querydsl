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
package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.testutil.Performance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Performance.class)
public class LoadTest {

  private QCat cat = QCat.cat;

  private DefaultEvaluatorFactory evaluatorFactory =
      new DefaultEvaluatorFactory(CollQueryTemplates.DEFAULT);

  private QueryMetadata metadata = new DefaultQueryMetadata();

  @Test
  public void creation() {
    System.out.println("Evaluator creation #1");
    for (int i = 0; i < 5; i++) {
      long s = System.currentTimeMillis();
      evaluatorFactory.create(metadata, Collections.singletonList(cat), cat.name.startsWith("Bob"));
      long e = System.currentTimeMillis();
      System.out.println(" " + (e - s) + "ms");
    }
    System.out.println();

    System.out.println("Evaluator creation #2");
    for (int i = 0; i < 5; i++) {
      long s = System.currentTimeMillis();
      evaluatorFactory.create(
          metadata, Collections.singletonList(cat), cat.name.startsWith("Bob" + i));
      long e = System.currentTimeMillis();
      System.out.println(" " + (e - s) + "ms");
    }
    System.out.println();
  }

  @Test
  public void test() {
    List<Cat> data = new ArrayList<Cat>(5000);
    for (int i = 0; i < 1000; i++) {
      data.addAll(
          Arrays.asList(
              new Cat("Bob" + i),
              new Cat("Ruth" + i),
              new Cat("Felix" + i),
              new Cat("Allen" + i),
              new Cat("Mary" + i)));
    }

    // #1
    System.out.println("Querydsl iteration");
    for (int i = 0; i < 5; i++) {
      long s1 = System.currentTimeMillis();
      List<Cat> bobs1 = CollQueryFactory.from(cat, data).where(cat.name.startsWith("Bob")).fetch();
      assertThat(bobs1).hasSize(1000);
      long e1 = System.currentTimeMillis();
      System.out.println(" " + (e1 - s1) + "ms");
    }
    System.out.println();

    // #2
    System.out.println("Normal iteration");
    for (int i = 0; i < 5; i++) {
      long s2 = System.currentTimeMillis();
      List<Cat> bobs2 = new ArrayList<Cat>();
      for (Cat c : data) {
        if (c.getName().startsWith("Bob")) {
          bobs2.add(c);
        }
      }
      assertThat(bobs2).hasSize(1000);
      long e2 = System.currentTimeMillis();
      System.out.println(" " + (e2 - s2) + "ms");
    }
    System.out.println();
  }
}
