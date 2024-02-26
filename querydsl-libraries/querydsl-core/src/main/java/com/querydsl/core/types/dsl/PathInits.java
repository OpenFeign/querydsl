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
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.PathMetadata;
import java.io.Serializable;
import java.util.*;

/**
 * {@code PathInits} defines path initializations that can be attached to properties via QueryInit
 * annotations
 *
 * @author tiwe
 */
public class PathInits implements Serializable {

  private static final long serialVersionUID = -2173980858324141095L;

  public static final PathInits DEFAULT = new PathInits();

  public static final PathInits DIRECT = new PathInits("*");

  public static final PathInits DIRECT2 = new PathInits("*.*");

  private final boolean initAllProps;

  private final PathInits defaultValue;

  private final Map<String, PathInits> propertyToInits = new HashMap<String, PathInits>();

  public PathInits(String... initStrs) {
    boolean initAllProps = false;
    PathInits defaultValue = DEFAULT;

    Map<String, Collection<String>> properties = new HashMap<>();
    for (String initStr : initStrs) {
      if (initStr.equals("*")) {
        initAllProps = true;
      } else if (initStr.startsWith("*.")) {
        initAllProps = true;
        defaultValue = new PathInits(initStr.substring(2));
      } else {
        String key = initStr;
        List<String> inits = Collections.emptyList();
        if (initStr.contains(".")) {
          key = initStr.substring(0, initStr.indexOf('.'));
          inits = Collections.singletonList(initStr.substring(key.length() + 1));
        }
        Collection<String> values = properties.computeIfAbsent(key, k -> new ArrayList<String>());
        values.addAll(inits);
      }
    }

    for (Map.Entry<String, Collection<String>> entry : properties.entrySet()) {
      PathInits inits = new PathInits(entry.getValue().toArray(new String[0]));
      propertyToInits.put(entry.getKey(), inits);
    }

    this.initAllProps = initAllProps;
    this.defaultValue = defaultValue;
  }

  public PathInits get(String property) {
    if (propertyToInits.containsKey(property)) {
      return propertyToInits.get(property);
    } else if (initAllProps) {
      return defaultValue;
    } else {
      throw new IllegalArgumentException(property + " is not initialized");
    }
  }

  public boolean isInitialized(String property) {
    return initAllProps || propertyToInits.containsKey(property);
  }

  public static PathInits getFor(PathMetadata metadata, PathInits root) {
    if (metadata.isRoot()) {
      return root;
    } else if (metadata.getParent().getMetadata().isRoot()) {
      return DIRECT;
    } else {
      return DEFAULT;
    }
  }
}
