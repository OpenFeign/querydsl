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
package com.querydsl.jpa.support;

import com.querydsl.core.types.Ops;
import com.querydsl.sql.DerbyTemplates;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.community.dialect.DerbyDialect;

/** {@code QDerbyDialect} extends {@code DerbyDialect} with additional functions */
public class QDerbyDialect extends DerbyDialect {

  @Override
  public void initializeFunctionRegistry(FunctionContributions functionContributions) {
    super.initializeFunctionRegistry(functionContributions);
    DialectSupport.extendRegistry(DerbyTemplates.DEFAULT, functionContributions);
    DialectSupport.extendRegistry(
        "concat",
        DialectSupport.createFunction(DerbyTemplates.DEFAULT, Ops.CONCAT),
        functionContributions);
  }
}
