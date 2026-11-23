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
package fluentq.sql.vector;

import fluentq.codegen.AbstractModule;
import fluentq.codegen.Extension;
import fluentq.sql.Configuration;

/**
 * {@code SqlVectorCodegenExtension} maps vector columns to {@code float[]} in SQL code generation
 */
public final class SqlVectorCodegenExtension implements Extension {

  @Override
  public void addSupport(AbstractModule module) {
    module.get(Configuration.class).registerType("vector", float[].class);
  }
}
