/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import fluentq.apt.morphia.MorphiaAnnotationProcessor;
import fluentq.codegen.CodegenModule;
import fluentq.codegen.utils.CodeWriter;
import fluentq.core.Entity;
import fluentq.core.types.Expression;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PackageVerification {

  @Test
  void verify_package() throws Exception {
    var version = System.getProperty("version");
    verify(new File("target/fluentq-mongodb-" + version + "-apt-one-jar.jar"));
  }

  private void verify(File oneJar) throws Exception {
    assertThat(oneJar.exists()).as(oneJar.getPath() + " doesn't exist").isTrue();
    // verify classLoader
    var oneJarClassLoader = new URLClassLoader(new URL[] {oneJar.toURI().toURL()});
    oneJarClassLoader.loadClass(Expression.class.getName()); // fluentq-core
    oneJarClassLoader.loadClass(CodeWriter.class.getName()); // codegen
    oneJarClassLoader
        .loadClass(CodegenModule.class.getName())
        .getDeclaredConstructor()
        .newInstance();
    oneJarClassLoader.loadClass(Entity.class.getName()); // morphia
    Class cl =
        oneJarClassLoader.loadClass(MorphiaAnnotationProcessor.class.getName()); // fluentq-apt
    cl.getDeclaredConstructor().newInstance();
    var resourceKey = "META-INF/services/javax.annotation.processing.Processor";
    assertThat(
            new String(
                Files.readAllBytes(Path.of(oneJarClassLoader.findResource(resourceKey).toURI())),
                StandardCharsets.UTF_8))
        .isEqualTo(MorphiaAnnotationProcessor.class.getName());
  }
}
