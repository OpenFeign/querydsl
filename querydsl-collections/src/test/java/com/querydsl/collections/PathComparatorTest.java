package com.querydsl.collections;

import static com.querydsl.collections.PathComparator.pathComparator;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import org.junit.Before;
import org.junit.Test;

public class PathComparatorTest {

  private Comparator<Car> comparator;

  @Before
  public void setUpComparator() {
    comparator = pathComparator(QCar.car.horsePower);
  }

  @Test
  public void equalReference() {
    Car car = new Car();
    assertThat(comparator.compare(car, car)).isEqualTo(0);
  }

  @Test
  public void semanticallyEqual() {
    Car car = new Car();
    car.setModel("car");
    car.setHorsePower(50);

    Car similarCar = new Car();
    similarCar.setModel("car");
    similarCar.setHorsePower(50);

    assertThat(comparator.compare(car, similarCar)).isEqualTo(0);
  }

  @Test
  public void leftIsNull() {
    assertThat(comparator.compare(null, new Car())).isEqualTo(-1);
  }

  @Test
  public void rightIsNull() {
    assertThat(comparator.compare(new Car(), null)).isEqualTo(1);
  }

  @Test
  public void compareOnValue() {
    Car car = new Car();
    car.setHorsePower(50);

    Car betterCar = new Car();
    betterCar.setHorsePower(150);

    assertThat(comparator.compare(car, betterCar)).isEqualTo(-1);
  }
}
