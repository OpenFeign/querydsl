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
package com.querydsl.r2dbc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import jdepend.framework.JDepend;
import org.junit.Ignore;
import org.junit.Test;

public class DependenciesTest {

  @Test
  @Ignore
  public void test() throws IOException {
    var jdepend = new JDepend();
    jdepend.addDirectory("target/classes/com/querydsl/r2dbc");
    jdepend.addDirectory("target/classes/com/querydsl/r2dbc/ddl");
    jdepend.addDirectory("target/classes/com/querydsl/r2dbc/dml");
    jdepend.addDirectory("target/classes/com/querydsl/r2dbc/mssql");
    jdepend.addDirectory("target/classes/com/querydsl/r2dbc/mysql");
    jdepend.addDirectory("target/classes/com/querydsl/r2dbc/postgresql");
    jdepend.addDirectory("target/classes/com/querydsl/r2dbc/support");
    jdepend.addDirectory("target/classes/com/querydsl/r2dbc/types");

    jdepend.analyze();
    assertThat(jdepend.containsCycles()).isFalse();
  }
}
