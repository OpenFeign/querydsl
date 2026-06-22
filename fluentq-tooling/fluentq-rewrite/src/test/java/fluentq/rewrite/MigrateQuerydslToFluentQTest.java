/*
 * Copyright 2024, The FluentQ Team (http://www.fluentq.com/team).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fluentq.rewrite;

import static org.openrewrite.java.Assertions.java;
import static org.openrewrite.maven.Assertions.pomXml;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;
import org.openrewrite.test.TypeValidation;

class MigrateQuerydslToFluentQTest implements RewriteTest {

  @Override
  public void defaults(RecipeSpec spec) {
    spec.recipeFromResources("io.github.openfeign.fluentq.rewrite.MigrateQuerydslToFluentQ")
        // Querydsl is not on the test classpath, so referenced types are unattributed; the
        // package rename works off the import/qualified names regardless.
        .typeValidationOptions(TypeValidation.none());
  }

  @Test
  void renamesJavaImportsAndReferences() {
    rewriteRun(
        java(
            """
            import com.querydsl.jpa.impl.JPAQueryFactory;

            class Repo {
              JPAQueryFactory factory;
            }
            """,
            """
            import fluentq.jpa.impl.JPAQueryFactory;

            class Repo {
              JPAQueryFactory factory;
            }
            """));
  }

  @Test
  @Disabled(
      "Enable once fluentQ 8.0 is published to Maven Central. The recipe rewrites to"
          + " io.github.openfeign.fluentq:fluentq-*:8.0, which OpenRewrite tries to resolve;"
          + " until it is released that resolution 404s and adds a marker comment.")
  void renamesOpenFeignForkDependency() {
    rewriteRun(
        pomXml(
            """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>demo</artifactId>
              <version>1.0</version>
              <dependencies>
                <dependency>
                  <groupId>io.github.openfeign.querydsl</groupId>
                  <artifactId>querydsl-jpa</artifactId>
                  <version>7.0</version>
                </dependency>
              </dependencies>
            </project>
            """,
            """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>demo</artifactId>
              <version>1.0</version>
              <dependencies>
                <dependency>
                  <groupId>io.github.openfeign.fluentq</groupId>
                  <artifactId>fluentq-jpa</artifactId>
                  <version>8.0</version>
                </dependency>
              </dependencies>
            </project>
            """));
  }

  @Test
  @Disabled(
      "Enable once fluentQ 8.0 is published to Maven Central. The recipe rewrites to"
          + " io.github.openfeign.fluentq:fluentq-*:8.0, which OpenRewrite tries to resolve;"
          + " until it is released that resolution 404s and adds a marker comment.")
  void renamesOriginalQuerydslDependency() {
    rewriteRun(
        pomXml(
            """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>demo</artifactId>
              <version>1.0</version>
              <dependencies>
                <dependency>
                  <groupId>com.querydsl</groupId>
                  <artifactId>querydsl-core</artifactId>
                  <version>5.0.0</version>
                </dependency>
              </dependencies>
            </project>
            """,
            """
            <project>
              <modelVersion>4.0.0</modelVersion>
              <groupId>com.example</groupId>
              <artifactId>demo</artifactId>
              <version>1.0</version>
              <dependencies>
                <dependency>
                  <groupId>io.github.openfeign.fluentq</groupId>
                  <artifactId>fluentq-core</artifactId>
                  <version>8.0</version>
                </dependency>
              </dependencies>
            </project>
            """));
  }
}
