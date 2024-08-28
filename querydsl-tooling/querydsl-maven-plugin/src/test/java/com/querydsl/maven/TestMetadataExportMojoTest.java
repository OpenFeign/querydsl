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
package com.querydsl.maven;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.codegen.GeneratedAnnotationResolver;
import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Collections;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

public class TestMetadataExportMojoTest {

  private final String url = "jdbc:h2:mem:testdb" + System.currentTimeMillis() + ";MODE=legacy";

  private TestMetadataExportMojo setupMojoWith(MavenProject project) {
    var mojo = new TestMetadataExportMojo();
    mojo.setProject(project);
    mojo.setJdbcDriver("org.h2.Driver");
    mojo.setJdbcUrl(url);
    mojo.setJdbcUser("sa");
    mojo.setNamePrefix("Q"); // default value
    mojo.setNameSuffix("");
    mojo.setBeanPrefix("");
    mojo.setBeanSuffix("Bean");
    mojo.setPackageName("com.example");
    mojo.setTargetFolder("target/export4");
    mojo.setImports(new String[] {"com.pck1", "com.pck2", "com.Q1", "com.Q2"});
    mojo.setExportTables(true); // default value
    mojo.setExportViews(true); // default value
    return mojo;
  }

  @Test
  public void execute() throws Exception {
    var project = new MavenProject();
    var mojo = setupMojoWith(project);
    var target = new File("target/export4").getCanonicalFile();
    mojo.setTargetFolder(target.getAbsolutePath());
    mojo.execute();

    // 'target/export4' seems to conflict with MetadataExportMojoTest.Execute_With_TypeMappings
    assertThat(project.getTestCompileSourceRoots())
        .isEqualTo(Collections.singletonList(target.getAbsolutePath()));
    assertThat(target).exists();
  }

  @Test
  public void defaultGeneratedAnnotation() throws Exception {
    var project = new MavenProject();
    var mojo = setupMojoWith(project);
    mojo.execute();

    var sourceFile = new File("target/export4/com/example/QInformationSchemaCatalogName.java");
    var sourceFileContent = FileUtils.fileRead(sourceFile);
    assertThat(sourceFileContent)
        .contains("@" + GeneratedAnnotationResolver.resolveDefault().getSimpleName());
  }

  @Test
  public void providedGeneratedAnnotation() throws Exception {
    Class<? extends Annotation> annotationClass = com.querydsl.core.annotations.Generated.class;
    var project = new MavenProject();
    var mojo = setupMojoWith(project);
    mojo.setGeneratedAnnotationClass(annotationClass.getName());
    mojo.execute();

    var sourceFile = new File("target/export4/com/example/QInformationSchemaCatalogName.java");
    var sourceFileContent = FileUtils.fileRead(sourceFile);
    assertThat(sourceFileContent).contains("@" + annotationClass.getSimpleName());
  }
}
