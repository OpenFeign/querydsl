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
package com.querydsl.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.domain.Cat;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.io.TempDir;

public class GenericExporterTest {

  @TempDir public File folder;

  private GenericExporter exporter;

  @Before
  public void setUp() {
    exporter = new GenericExporter();
  }

  @Test
  public void export() {
    exporter.setTargetFolder(folder);
    exporter.export(getClass().getPackage());
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEmbeddable.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEmbedded.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEntity.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEntityInterface.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleSupertype.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/sub/QExampleEntity2.java")).exists();
  }

  @Test
  public void export_with_keywords() throws IOException {
    exporter.setKeywords(Keywords.JPA);
    exporter.setTargetFolder(folder);
    exporter.export(getClass().getPackage());
    String str =
        new String(
            Files.readAllBytes(new File(folder, "com/querydsl/codegen/QGroup.java").toPath()),
            StandardCharsets.UTF_8);
    assertThat(str).contains("QGroup group = new QGroup(\"group1\");");
  }

  @Test
  public void export_with_stopClass() {
    exporter.setTargetFolder(folder);
    exporter.addStopClass(Examples.Supertype.class);
    exporter.export(getClass().getPackage());
    assertThat(new File(folder, "com/querydsl/codegen/QExamples_Supertype.java").exists())
        .isFalse();
  }

  @Test
  public void override_serializer() {
    exporter.setTargetFolder(folder);
    exporter.setSerializerClass(DefaultEntitySerializer.class);
    exporter.export(getClass().getPackage());
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEmbeddable.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEmbedded.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEntity.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEntityInterface.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleSupertype.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/sub/QExampleEntity2.java")).exists();
  }

  @Test
  public void export_package_as_string() {
    exporter.setTargetFolder(folder);
    exporter.export(getClass().getPackage().getName());
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEmbeddable.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEmbedded.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEntity.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEntityInterface.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleSupertype.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/sub/QExampleEntity2.java")).exists();
  }

  @Test
  public void export_with_package_suffix() {
    exporter.setTargetFolder(folder);
    exporter.setPackageSuffix("types");
    exporter.export(getClass().getPackage());
    assertThat(new File(folder, "com/querydsl/codegentypes/QExampleEmbeddable.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegentypes/QExampleEmbedded.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegentypes/QExampleEntity.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegentypes/QExampleEntityInterface.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegentypes/QExampleSupertype.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/subtypes/QExampleEntity2.java")).exists();
  }

  @Test
  public void export_handle_no_methods_nor_fields() {
    exporter.setTargetFolder(folder);
    exporter.setHandleFields(false);
    exporter.setHandleMethods(false);
    exporter.export(getClass().getPackage());
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEmbeddable.java")).exists();
  }

  @Test
  public void export_domain_package() {
    exporter.setTargetFolder(folder);
    exporter.export(Cat.class.getPackage());
  }

  @Test
  public void export_serializerConfig() {
    exporter.setTargetFolder(folder);
    exporter.setSerializerConfig(new SimpleSerializerConfig(true, true, true, true, ""));
    exporter.export(getClass().getPackage());
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEmbeddable.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEmbedded.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEntity.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEntityInterface.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleSupertype.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/sub/QExampleEntity2.java")).exists();
  }

  @Test
  public void export_useFieldTypes() {
    exporter.setTargetFolder(folder);
    exporter.setUseFieldTypes(true);
    exporter.export(getClass().getPackage());
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEmbeddable.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEmbedded.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEntity.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleEntityInterface.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/QExampleSupertype.java")).exists();
    assertThat(new File(folder, "com/querydsl/codegen/sub/QExampleEntity2.java")).exists();
  }

  @Test
  public void export_propertyHandling() throws IOException {
    for (PropertyHandling ph : PropertyHandling.values()) {
      File f = newFolder(folder, "junit");
      GenericExporter e = new GenericExporter();
      e.setTargetFolder(f);
      e.setPropertyHandling(ph);
      e.export(getClass().getPackage());
      assertThat(new File(f, "com/querydsl/codegen/QExampleEmbeddable.java")).exists();
      assertThat(new File(f, "com/querydsl/codegen/QExampleEntity.java")).exists();
      assertThat(new File(f, "com/querydsl/codegen/QExampleEntityInterface.java")).exists();
      assertThat(new File(f, "com/querydsl/codegen/QExampleSupertype.java")).exists();
      assertThat(new File(f, "com/querydsl/codegen/sub/QExampleEntity2.java")).exists();
    }
  }

  private static File newFolder(File root, String... subDirs) throws IOException {
    String subFolder = String.join("/", subDirs);
    File result = new File(root, subFolder);
    if (!result.mkdirs()) {
      throw new IOException("Couldn't create folders " + root);
    }
    return result;
  }
}
