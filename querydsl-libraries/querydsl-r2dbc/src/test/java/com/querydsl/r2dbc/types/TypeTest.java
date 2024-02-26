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
package com.querydsl.r2dbc.types;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

import com.mysema.commons.lang.Pair;
import com.querydsl.r2dbc.binding.BindMarker;
import com.querydsl.r2dbc.binding.BindMarkersFactory;
import com.querydsl.r2dbc.binding.BindTarget;
import com.querydsl.r2dbc.binding.StatementWrapper;
import io.r2dbc.spi.Blob;
import io.r2dbc.spi.Clob;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import org.easymock.EasyMock;
import org.junit.Test;

public class TypeTest implements InvocationHandler {

  public enum Gender {
    MALE,
    FEMALE
  }

  private Object value;

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getName().equals("wasNull")) {
      return value == null;
    } else if (method.getName().startsWith("get")) {
      if (method.getReturnType().isPrimitive() && value == null) {
        Class<?> rt = method.getReturnType();
        if (rt == Byte.TYPE) {
          return (byte) 0;
        } else if (rt == Short.TYPE) {
          return (short) 0;
        } else if (rt == Integer.TYPE) {
          return 0;
        } else if (rt == Long.TYPE) {
          return 0L;
        } else if (rt == Float.TYPE) {
          return 0.0f;
        } else if (rt == Double.TYPE) {
          return 0.0;
        } else if (rt == Boolean.TYPE) {
          return Boolean.FALSE;
        } else if (rt == Character.TYPE) {
          return (char) 0;
        }
      }
      return value;
    } else {
      value = args[1];
      return null;
    }
  }

  private final Row resultSet =
      (Row) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {Row.class}, this);

  private final Statement statement =
      (Statement)
          Proxy.newProxyInstance(
              getClass().getClassLoader(), new Class<?>[] {io.r2dbc.spi.Statement.class}, this);

  @SuppressWarnings("unchecked")
  @Test
  public void test() throws MalformedURLException {
    List<Pair<?, ?>> valueAndType = new ArrayList<Pair<?, ?>>();
    valueAndType.add(Pair.of(new BigDecimal("1"), new BigDecimalType()));
    valueAndType.add(Pair.of(new BigInteger("2"), new BigIntegerType()));
    valueAndType.add(Pair.of(new BigDecimal("1.0"), new BigDecimalAsDoubleType()));
    valueAndType.add(Pair.of(new BigInteger("2"), new BigIntegerAsLongType()));
    // valueAndType.add(Pair.of(Boolean.TRUE,         new BooleanType()));
    valueAndType.add(Pair.of((byte) 1, new ByteType()));
    valueAndType.add(Pair.of(new byte[0], new BytesType()));
    //        valueAndType.add(Pair.of(Calendar.getInstance(), new CalendarType()));
    valueAndType.add(Pair.of('c', new CharacterType()));
    valueAndType.add(Pair.of(Currency.getInstance("EUR"), new CurrencyType()));
    valueAndType.add(Pair.of(java.sql.Date.valueOf(LocalDate.now()), new DateType()));
    valueAndType.add(Pair.of(1.0, new DoubleType()));
    valueAndType.add(Pair.of(1.0f, new FloatType()));
    valueAndType.add(Pair.of(1, new IntegerType()));
    valueAndType.add(Pair.of(true, new NumericBooleanType()));
    valueAndType.add(Pair.of(1L, new LongType()));
    valueAndType.add(Pair.of(new Object(), new ObjectType()));
    valueAndType.add(Pair.of((short) 1, new ShortType()));
    valueAndType.add(Pair.of("", new StringType()));
    valueAndType.add(Pair.of(true, new TrueFalseType()));
    valueAndType.add(Pair.of(true, new YesNoType()));
    valueAndType.add(Pair.of(Timestamp.valueOf(LocalDateTime.now()), new TimestampType()));
    valueAndType.add(Pair.of(Time.valueOf(LocalTime.now()), new TimeType()));
    valueAndType.add(Pair.of(new URL("http://www.mysema.com"), new URLType()));
    valueAndType.add(Pair.of(new java.util.Date(), new UtilDateType()));

    //        valueAndType.add(Pair.of(new DateTime(), new DateTimeType()));
    //        valueAndType.add(Pair.of(new LocalDateTime(), new LocalDateTimeType()));
    //        valueAndType.add(Pair.of(new LocalDate(), new LocalDateType()));
    //        valueAndType.add(Pair.of(new LocalTime(), new LocalTimeType()));

    valueAndType.add(Pair.of(Gender.MALE, new EnumByNameType<Gender>(Gender.class)));
    valueAndType.add(Pair.of(Gender.MALE, new EnumByOrdinalType<Gender>(Gender.class)));

    valueAndType.add(Pair.of(EasyMock.createNiceMock(Blob.class), new BlobType()));
    valueAndType.add(Pair.of(EasyMock.createNiceMock(Clob.class), new ClobType()));

    valueAndType.add(Pair.of(UUID.randomUUID(), new UtilUUIDType()));
    //        valueAndType.add(Pair.of(UUID.randomUUID(), new UtilUUIDType(false)));
    BindMarker bindMarker = BindMarkersFactory.anonymous("?").create().next();

    for (Pair pair : valueAndType) {
      BindTarget bindTarget = new StatementWrapper(statement);

      value = null;
      Type type = (Type) pair.getSecond();
      assertNull(type.toString(), type.getValue(resultSet, 0));
      type.setValue(bindMarker, bindTarget, pair.getFirst());
      assertThat(type.getValue(resultSet, 0)).isEqualTo(pair.getFirst());
    }
  }
}
