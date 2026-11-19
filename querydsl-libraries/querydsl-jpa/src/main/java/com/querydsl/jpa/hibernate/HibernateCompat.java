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
package com.querydsl.jpa.hibernate;

import com.querydsl.core.types.Path;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashSet;
import java.util.Map;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.SharedSessionContract;
import org.hibernate.query.Query;
import org.jetbrains.annotations.Nullable;

/**
 * Internal helper for the Hibernate calls that have no common binary signature on Hibernate ORM 7
 * and 8. Each method is resolved once by name and parameter types.
 *
 * <p>Not part of the public API.
 */
final class HibernateCompat {

  private static final MethodHandle CREATE_QUERY;

  /** {@code Query.setLockMode(String, LockMode)}; removed in Hibernate ORM 8. */
  @Nullable private static final MethodHandle SET_ALIAS_LOCK_MODE;

  /** {@code Query.setHibernateLockMode(LockMode)}; its return type differs between versions. */
  private static final MethodHandle SET_HIBERNATE_LOCK_MODE;

  /** {@code Query.setHibernateFlushMode(FlushMode)}; removed in Hibernate ORM 8. */
  @Nullable private static final MethodHandle SET_HIBERNATE_FLUSH_MODE;

  /** {@code Query.setQueryFlushMode(jakarta.persistence.QueryFlushMode)}; Hibernate ORM 8. */
  @Nullable private static final MethodHandle SET_QUERY_FLUSH_MODE;

  @Nullable private static final Class<?> QUERY_FLUSH_MODE;

  static {
    try {
      var lookup = MethodHandles.publicLookup();
      CREATE_QUERY =
          lookup
              .unreflect(SharedSessionContract.class.getMethod("createQuery", String.class))
              .asType(
                  MethodType.methodType(Query.class, SharedSessionContract.class, String.class));
      SET_ALIAS_LOCK_MODE =
          findOnQuery(lookup, "setLockMode", Object.class, String.class, LockMode.class);
      SET_HIBERNATE_LOCK_MODE =
          lookup
              .unreflect(Query.class.getMethod("setHibernateLockMode", LockMode.class))
              .asType(MethodType.methodType(Object.class, Query.class, LockMode.class));
      SET_HIBERNATE_FLUSH_MODE =
          findOnQuery(lookup, "setHibernateFlushMode", Object.class, FlushMode.class);
      Class<?> queryFlushMode = null;
      MethodHandle setQueryFlushMode = null;
      if (SET_HIBERNATE_FLUSH_MODE == null) {
        queryFlushMode =
            Class.forName(
                "jakarta.persistence.QueryFlushMode", false, Query.class.getClassLoader());
        setQueryFlushMode =
            lookup
                .unreflect(Query.class.getMethod("setQueryFlushMode", queryFlushMode))
                .asType(MethodType.methodType(Object.class, Query.class, Object.class));
      }
      QUERY_FLUSH_MODE = queryFlushMode;
      SET_QUERY_FLUSH_MODE = setQueryFlushMode;
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Unsupported Hibernate ORM version", e);
    }
  }

  private HibernateCompat() {}

  /** Same as {@code session.createQuery(queryString)}. */
  static Query<?> createQuery(SharedSessionContract session, String queryString) {
    try {
      return (Query<?>) CREATE_QUERY.invokeExact(session, queryString);
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable t) {
      throw new IllegalStateException(t);
    }
  }

  /**
   * Apply lock modes registered per alias.
   *
   * <p>Where Hibernate supports alias specific lock modes (ORM 7), they are passed through. Where
   * it does not (ORM 8), a single distinct lock mode is applied to the whole query and differing
   * lock modes are rejected.
   *
   * @throws UnsupportedOperationException if the aliases carry different lock modes and Hibernate
   *     has no alias specific lock modes
   */
  static void setLockModes(Query<?> query, Map<Path<?>, LockMode> lockModes) {
    if (lockModes.isEmpty()) {
      return;
    }
    try {
      if (SET_ALIAS_LOCK_MODE != null) {
        for (Map.Entry<Path<?>, LockMode> entry : lockModes.entrySet()) {
          Object ignored =
              SET_ALIAS_LOCK_MODE.invokeExact(query, entry.getKey().toString(), entry.getValue());
        }
      } else {
        var distinct = new HashSet<>(lockModes.values());
        if (distinct.size() > 1) {
          throw new UnsupportedOperationException(
              "Per-alias lock modes are not supported by this Hibernate ORM version; "
                  + "all aliases must use the same lock mode, but got "
                  + lockModes);
        }
        Object ignored = SET_HIBERNATE_LOCK_MODE.invokeExact(query, distinct.iterator().next());
      }
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable t) {
      throw new IllegalStateException(t);
    }
  }

  /**
   * Apply lock modes registered on an insert, update or delete clause.
   *
   * <p>Hibernate ORM 7 accepts lock modes on a mutation query without acting on them, so they are
   * passed through unchanged. Hibernate ORM 8 rejects them, so they are left out there; the
   * statement behaves the same on both versions.
   */
  static void setMutationLockModes(Query<?> query, Map<Path<?>, LockMode> lockModes) {
    if (SET_ALIAS_LOCK_MODE != null) {
      setLockModes(query, lockModes);
    }
  }

  /** Same as {@code query.setHibernateFlushMode(flushMode)} on Hibernate ORM 7. */
  @SuppressWarnings({"unchecked", "rawtypes"})
  static void setFlushMode(Query<?> query, FlushMode flushMode) {
    try {
      if (SET_HIBERNATE_FLUSH_MODE != null) {
        Object ignored = SET_HIBERNATE_FLUSH_MODE.invokeExact(query, flushMode);
      } else {
        var name =
            switch (flushMode) {
              case ALWAYS -> "FLUSH";
              case MANUAL, COMMIT -> "NO_FLUSH";
              default -> "DEFAULT";
            };
        Object ignored =
            SET_QUERY_FLUSH_MODE.invokeExact(
                query, (Object) Enum.valueOf((Class<Enum>) QUERY_FLUSH_MODE, name));
      }
    } catch (RuntimeException | Error e) {
      throw e;
    } catch (Throwable t) {
      throw new IllegalStateException(t);
    }
  }

  @Nullable
  private static MethodHandle findOnQuery(
      MethodHandles.Lookup lookup, String name, Class<?> returnType, Class<?>... parameterTypes)
      throws IllegalAccessException {
    try {
      return lookup
          .unreflect(Query.class.getMethod(name, parameterTypes))
          .asType(MethodType.methodType(returnType, Query.class, parameterTypes));
    } catch (NoSuchMethodException e) {
      return null;
    }
  }
}
