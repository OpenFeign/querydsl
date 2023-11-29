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
package com.querydsl.sql;

/**
 * An extended listener interface that details much more about the preparation and execution of
 * queries
 */
public interface SQLDetailedListener extends SQLListener {
  /**
   * Called at the start of a query. Most context parameters are empty at this stage
   *
   * @param context a context object that is progressively filled out as the query executes
   */
  void start(SQLListenerContext context);

  /**
   * Called at the start of SQL rendering.
   *
   * @param context a context object that is progressively filled out as the query executes
   */
  void preRender(SQLListenerContext context);

  /**
   * Called at the end of SQL rendering. The sql context value will not be available
   *
   * @param context a context object that is progressively filled out as the query executes
   */
  void rendered(SQLListenerContext context);

  /**
   * Called at the start of {@link java.sql.PreparedStatement} preparation.
   *
   * @param context a context object that is progressively filled out as the query executes
   */
  void prePrepare(SQLListenerContext context);

  /**
   * Called at the end of {@link java.sql.PreparedStatement} preparation.
   *
   * @param context a context object that is progressively filled out as the query executes
   */
  void prepared(SQLListenerContext context);

  /**
   * Called at the start of {@link java.sql.PreparedStatement} execution.
   *
   * @param context a context object that is progressively filled out as the query executes
   */
  void preExecute(SQLListenerContext context);

  /**
   * Called at the end of {@link java.sql.PreparedStatement} execution.
   *
   * @param context a context object that is progressively filled out as the query executes
   */
  void executed(SQLListenerContext context);

  /**
   * Called if an exception happens during query building and execution. The context exception
   * values will now be available indicating the exception that occurred.
   *
   * @param context a context object that is progressively filled out as the query executes
   */
  void exception(SQLListenerContext context);

  /**
   * Called at the end of a query.
   *
   * @param context a context object that is progressively filled out as the query executes
   */
  void end(SQLListenerContext context);
}
