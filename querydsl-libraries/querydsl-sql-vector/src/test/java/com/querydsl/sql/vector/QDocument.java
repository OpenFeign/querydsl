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
package com.querydsl.sql.vector;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.vector.VectorPath;
import java.sql.Types;

public class QDocument extends RelationalPathBase<Document> {

  private static final long serialVersionUID = -1652339447328106042L;

  public static final QDocument document = new QDocument("document");

  public final NumberPath<Long> id = createNumber("id", Long.class);

  public final StringPath title = createString("title");

  public final VectorPath embedding = new VectorPath(forProperty("embedding"));

  public QDocument(String variable) {
    super(Document.class, forVariable(variable), null, "vector_document");
    addMetadata(id, ColumnMetadata.named("id").ofType(Types.BIGINT));
    addMetadata(title, ColumnMetadata.named("title").ofType(Types.VARCHAR));
    addMetadata(embedding, ColumnMetadata.named("embedding").ofType(Types.OTHER));
  }
}
