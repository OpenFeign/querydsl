package fluentq.codegen;

import fluentq.core.annotations.QueryEmbeddable;
import fluentq.core.annotations.QueryEntity;

public class QueryEmbeddable2Test extends AbstractExporterTest {

  @QueryEntity
  public static class User {

    Complex<String> complex;
  }

  @QueryEmbeddable
  public static class Complex<T extends Comparable<T>> implements Comparable<Complex<T>> {

    T a;

    @Override
    public int compareTo(Complex<T> arg0) {
      return 0;
    }

    @Override
    public boolean equals(Object o) {
      return o == this;
    }
  }
}
