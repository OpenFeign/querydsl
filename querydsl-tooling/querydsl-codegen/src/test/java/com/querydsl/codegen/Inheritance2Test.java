package com.querydsl.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.codegen.utils.model.TypeExtends;
import com.querydsl.core.annotations.QueryEntity;
import java.io.File;
import org.junit.Test;

public class Inheritance2Test {

  @QueryEntity
  public abstract class Base<T extends Base<T>> {
    @SuppressWarnings("unchecked")
    Base2 base;

    Base2<?, ?> base2;
  }

  @QueryEntity
  public abstract class Base2<T extends Base2<T, U>, U extends IFace> {}

  @QueryEntity
  public abstract class BaseSub extends Base<BaseSub> {}

  @QueryEntity
  public abstract class BaseSub2<T extends BaseSub2<T>> extends Base<T> {}

  @QueryEntity
  public abstract class Base2Sub<T extends IFace> extends Base2<Base2Sub<T>, T> {}

  public interface IFace {}

  @Test
  public void base_base() throws SecurityException, NoSuchFieldException {
    var typeFactory = new TypeFactory();
    var field = Base.class.getDeclaredField("base");
    var type = typeFactory.get(field.getType(), field.getGenericType());
    assertThat(type.getParameters()).isEmpty();
  }

  @Test
  public void base_base2() throws SecurityException, NoSuchFieldException {
    var typeFactory = new TypeFactory();
    var field = Base.class.getDeclaredField("base2");
    var type = typeFactory.get(field.getType(), field.getGenericType());
    assertThat(type.getParameters()).hasSize(2);
    assertThat(((TypeExtends) type.getParameters().get(0)).getVarName()).isNull();
    assertThat(((TypeExtends) type.getParameters().get(1)).getVarName()).isNull();
  }

  @Test
  public void test() {
    var exporter = new GenericExporter();
    exporter.setTargetFolder(new File("target/" + getClass().getSimpleName()));
    exporter.export(getClass().getClasses());
  }
}
