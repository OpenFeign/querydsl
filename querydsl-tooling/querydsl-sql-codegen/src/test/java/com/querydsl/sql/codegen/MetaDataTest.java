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
package com.querydsl.sql.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import org.junit.Before;
import org.junit.Test;

public class MetaDataTest {

  private EntityType classModel;

  @Before
  public void setUp() {
    NamingStrategy namingStrategy = new DefaultNamingStrategy();
    var packageName = "com.myproject.domain";
    var tableName = "vwServiceName";
    var className = namingStrategy.getClassName(tableName);

    Type classTypeModel =
        new SimpleType(
            TypeCategory.ENTITY,
            packageName + "." + className,
            packageName,
            className,
            false,
            false);
    classModel = new EntityType(classTypeModel);
    //        classModel.addAnnotation(new TableImpl(namingStrategy.normalizeTableName(tableName)));
    classModel.getData().put("table", namingStrategy.normalizeTableName(tableName));
  }

  @Test
  public void getSimpleName() {
    assertThat(classModel.getSimpleName()).isEqualTo("VwServiceName");
  }

  @Test
  public void getFullName() {
    assertThat(classModel.getFullName()).isEqualTo("com.myproject.domain.VwServiceName");
  }
}
