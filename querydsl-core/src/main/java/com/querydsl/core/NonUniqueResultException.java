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
package com.querydsl.core;

/**
 * {@code NonUniqueResultException} is thrown for query results where one result row is expected,
 * but multiple are retrieved.
 *
 * @author tiwe
 */
public class NonUniqueResultException extends QueryException {

  private static final long serialVersionUID = -1757423191400510323L;

  public NonUniqueResultException() {
    super("Only one result is allowed for fetchOne calls");
  }

  public NonUniqueResultException(String message) {
    super(message);
  }

  public NonUniqueResultException(Exception e) {
    super(e);
  }
}
