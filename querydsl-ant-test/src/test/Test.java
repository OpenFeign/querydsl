package test;

import com.mysema.query.annotations.QueryEntity;
import java.math.BigDecimal;

@QueryEntity
public class Test {

  private String property;

  private int intProperty;

  private BigDecimal bigDecimal;
}
