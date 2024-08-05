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
package com.querydsl.r2dbc;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class StoredProcedures {

  private StoredProcedures() {}

  public static void main(String[] args) throws ClassNotFoundException, SQLException {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    var url = "jdbc:derby:target/procedure_test;create=true";
    var connection = DriverManager.getConnection(url, "", "");

    try {
      var md = connection.getMetaData();
      var procedures = md.getProcedures(null, null, null);
      try {
        while (procedures.next()) {
          var cat = procedures.getString(1);
          var schema = procedures.getString(2);
          var name = procedures.getString(3);
          var remarks = procedures.getString(7);
          var type = procedures.getString(8);
          var specificName = procedures.getString(9);
          System.out.println(name + "\n" + remarks + "\n" + type + "\n" + specificName);

          var procedureColumns = md.getProcedureColumns(cat, schema, name, null);
          try {
            while (procedureColumns.next()) {
              var columnName = procedureColumns.getString(4);
              var columnType = procedureColumns.getInt(5);
              var dataType = procedureColumns.getInt(6);
              var typeName = procedureColumns.getString(7);
              var nullable = procedureColumns.getShort(12);
              System.out.println(
                  " "
                      + columnName
                      + " "
                      + columnType
                      + " "
                      + dataType
                      + " "
                      + typeName
                      + " "
                      + nullable);
            }
            System.out.println();
          } finally {
            procedureColumns.close();
          }
        }
      } finally {
        procedures.close();
      }
    } finally {
      connection.close();
    }
  }
}
