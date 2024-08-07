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

public class SQLSyntax {

  public String createTable = "create table ";
  public String asc = " asc";
  public String autoIncrement = " auto_increment";
  public String columnAlias = " ";
  public String count = "count ";
  public String countStar = "count(*)";
  public String crossJoin = ", ";
  public String delete = "delete ";
  public String desc = " desc";
  public String distinctCountEnd = ")";
  public String distinctCountStart = "count(distinct ";
  public String dummyTable = "dual";
  public String from = " from ";
  public String fullJoin = " full join ";
  public String groupBy = " group by ";
  public String having = " having ";
  public String innerJoin = " inner join ";
  public String insertInto = "insert into ";
  public String join = " join ";
  public String key = "key";
  public String leftJoin = " left join ";
  public String rightJoin = " right join ";
  public String limitTemplate = " limit {0}";
  public String mergeInto = "merge into ";
  public String notNull = " not null";
  public String offsetTemplate = " offset {0}";
  public String on = " on ";
  public String orderBy = " order by ";
  public String select = "select ";
  public String selectDistinct = "select distinct ";
  public String set = "set ";
  public String tableAlias = " ";
  public String update = "update ";
  public String values = " values ";
  public String defaultValues = " values ()";
  public String where = " where ";
  public String with = "with ";
  public String withRecursive = "with recursive ";
  public String createIndex = "create index ";
  public String createUniqueIndex = "create unique index ";
  public String nullsFirst = " nulls first";
  public String nullsLast = " nulls last";
}
