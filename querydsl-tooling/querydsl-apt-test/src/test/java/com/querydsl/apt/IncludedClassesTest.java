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
package com.querydsl.apt;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import org.junit.Test;

public class IncludedClassesTest extends AbstractProcessorTest {

  private static final String packagePath = "src/test/java/com/querydsl/";

  @Test
  public void process() throws IOException {
    var classes = getFiles(packagePath);
    process(QuerydslAnnotationProcessor.class, classes, "includedClasses");

    assertThat(
            new File(
                "target/includedClasses/com/querydsl/apt/domain/QArrayTest_ArrayTestEntity.java"))
        .exists();
    assertThat(
            new File("target/includedClasses/com/querydsl/apt/domain/QArray2Test_Example.java")
                .exists())
        .isFalse();
  }

  @Override
  protected Collection<String> getAPTOptions() {
    return Collections.singletonList(
        "-Aquerydsl.includedClasses=com.querydsl.apt.domain.ArrayTest.ArrayTestEntity");
  }
}
