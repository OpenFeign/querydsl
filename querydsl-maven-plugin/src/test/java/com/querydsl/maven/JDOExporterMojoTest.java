package com.querydsl.maven;

import static org.junit.Assert.assertTrue;

import java.io.File;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

public class JDOExporterMojoTest {

  @Test
  public void execute() throws Exception {
    MavenProject mavenProject = new MavenProject();
    mavenProject.getBuild().setOutputDirectory("target/classes");
    mavenProject.getBuild().setTestOutputDirectory("target/test-classes");

    JDOExporterMojo mojo = new JDOExporterMojo();
    mojo.setTargetFolder(new File("target/generated-test-data3"));
    mojo.setPackages(new String[] {"com.querydsl.maven"});
    mojo.setProject(mavenProject);
    mojo.setTestClasspath(true);
    mojo.execute();

    File file = new File("target/generated-test-data3/com/querydsl/maven/QEntity.java");
    assertTrue(file.exists());
  }
}
