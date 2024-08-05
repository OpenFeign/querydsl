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
package com.querydsl.hibernate.search;

import com.querydsl.core.types.Path;
import com.querydsl.lucene5.LuceneSerializer;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

/**
 * {@code SearchSerializer} extends the {@link LuceneSerializer} to use {@link FullTextField}
 * annotation data from paths
 *
 * @author tiwe
 */
public class SearchSerializer extends LuceneSerializer {

  public static final SearchSerializer DEFAULT = new SearchSerializer(false, true);

  /**
   * Create a new SearchSerializer instance
   *
   * @param lowerCase lowercase names
   * @param splitTerms split terms
   */
  public SearchSerializer(boolean lowerCase, boolean splitTerms) {
    super(lowerCase, splitTerms);
  }

  @Override
  public String toField(Path<?> path) {
    if (path.getAnnotatedElement() != null) {
      FullTextField fullTextField = path.getAnnotatedElement().getAnnotation(FullTextField.class);
      if (fullTextField != null && fullTextField.name().length() > 0) {
        return fullTextField.name();
      }
      KeywordField keywordField = path.getAnnotatedElement().getAnnotation(KeywordField.class);
      if (keywordField != null && keywordField.name().length() > 0) {
        return keywordField.name();
      }
      GenericField genericField = path.getAnnotatedElement().getAnnotation(GenericField.class);
      if (genericField != null && genericField.name().length() > 0) {
        return genericField.name();
      }
    }
    return super.toField(path);
  }
}
