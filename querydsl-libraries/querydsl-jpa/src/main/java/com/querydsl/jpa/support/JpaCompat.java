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
package com.querydsl.jpa.support;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Internal helper that creates queries on an {@link EntityManager} in a way that links on both
 * Jakarta Persistence 3.2 and 4.
 *
 * <p>Jakarta Persistence 4 changed the return types of {@code createQuery(String)} and {@code
 * createNativeQuery(...)}. The JVM links a call by its full descriptor, return type included, so a
 * direct call compiled against one version fails with {@code NoSuchMethodError} on the other. The
 * methods here are resolved once by name and parameter types, and their result is always handed
 * back as a plain {@link Query}.
 *
 * <p>Not part of the public API.
 */
public final class JpaCompat {

  private static final MethodHandle CREATE_QUERY = find("createQuery", String.class);

  private static final MethodHandle CREATE_NATIVE_QUERY = find("createNativeQuery", String.class);

  private static final MethodHandle CREATE_NATIVE_QUERY_TYPED =
      find("createNativeQuery", String.class, Class.class);

  private JpaCompat() {}

  /** Same as {@code em.createQuery(queryString)}. */
  public static Query createQuery(EntityManager em, String queryString) {
    try {
      return (Query) CREATE_QUERY.invokeExact(em, queryString);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable t) {
      throw new IllegalStateException(t);
    }
  }

  /** Same as {@code em.createNativeQuery(sqlString)}. */
  public static Query createNativeQuery(EntityManager em, String sqlString) {
    try {
      return (Query) CREATE_NATIVE_QUERY.invokeExact(em, sqlString);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable t) {
      throw new IllegalStateException(t);
    }
  }

  /** Same as {@code em.createNativeQuery(sqlString, resultClass)}. */
  public static Query createNativeQuery(EntityManager em, String sqlString, Class<?> resultClass) {
    try {
      return (Query) CREATE_NATIVE_QUERY_TYPED.invokeExact(em, sqlString, resultClass);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable t) {
      throw new IllegalStateException(t);
    }
  }

  private static MethodHandle find(String name, Class<?>... parameterTypes) {
    try {
      var method = EntityManager.class.getMethod(name, parameterTypes);
      return MethodHandles.publicLookup()
          .unreflect(method)
          .asType(MethodType.methodType(Query.class, EntityManager.class, parameterTypes));
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(
          "Unsupported Jakarta Persistence API: EntityManager." + name + " not found", e);
    }
  }
}
