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

import com.querydsl.sql.codegen.MetaDataExporter;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * {@code MetadataExportMojo} is a goal for {@link MetaDataExporter} execution. It is executed by
 * default in the {@code generate-sources} phase.
 */
@Mojo(name = "export", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class MetadataExportMojo extends AbstractMetaDataExportMojo {

  @Override
  protected boolean isForTest() {
    return false;
  }
}
