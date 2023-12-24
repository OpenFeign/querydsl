/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.github.openfeign.querydsl.gradle;

import static org.junit.jupiter.api.Assertions.*;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

/** A simple unit test for the 'io.github.openfeign.querydsl.gradle.greeting' plugin. */
class QuerydslGradlePluginPluginTest {
  @Test
  void pluginRegistersATask() {
    // Create a test project and apply the plugin
    Project project = ProjectBuilder.builder().build();
    project.getPlugins().apply("io.github.openfeign.querydsl.gradle.greeting");

    // Verify the result
    assertNotNull(project.getTasks().findByName("greeting"));
  }
}
