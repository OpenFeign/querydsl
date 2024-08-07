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
  public String from = "\nfrom ";
  public String fullJoin = "\nfull join ";
  public String groupBy = "\ngroup by ";
  public String having = "\nhaving ";
  public String innerJoin = "\ninner join ";
  public String insertInto = "insert into ";
  public String join = "\njoin ";
  public String key = "key";
  public String leftJoin = "\nleft join ";
  public String rightJoin = "\nright join ";
  public String limitTemplate = "\nlimit {0}";
  public String mergeInto = "merge into ";
  public String notNull = " not null";
  public String offsetTemplate = "\noffset {0}";
  public String on = "\non ";
  public String orderBy = "\norder by ";
  public String select = "select ";
  public String selectDistinct = "select distinct ";
  public String set = "set ";
  public String tableAlias = " ";
  public String update = "update ";
  public String values = "\nvalues ";
  public String defaultValues = "\nvalues ()";
  public String where = "\nwhere ";
  public String with = "with ";
  public String withRecursive = "with recursive ";
  public String createIndex = "create index ";
  public String createUniqueIndex = "create unique index ";
  public String nullsFirst = " nulls first";
  public String nullsLast = " nulls last";
}
