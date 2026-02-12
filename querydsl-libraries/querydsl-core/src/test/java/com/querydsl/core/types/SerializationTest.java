package com.querydsl.core.types;

import static com.querydsl.core.testutil.Serialization.serialize;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.testutil.Serialization;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilderValidator;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.SimplePath;
import io.github.classgraph.ClassGraph;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class SerializationTest {

  public enum Gender {
    MALE,
    FEMALE
  }

  @Test
  void expressions() throws Exception {
    Map<Class<?>, Object> args = new HashMap<>();
    args.put(Object.class, "obj");
    args.put(BeanPath.class, new EntityPathBase<>(Object.class, "obj"));
    args.put(Class.class, Integer.class);
    args.put(Class[].class, new Class<?>[] {Object.class, Object.class});
    args.put(java.util.Date.class, new java.util.Date(0));
    args.put(java.sql.Date.class, new java.sql.Date(0));
    args.put(java.sql.Time.class, new java.sql.Time(0));
    args.put(java.sql.Timestamp.class, new java.sql.Timestamp(0));
    args.put(Expression.class, Expressions.enumPath(Gender.class, "e"));
    args.put(
        Expression[].class,
        new Expression<?>[] {Expressions.enumPath(Gender.class, "e"), Expressions.stringPath("s")});
    args.put(FactoryExpression.class, Projections.tuple(Expressions.stringPath("str")));
    args.put(GroupExpression.class, GroupBy.avg(Expressions.numberPath(Integer.class, "num")));
    args.put(Number.class, 1);
    args.put(Operator.class, Ops.AND);
    args.put(Path.class, Expressions.stringPath("str"));
    args.put(PathBuilderValidator.class, PathBuilderValidator.DEFAULT);
    args.put(PathMetadata.class, PathMetadataFactory.forVariable("obj"));
    args.put(PathInits.class, PathInits.DEFAULT);
    args.put(Predicate.class, Expressions.path(Object.class, "obj").isNull());
    args.put(QueryMetadata.class, new DefaultQueryMetadata());
    args.put(String.class, "obj");

    var reflections = new ClassGraph().enableClassInfo();
    var types = reflections.scan().getSubclasses(Expression.class.getName()).loadClasses();
    for (Class<?> type : types) {
      if (!type.isInterface()
          && !type.isMemberClass()
          && !Modifier.isAbstract(type.getModifiers())) {
        for (Constructor<?> c : type.getConstructors()) {
          var parameters = new Object[c.getParameterTypes().length];
          for (var i = 0; i < c.getParameterTypes().length; i++) {
            parameters[i] =
                Objects.requireNonNull(
                    args.get(c.getParameterTypes()[i]), c.getParameterTypes()[i].getName());
          }
          c.setAccessible(true);
          Object o = c.newInstance(parameters);
          assertThat(Serialization.serialize(o)).isEqualTo(o);
        }
      }
    }
  }

  @Test
  void order() {
    OrderSpecifier<?> order = new OrderSpecifier<>(Order.ASC, Expressions.stringPath("str"));
    assertThat(Serialization.serialize(order)).isEqualTo(order);
  }

  @Test
  void roundtrip() throws Exception {
    Path<?> path = ExpressionUtils.path(Object.class, "entity");
    SimplePath<?> path2 = Expressions.path(Object.class, "entity");
    assertThat(serialize(path)).isEqualTo(path);
    assertThat(serialize(path2)).isEqualTo(path2);
    assertThat(serialize(path2.isNull())).isEqualTo(path2.isNull());
    assertThat(serialize(path)).hasSameHashCodeAs(path);
    assertThat(serialize(path2)).hasSameHashCodeAs(path2);
    assertThat(serialize(path2.isNull())).hasSameHashCodeAs(path2.isNull());
  }
}
