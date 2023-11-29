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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import org.junit.Test;

public class PhraseElementTest {

  @Test
  public void test() {
    StringPath title = Expressions.stringPath("title");
    LuceneSerializer serializer = new LuceneSerializer(false, false);
    QueryMetadata metadata = new DefaultQueryMetadata();
    assertEquals(
        "title:Hello World", serializer.toQuery(title.eq("Hello World"), metadata).toString());
    assertEquals(
        "title:\"Hello World\"",
        serializer.toQuery(title.eq(new PhraseElement("Hello World")), metadata).toString());
  }

  @Test
  public void equals() {
    PhraseElement el1 = new PhraseElement("x"),
        el2 = new PhraseElement("x"),
        el3 = new PhraseElement("y");
    assertEquals(el1, el2);
    assertFalse(el1.equals(el3));
  }

  @Test
  public void hashCode_() {
    PhraseElement el1 = new PhraseElement("x"), el2 = new PhraseElement("x");
    assertEquals(el1.hashCode(), el2.hashCode());
  }
}
