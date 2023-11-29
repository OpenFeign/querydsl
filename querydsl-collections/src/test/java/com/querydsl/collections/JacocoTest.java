package com.querydsl.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.querydsl.core.alias.Alias;
import com.querydsl.core.types.dsl.EntityPathBase;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class JacocoTest {

  public static class CloneableVO {}

  public static class CloneableKlasse implements Cloneable {
    private CloneableVO value;
    private Integer otherValue;

    public CloneableVO getValue() {
      return value;
    }

    public void setValue(CloneableVO value) {
      this.value = value;
    }

    public Integer getOtherValue() {
      return otherValue;
    }

    public void setOtherValue(Integer otherValue) {
      this.otherValue = otherValue;
    }

    @Override
    public Object clone() {
      try {
        return super.clone();
      } catch (final CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Test
  public void withSimpleClass() {
    List<CloneableKlasse> vos = new ArrayList<CloneableKlasse>();
    for (int i = 0; i < 5; i++) {
      CloneableKlasse vo = new CloneableKlasse();
      vo.setOtherValue(i);
      vos.add(vo);
    }
    CloneableKlasse vo = Alias.alias(CloneableKlasse.class, "vo");
    assertNotNull(vo);
    CollQuery<?> query = new CollQuery<Void>();
    final EntityPathBase<CloneableKlasse> fromVo = Alias.$(vo);
    assertNotNull(fromVo);
    query.from(fromVo, vos);
    query.where(Alias.$(vo.getOtherValue()).eq(1));
    List<CloneableKlasse> result = query.select(Alias.$(vo)).fetch();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(Integer.valueOf(1), result.get(0).getOtherValue());
  }
}
