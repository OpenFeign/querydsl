package com.querydsl.maven;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

public class JPAExporterMojoTest {

  @Test
  public void execute() throws Exception {
    var mavenProject = new MavenProject();
    mavenProject.getBuild().setOutputDirectory("target/classes");
    mavenProject.getBuild().setTestOutputDirectory("target/test-classes");

    var mojo = new JPAExporterMojo();
    mojo.setTargetFolder(new File("target/generated-test-data2"));
    mojo.setPackages(new String[] {"com.querydsl.maven"});
    mojo.setProject(mavenProject);
    mojo.setTestClasspath(true);
    mojo.execute();

    var file = new File("target/generated-test-data2/com/querydsl/maven/QEntity.java");
    assertThat(file).exists();
  }
}
