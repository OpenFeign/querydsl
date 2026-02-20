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

public class UnknownAsEmbeddableTest extends AbstractProcessorTest {

  private static final String packagePath = "src/test/java/com/querydsl/apt/domain";

  @Test
  public void process() throws IOException {
    var classes = getFiles(packagePath);
    process(QuerydslAnnotationProcessor.class, classes, "unknownAsEmbeddable");

    assertThat(new File("target/unknownAsEmbeddable/com/querydsl/apt/domain/custom/QEntity.java"))
        .exists();
    assertThat(
            new File(
                "target/unknownAsEmbeddable/com/querydsl/apt/domain/custom/QEmbeddedType.java"))
        .exists();
    assertThat(
            new File(
                "target/unknownAsEmbeddable/com/querydsl/apt/domain/custom/QEmbeddedType2.java"))
        .exists();
    assertThat(
            new File(
                "target/unknownAsEmbeddable/com/querydsl/apt/domain/custom/QEmbeddedType3.java"))
        .exists();
  }

  @Override
  protected Collection<String> getAPTOptions() {
    return Collections.singletonList("-Aquerydsl.unknownAsEmbeddable=true");
  }
}
