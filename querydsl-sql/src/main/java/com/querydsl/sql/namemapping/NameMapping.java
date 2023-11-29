/*
 * Copyright 2018, The Querydsl Team (http://www.querydsl.com/team)
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
package com.querydsl.sql.namemapping;

import com.querydsl.sql.SchemaAndTable;
import java.util.Optional;

/**
 * By implementing this interface, it is possible to programmatically override schema, table and
 * column names.
 */
public interface NameMapping {

  Optional<String> getColumnOverride(SchemaAndTable key, String column);

  Optional<SchemaAndTable> getOverride(SchemaAndTable key);
}
