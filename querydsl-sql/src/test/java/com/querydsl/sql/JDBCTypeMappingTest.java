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

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.testutil.ReportingOnly;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Types;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class JDBCTypeMappingTest {

  private JDBCTypeMapping typeMapping = new JDBCTypeMapping();

  @Test
  public void get() {
    assertThat(typeMapping.get(Types.FLOAT, 0, 0)).isEqualTo(Float.class);
    assertThat(typeMapping.get(Types.REAL, 0, 0)).isEqualTo(Float.class);
  }

  @Test
  public void stringTypes() {
    assertThat(typeMapping.get(Types.CHAR, 0, 0)).isEqualTo(String.class);
    assertThat(typeMapping.get(Types.NCHAR, 0, 0)).isEqualTo(String.class);
    assertThat(typeMapping.get(Types.CLOB, 0, 0)).isEqualTo(String.class);
    assertThat(typeMapping.get(Types.NCLOB, 0, 0)).isEqualTo(String.class);
    assertThat(typeMapping.get(Types.LONGVARCHAR, 0, 0)).isEqualTo(String.class);
    assertThat(typeMapping.get(Types.LONGNVARCHAR, 0, 0)).isEqualTo(String.class);
    assertThat(typeMapping.get(Types.SQLXML, 0, 0)).isEqualTo(String.class);
    assertThat(typeMapping.get(Types.VARCHAR, 0, 0)).isEqualTo(String.class);
    assertThat(typeMapping.get(Types.NVARCHAR, 0, 0)).isEqualTo(String.class);
  }

  @Test
  public void blobTypes() {
    assertThat(typeMapping.get(Types.BLOB, 0, 0)).isEqualTo(Blob.class);
  }

  @Test
  public void bytesTypes() {
    assertThat(typeMapping.get(Types.BINARY, 0, 0)).isEqualTo(byte[].class);
    assertThat(typeMapping.get(Types.VARBINARY, 0, 0)).isEqualTo(byte[].class);
    assertThat(typeMapping.get(Types.LONGVARBINARY, 0, 0)).isEqualTo(byte[].class);
  }

  @Test
  public void numericTypes() {
    //        19,0       -> BigInteger
    //        10-18,0    -> Long
    //        5-9,0      -> Integer
    //        3-4,0      -> Short
    //        1-2,0      -> Byte

    //        ?,?   -> BigDecimal
    assertThat(BigInteger.class).isEqualTo(typeMapping.get(Types.NUMERIC, 20, 0));
    assertThat(BigInteger.class).isEqualTo(typeMapping.get(Types.NUMERIC, 19, 0));
    assertThat(Long.class).isEqualTo(typeMapping.get(Types.NUMERIC, 15, 0));
    assertThat(Integer.class).isEqualTo(typeMapping.get(Types.NUMERIC, 6, 0));
    assertThat(Integer.class).isEqualTo(typeMapping.get(Types.NUMERIC, 5, 0));
    assertThat(Short.class).isEqualTo(typeMapping.get(Types.NUMERIC, 4, 0));
    assertThat(Short.class).isEqualTo(typeMapping.get(Types.NUMERIC, 3, 0));
    assertThat(Byte.class).isEqualTo(typeMapping.get(Types.NUMERIC, 2, 0));
    assertThat(Byte.class).isEqualTo(typeMapping.get(Types.NUMERIC, 1, 0));
    assertThat(BigInteger.class).isEqualTo(typeMapping.get(Types.NUMERIC, 0, 0));

    assertThat(BigDecimal.class).isEqualTo(typeMapping.get(Types.NUMERIC, 17, 2));
    assertThat(BigDecimal.class).isEqualTo(typeMapping.get(Types.NUMERIC, 5, 2));
  }

  @Test
  @Category(ReportingOnly.class)
  public void max() {
    System.err.println("Byte: " + String.valueOf(Byte.MAX_VALUE).length());
    System.err.println("Short: " + String.valueOf(Short.MAX_VALUE).length());
    System.err.println("Integer: " + String.valueOf(Integer.MAX_VALUE).length());
    System.err.println("Long: " + String.valueOf(Long.MAX_VALUE).length());
  }

  @Test
  public void numericOverriden() {
    typeMapping.registerNumeric(19, 0, BigInteger.class);
    assertThat(BigInteger.class).isEqualTo(typeMapping.get(Types.NUMERIC, 19, 0));
  }

  @Test
  public void numericOverriden2() {
    typeMapping.registerNumeric(19, 0, BigInteger.class);
    assertThat(BigInteger.class).isEqualTo(typeMapping.get(Types.INTEGER, 19, 0));
    assertThat(Integer.class).isEqualTo(typeMapping.get(Types.INTEGER, 18, 0));
  }

  @Test
  public void numericOverriden3() {
    typeMapping.registerNumeric(5, 2, BigDecimal.class);
    assertThat(BigDecimal.class).isEqualTo(typeMapping.get(Types.DOUBLE, 5, 2));
    assertThat(Double.class).isEqualTo(typeMapping.get(Types.DOUBLE, 5, 1));
  }
}
