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
import java.nio.file.Files;
import java.util.Collections;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class IncrementalCompilationTest extends AbstractProcessorTest {

  private static final String packagePath = "src/test/java/com/querydsl/apt/domain/";

  @Test
  public void does_not_overwrite_unchanged_files() throws IOException, InterruptedException {
    var source = new File(packagePath, "ExampleEntity.java");
    var path = source.getPath();
    var qType = new File("target/overwrite/com/querydsl/apt/domain/QExampleEntity.java");

    // QTestEntity is generated
    process(QuerydslAnnotationProcessor.class, Collections.singletonList(path), "overwrite");
    assertThat(qType).exists();
    var modified = qType.lastModified();
    Thread.sleep(1000);

    // TestEntity has not changed, QTestEntity is not overwritten
    compile(QuerydslAnnotationProcessor.class, Collections.singletonList(path), "overwrite");
    assertThat(qType.lastModified()).isEqualTo(modified);

    // TestEntity is updated, QTestEntity is overwritten
    Files.createFile(source.toPath());
    compile(QuerydslAnnotationProcessor.class, Collections.singletonList(path), "overwrite");
    assertThat(modified < qType.lastModified())
        .as("" + modified + " >= " + qType.lastModified())
        .isTrue();

    // QTestEntity is deleted and regenerated
    assertThat(qType.delete()).isTrue();
    compile(QuerydslAnnotationProcessor.class, Collections.singletonList(path), "overwrite");
    assertThat(qType).exists();
  }
}
