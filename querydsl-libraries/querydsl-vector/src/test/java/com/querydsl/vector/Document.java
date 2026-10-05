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
package com.querydsl.vector;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "vector_document")
public class Document {

  @Id private Long id;

  private String title;

  @JdbcTypeCode(SqlTypes.VECTOR)
  @Array(length = 3)
  private float[] embedding;

  protected Document() {}

  public Document(long id, String title, float... embedding) {
    this.id = id;
    this.title = title;
    this.embedding = embedding;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public float[] getEmbedding() {
    return embedding;
  }
}
