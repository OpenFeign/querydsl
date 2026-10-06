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

import java.util.StringJoiner;

final class VectorText {

  private VectorText() {}

  static String format(float[] vector) {
    var joiner = new StringJoiner(",", "[", "]");
    for (float component : vector) {
      joiner.add(Float.toString(component));
    }
    return joiner.toString();
  }

  static float[] parse(String text) {
    var elementsText = text.substring(1, text.length() - 1).trim();
    if (elementsText.isEmpty()) {
      return new float[0];
    }
    var elementTexts = elementsText.split(",");
    var vector = new float[elementTexts.length];
    for (var i = 0; i < elementTexts.length; i++) {
      vector[i] = Float.parseFloat(elementTexts[i].trim());
    }
    return vector;
  }
}
