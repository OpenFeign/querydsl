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
package com.querydsl.lucene3;

/**
 * Thrown for case ignore usage
 *
 * @author tiwe
 */
public class IgnoreCaseUnsupportedException extends UnsupportedOperationException {

  private static final long serialVersionUID = 412913389929530788L;

  public IgnoreCaseUnsupportedException() {
    super("Ignore case queries are not supported with Lucene");
  }
}
