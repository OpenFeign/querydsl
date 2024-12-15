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

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class BookVersionPK implements Serializable {

  private static final long serialVersionUID = 8483495681236266676L;

  private Long bookID;

  private Long library;

  public Long getBookID() {
    return bookID;
  }

  public void setBookID(Long bookID) {
    this.bookID = bookID;
  }

  public Long getLibrary() {
    return library;
  }

  public void setLibrary(Long library) {
    this.library = library;
  }
}
