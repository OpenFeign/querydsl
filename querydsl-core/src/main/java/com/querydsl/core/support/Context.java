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
package com.querydsl.core.support;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code Context} is used in {@link CollectionAnyVisitor} as the visitor context
 *
 * @author tiwe
 */
public class Context {

  public boolean replace;

  public final List<Path<?>> paths = new ArrayList<Path<?>>();

  public final List<EntityPath<?>> replacements = new ArrayList<EntityPath<?>>();

  public void add(Path<?> anyPath, EntityPath<?> replacement) {
    replace = true;
    paths.add(anyPath);
    replacements.add(replacement);
  }

  public void add(Context c) {
    replace |= c.replace;
    paths.addAll(c.paths);
    replacements.addAll(c.replacements);
  }

  public void clear() {
    paths.clear();
    replacements.clear();
  }
}
