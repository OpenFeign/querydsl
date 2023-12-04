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
package com.querydsl.jpa.domain4;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Access(AccessType.PROPERTY)
public class BookMark implements Serializable {

  private static final long serialVersionUID = 8027009758015834551L;

  private Long page;

  private String comment;

  @Basic
  public Long getPage() {
    return page;
  }

  public void setPage(Long page) {
    this.page = page;
  }

  @Basic
  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
