package com.querydsl.maven;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.codegen.GeneratedAnnotationResolver;
import java.io.File;
import java.lang.annotation.Annotation;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

public class GenericExporterMojoTest {

  public static final File Q_ENTITY_SOURCE_FILE =
      new File("target/generated-test-data/com/querydsl/maven/QEntity.java");

  private GenericExporterMojo prepareMojo() {
    var mavenProject = new MavenProject();
    mavenProject.getBuild().setOutputDirectory("target/classes");
    mavenProject.getBuild().setTestOutputDirectory("target/test-classes");

    var mojo = new GenericExporterMojo();
    mojo.setTargetFolder(new File("target/generated-test-data"));
    mojo.setPackages(new String[] {"com.querydsl.maven"});
    mojo.setProject(mavenProject);
    mojo.setTestClasspath(true);
    return mojo;
  }

  @Test
  public void execute() throws Exception {
    var mojo = prepareMojo();
    mojo.execute();

    assertThat(Q_ENTITY_SOURCE_FILE).exists();
  }

  @Test
  public void defaultGeneratedAnnotation() throws Exception {
    var mojo = prepareMojo();
    mojo.execute();

    var file = Q_ENTITY_SOURCE_FILE;
    var source = FileUtils.fileRead(file);
    assertThat(source).contains("@" + GeneratedAnnotationResolver.resolveDefault().getSimpleName());
  }

  @Test
  public void providedGeneratedAnnotation() throws Exception {
    Class<? extends Annotation> annotationClass = com.querydsl.core.annotations.Generated.class;
    var mojo = prepareMojo();
    mojo.setGeneratedAnnotationClass(annotationClass.getName());
    mojo.execute();

    var file = Q_ENTITY_SOURCE_FILE;
    var source = FileUtils.fileRead(file);
    assertThat(source).contains("@" + annotationClass.getSimpleName());
  }
}
