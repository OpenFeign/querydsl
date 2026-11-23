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
package fluentq.core;

import static org.assertj.core.api.Assertions.assertThat;

import jdepend.framework.JDepend;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class DependenciesTest {

  @Test
  @Disabled
  void test() throws Exception {
    // FIXME
    var jdepend = new JDepend();
    jdepend.addDirectory("target/classes/fluentq/alias");
    jdepend.addDirectory("target/classes/fluentq/codegen");
    jdepend.addDirectory("target/classes/fluentq/dml");
    jdepend.addDirectory("target/classes/fluentq/support");
    jdepend.addDirectory("target/classes/fluentq/types");
    jdepend.addDirectory("target/classes/fluentq/types/dsl");
    jdepend.addDirectory("target/classes/fluentq/types/path");
    jdepend.addDirectory("target/classes/fluentq/types/fluentq");
    jdepend.addDirectory("target/classes/fluentq/types/template");

    jdepend.analyze();
    assertThat(jdepend.containsCycles()).isFalse();
  }
}
