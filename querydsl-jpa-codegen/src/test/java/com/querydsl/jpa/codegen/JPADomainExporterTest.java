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
package com.querydsl.jpa.codegen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.TemporaryFolder;

public class JPADomainExporterTest {

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Rule public ErrorCollector errors = new ErrorCollector();

  @Test
  public void test() throws IOException {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("h2", new Properties());
    Path outputFolder = folder.getRoot().toPath();
    JPADomainExporter exporter = new JPADomainExporter(outputFolder.toFile(), emf.getMetamodel());
    exporter.execute();

    File origRoot = new File("../querydsl-jpa/target/generated-test-sources/test-annotations");
    Set<File> files = exporter.getGeneratedFiles();
    assertThat(files).isNotEmpty();
    for (File file : files) {
      Path relativeFile = outputFolder.relativize(file.toPath());
      Path origFile = origRoot.toPath().resolve(relativeFile);
      String reference = new String(Files.readAllBytes(origFile), StandardCharsets.UTF_8);
      String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
      errors.checkThat("Mismatch for " + file.getPath(), content, is(equalTo(reference)));
    }
  }
}
