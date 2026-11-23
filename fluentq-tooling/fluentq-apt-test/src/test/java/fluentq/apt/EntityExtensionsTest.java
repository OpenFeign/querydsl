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
package fluentq.apt;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class EntityExtensionsTest extends AbstractProcessorTest {

  private static final String packagePath = "src/test/java/fluentq/apt/";

  @Test
  public void handles_entity_extensions_correctly() throws IOException, InterruptedException {
    var source = new File(packagePath, "EntityWithExtensions.java");
    var source2 = new File(packagePath, "EntityExtensions.java");
    List<String> sources = Arrays.asList(source.getPath(), source2.getPath());
    var qType = new File("target/overwrite2/fluentq/apt/QEntityWithExtensions.java");

    // QEntityWithExtensions is generated
    process(FluentQAnnotationProcessor.class, sources, "overwrite2");
    assertThat(qType).exists();
    var modified = qType.lastModified();
    Thread.sleep(1000);
    assertThat(Files.readString(qType.toPath())).contains("extension()");

    // EntityWithExtensions has not changed, QEntityWithExtensions is not overwritten
    compile(FluentQAnnotationProcessor.class, sources, "overwrite2");
    assertThat(qType.lastModified()).isEqualTo(modified);

    // EntityWithExtensions is updated, QEntityWithExtensions is overwritten
    Files.createFile(source.toPath());
    compile(FluentQAnnotationProcessor.class, sources, "overwrite2");
    assertThat(modified < qType.lastModified())
        .as("" + modified + " >= " + qType.lastModified())
        .isTrue();
    assertThat(Files.readString(qType.toPath())).contains("extension()");

    // QEntityWithExtensions is deleted and regenerated
    assertThat(qType.delete()).isTrue();
    compile(FluentQAnnotationProcessor.class, sources, "overwrite2");
    assertThat(qType).exists();
    assertThat(Files.readString(qType.toPath())).contains("extension()");
  }

  @Override
  protected Collection<String> getAPTOptions() {
    return Collections.singletonList("-AdefaultOverwrite=true");
  }
}
