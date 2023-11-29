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
package com.querydsl.core.util;

import java.lang.reflect.Array;

/**
 * ArrayUtils provides array related utility functionality
 *
 * @author tiwe
 */
public final class ArrayUtils {

  @SuppressWarnings("unchecked")
  public static <T> T[] combine(Class<T> type, T first, T second, T... rest) {
    T[] array = (T[]) Array.newInstance(type, rest.length + 2);
    array[0] = first;
    array[1] = second;
    System.arraycopy(rest, 0, array, 2, rest.length);
    return array;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] combine(Class<T> type, T first, T... rest) {
    T[] array = (T[]) Array.newInstance(type, rest.length + 1);
    array[0] = first;
    System.arraycopy(rest, 0, array, 1, rest.length);
    return array;
  }

  public static Object[] combine(int size, Object[]... arrays) {
    int offset = 0;
    Object[] target = new Object[size];
    for (Object[] arr : arrays) {
      System.arraycopy(arr, 0, target, offset, arr.length);
      offset += arr.length;
    }
    return target;
  }

  // copied and modified from commons-lang-2.3
  // originally licensed under ASL 2.0
  public static Object[] subarray(Object[] array, int startIndexInclusive, int endIndexExclusive) {
    int newSize = endIndexExclusive - startIndexInclusive;
    Class<?> type = array.getClass().getComponentType();
    if (newSize <= 0) {
      return (Object[]) Array.newInstance(type, 0);
    }
    Object[] subarray = (Object[]) Array.newInstance(type, newSize);
    System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
    return subarray;
  }

  public static boolean isEmpty(Object[] array) {
    return array == null || array.length == 0;
  }

  private ArrayUtils() {}
}
