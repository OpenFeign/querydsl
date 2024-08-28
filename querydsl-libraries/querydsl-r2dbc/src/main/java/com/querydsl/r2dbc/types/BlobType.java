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
package com.querydsl.r2dbc.types;

import io.r2dbc.spi.Blob;
import java.sql.Types;

/**
 * {@code BlobType} maps Blob to Blob on the JDBC level
 *
 * @author mc_fish
 */
public class BlobType extends AbstractType<Blob, Blob> {

  public BlobType() {
    super(Types.BLOB);
  }

  public BlobType(int type) {
    super(type);
  }

  @Override
  public Class<Blob> getReturnedClass() {
    return Blob.class;
  }
}
