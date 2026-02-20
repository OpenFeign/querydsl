package com.querydsl.mongodb;

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.querydsl.mongodb.domain2.Product;
import com.querydsl.mongodb.domain2.QProduct;
import com.querydsl.mongodb.morphia.MorphiaQuery;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("com.querydsl.core.testutil.MongoDB")
public class MongoExpressionTest {
  private final MongoClient mongo;
  private final Datastore ds;
  private final String dbname = "testdb";
  private final QProduct product = QProduct.product;

  private final List<Product> data = new ArrayList<>();

  @SuppressWarnings({"deprecation", "removal"})
  public MongoExpressionTest() {
    mongo = MongoClients.create();
    ds = Morphia.createDatastore(mongo, dbname);
    ds.getMapper().map(Product.class);
  }

  @BeforeEach
  void setUp() {
    ds.getCollection(Product.class).deleteMany(new org.bson.Document());
    data.clear();
    add(new Product(10.0, 2.0, 20.0, 30.0, "Lite", "Light everyday product"));
    add(new Product(50.0, 0.0, 40.0, 60.0, "Lux", "High-value product with no discount"));
    add(
        new Product(
            5.0, 0.0, 5.0, 10.0, "LiTiny", "Small and inexpensive product, similar to Lite"));
    add(new Product(15.0, 1.0, 30.0, 40.0, "Mid", "Medium product with a small discount"));
    add(new Product(60.0, 5.0, 55.0, 70.0, "Prem", "Premium product with discount"));
    add(new Product(25.0, 10.0, 25.0, 30.0, "MidMax", "Mid-range product with large discount"));
    add(new Product(35.0, 3.0, 20.0, 35.0, "Univ", "Universal product with medium discount"));
    add(new Product(1.0, 0.0, 2.0, 5.0, "Mini", "Mini practical product"));
    add(
        new Product(
            100.0, 50.0, 120.0, 150.0, "Luxo", "Luxury product with high price and discount"));
    add(new Product(80.0, 0.0, 70.0, 90.0, "Large", "Large product with no discount"));
    add(new Product(9.0, 1.0, 9.0, 10.0, "Tiny", "Tiny product with minimal discount"));
    add(new Product(11.0, 11.0, 12.0, 15.0, "Full", "Product with full discount"));
    add(new Product(12.0, 6.0, 12.0, 15.0, "Mod", "Medium product with moderate discount"));
    add(
        new Product(
            49.0, 48.0, 60.0, 70.0, "NearFull", "High-value product with nearly full discount"));
    add(new Product(40.0, 39.0, 40.0, 50.0, "Almost", "Product almost at maximum discount"));
    add(new Product(70.0, 0.0, 65.0, 80.0, "LargeX", "Large product variant, no discount"));
    add(
        new Product(
            22.0, 2.0, 21.0, 30.0, "MidLite", "Mid product with slight discount, similar to Lite"));
    add(
        new Product(
            21.0, 2.0, 22.0, 25.0, "MidTiny", "Mid product with slight discount, similar to Tiny"));
    add(new Product(7.0, 7.0, 8.0, 9.0, "FullMini", "Full discount product in mini size"));
    add(new Product(3.0, 1.0, 3.0, 4.0, "MiniX", "Mini product variant"));
    add(
        new Product(
            200.0,
            150.0,
            250.0,
            300.0,
            "LuxMax",
            "Extremely luxurious product with maximum price"));
    add(new Product(0.0, 0.0, 1.0, 5.0, "Free", "Almost free product"));
    add(
        new Product(
            18.0,
            8.0,
            17.0,
            20.0,
            "ModMid",
            "Medium product with moderate discount, bridging Mod and Mid"));
    add(
        new Product(
            17.0,
            8.0,
            18.0,
            20.0,
            "ModLite",
            "Medium product with moderate discount, bridging Mod and Lite"));
  }

  private void add(Product p) {
    data.add(p);
    ds.save(p);
  }

  // Field-to-field: totalStock <= minStock
  @Test
  void totalStock_loe_minStock_fieldToField() {
    List<Product> results = query().where(product.totalStock.loe(product.minStock)).fetch();
    long expected = data.stream().filter(p -> p.getTotalStock() <= p.getMinStock()).count();
    assertThat(results).hasSize((int) expected);
  }

  // Field-to-field: totalStock < minStock
  @Test
  void totalStock_lt_minStock_fieldToField() {
    List<Product> results = query().where(product.totalStock.lt(product.minStock)).fetch();
    long expected = data.stream().filter(p -> p.getTotalStock() < p.getMinStock()).count();
    assertThat(results).hasSize((int) expected);
  }

  // Field-to-field: totalStock > minStock
  @Test
  void totalStock_gt_minStock_fieldToField() {
    List<Product> results = query().where(product.totalStock.gt(product.minStock)).fetch();
    long expected = data.stream().filter(p -> p.getTotalStock() > p.getMinStock()).count();
    assertThat(results).hasSize((int) expected);
  }

  // Field-to-field: totalStock >= minStock
  @Test
  void totalStock_goe_minStock_fieldToField() {
    List<Product> results = query().where(product.totalStock.goe(product.minStock)).fetch();
    long expected = data.stream().filter(p -> p.getTotalStock() >= p.getMinStock()).count();
    assertThat(results).hasSize((int) expected);
  }

  // Constant: totalStock < 10.0
  @Test
  void totalStock_lt_constant() {
    double threshold = 10.0;
    List<Product> results = query().where(product.totalStock.lt(threshold)).fetch();
    long expected = data.stream().filter(p -> p.getTotalStock() < threshold).count();
    assertThat(results).hasSize((int) expected);
  }

  // Constant via Java variable: totalStock > threshold
  @Test
  void totalStock_gt_variableConstant() {
    double threshold = 10.0;
    List<Product> results = query().where(product.totalStock.gt(threshold)).fetch();
    long expected = data.stream().filter(p -> p.getTotalStock() > threshold).count();
    assertThat(results).hasSize((int) expected);
  }

  // Constant: totalStock <= 10.0
  @Test
  void totalStock_loe_constant() {
    double max = 10.0;
    List<Product> results = query().where(product.totalStock.loe(max)).fetch();
    long expected = data.stream().filter(p -> p.getTotalStock() <= max).count();
    assertThat(results).hasSize((int) expected);
  }

  // Constant via Java variable: totalStock >= minValue
  @Test
  void totalStock_goe_variableConstant() {
    double minValue = 5.0;
    List<Product> results = query().where(product.totalStock.goe(minValue)).fetch();
    long expected = data.stream().filter(p -> p.getTotalStock() >= minValue).count();
    assertThat(results).hasSize((int) expected);
  }

  // Additional field-to-field sanity: totalStock > reservedStock
  @Test
  void totalStock_gt_reservedStock_fieldToField() {
    List<Product> results = query().where(product.totalStock.gt(product.reservedStock)).fetch();
    long expected = data.stream().filter(p -> p.getTotalStock() > p.getReservedStock()).count();
    assertThat(results).hasSize((int) expected);
  }

  // Constant range: totalStock BETWEEN 5.0 AND 10.0 (inclusive)
  @Test
  void totalStock_between_constants_inclusive() {
    double lower = 5.0, upper = 10.0;
    List<Product> results = query().where(product.totalStock.between(lower, upper)).fetch();
    long expected =
        data.stream().filter(p -> p.getTotalStock() >= lower && p.getTotalStock() <= upper).count();
    assertThat(results).hasSize((int) expected);
  }

  // Variable range: totalStock BETWEEN lower AND upper
  @Test
  void totalStock_between_variableBounds() {
    double lower = 6.0;
    double upper = 100.0;
    List<Product> results = query().where(product.totalStock.between(lower, upper)).fetch();
    long expected =
        data.stream().filter(p -> p.getTotalStock() >= lower && p.getTotalStock() <= upper).count();
    assertThat(results).hasSize((int) expected);
  }

  // Dynamic columns (emulated BETWEEN): reservedStock <= minStock <= totalStock
  @Test
  void totalStock_between_dynamicFields_viaComparisons() {
    List<Product> results =
        query().where(product.minStock.between(product.reservedStock, product.totalStock)).fetch();
    long expected =
        data.stream()
            .filter(
                p ->
                    p.getMinStock() >= p.getReservedStock() && p.getMinStock() <= p.getTotalStock())
            .count();
    assertThat(results).hasSize((int) expected);
  }

  // Constant list: totalStock IN (5.0, 50.0)
  @Test
  void totalStock_in_constants() {
    Set<Double> values = Set.of(5.0, 50.0);
    List<Product> results = query().where(product.totalStock.in(values)).fetch();
    long expected = data.stream().filter(p -> values.contains(p.getTotalStock())).count();
    assertThat(results).hasSize((int) expected);
  }

  // Variable list: totalStock IN list
  @Test
  void totalStock_in_variableList() {
    List<Double> values = Arrays.asList(10.0, 15.0);
    List<Product> results = query().where(product.totalStock.in(values)).fetch();
    long expected = data.stream().filter(p -> values.contains(p.getTotalStock())).count();
    assertThat(results).hasSize((int) expected);
  }

  // Constant list: totalStock NOT IN (50.0)
  @Test
  void totalStock_notIn_constants() {
    Set<Double> exclude = Set.of(50.0);
    List<Product> results = query().where(product.totalStock.notIn(exclude)).fetch();
    long expected = data.stream().filter(p -> !exclude.contains(p.getTotalStock())).count();
    assertThat(results).hasSize((int) expected);
  }

  // Variable list: totalStock NOT IN list
  @Test
  void totalStock_notIn_variableList() {
    List<Double> exclude = Arrays.asList(5.0, 10.0);
    List<Product> results = query().where(product.totalStock.notIn(exclude)).fetch();
    long expected = data.stream().filter(p -> !exclude.contains(p.getTotalStock())).count();
    assertThat(results).hasSize((int) expected);
  }

  @Test
  void minStock_in_dynamicFields_viaComparisonsSinge() {
    List<Product> results = query().where(product.minStock.in(product.reservedStock)).fetch();
    long expected = data.stream().filter(p -> p.getMinStock() == p.getReservedStock()).count();

    assertThat(results).hasSize((int) expected);
  }

  @Test
  void minStock_in_dynamicFields_2() {
    List<Product> results =
        query()
            .where(product.minStock.in(product.reservedStock, product.totalStock, product.maxStock))
            .fetch();
    long expected =
        data.stream()
            .filter(
                p ->
                    p.getMinStock() == p.getReservedStock() || p.getMinStock() == p.getTotalStock())
            .count();

    assertThat(results).hasSize((int) expected);
  }

  @Test
  void minStock_in_dynamicFields_3() {
    List<Product> results =
        query()
            .where(product.minStock.in(product.reservedStock, product.totalStock, product.maxStock))
            .fetch();
    long expected =
        data.stream()
            .filter(
                p ->
                    p.getMinStock() == p.getReservedStock()
                        || p.getMinStock() == p.getTotalStock()
                        || p.getMinStock() == p.getMaxStock())
            .count();

    assertThat(results).hasSize((int) expected);
  }

  @Test
  void minStock_notIn() {
    List<Product> results = query().where(product.minStock.notIn(3, 4, 5)).fetch();
    long expected =
        data.stream()
            .filter(p -> p.getMinStock() != 3 && p.getMinStock() != 4 && p.getMinStock() != 5)
            .count();

    assertThat(results).hasSize((int) expected);
  }

  @Test
  void minStock_ne() {
    List<Product> results = query().where(product.minStock.ne(3.0)).fetch();
    long expected = data.stream().filter(p -> p.getMinStock() != 3).count();

    assertThat(results).hasSize((int) expected);
  }

  @Test
  void minStock_ne_dynamic() {
    List<Product> results = query().where(product.minStock.ne(product.maxStock)).fetch();
    long expected = data.stream().filter(p -> p.getMinStock() != p.getMaxStock()).count();

    assertThat(results).hasSize((int) expected);
  }

  @Test
  void minStock_notIn_dynamicFields() {
    List<Product> results =
        query()
            .where(
                product.minStock.notIn(product.reservedStock, product.totalStock, product.maxStock))
            .fetch();
    long expected =
        data.stream()
            .filter(
                p ->
                    p.getMinStock() != p.getReservedStock()
                        && p.getMinStock() != p.getTotalStock()
                        && p.getMinStock() != p.getMaxStock())
            .count();

    assertThat(results).hasSize((int) expected);
  }

  @Test
  void minStock_eq_dynamicField() {
    List<Product> results = query().where(product.minStock.eq(product.totalStock)).fetch();
    long expected = data.stream().filter(p -> p.getMinStock() == p.getTotalStock()).count();
    assertThat(results).hasSize((int) expected);
  }

  @Test
  void minStock_or_test() {
    List<Product> results =
        query()
            .where(
                product
                    .minStock
                    .eq(product.totalStock)
                    .or(product.minStock.eq(product.reservedStock)))
            .fetch();
    long expected =
        data.stream()
            .filter(
                p ->
                    p.getMinStock() == p.getTotalStock() || p.getMinStock() == p.getReservedStock())
            .count();
    assertThat(results).hasSize((int) expected);
  }

  @Test
  void description_eq_constant() {
    List<Product> results = query().where(product.shotDesc.eq("Lux")).fetch();
    long expected = data.stream().filter(p -> "Lux".equals(p.getShotDesc())).count();
    assertThat(results).hasSize((int) expected);
  }

  @Test
  void description_eq_variable() {
    List<Product> results = query().where(product.description.eq(product.shotDesc)).fetch();
    long expected = data.stream().filter(p -> p.getDescription().equals(p.getShotDesc())).count();
    assertThat(results).hasSize((int) expected);
  }

  @Test
  void description_startWith() {
    String prefix = "Li";
    List<Product> results = query().where(product.description.startsWith(prefix)).fetch();
    long expected = data.stream().filter(p -> p.getDescription().startsWith(prefix)).count();
    assertThat(results).hasSize((int) expected);
  }

  @Test
  void description_startWith_variable() {
    List<Product> results = query().where(product.description.startsWith(product.shotDesc)).fetch();
    long expected =
        data.stream().filter(p -> p.getDescription().startsWith(p.getShotDesc())).count();
    assertThat(results).hasSize((int) expected);
  }

  @Test
  void description_startWith_ignore_variable() {
    List<Product> results =
        query().where(product.description.startsWithIgnoreCase(product.shotDesc)).fetch();
    long expected =
        data.stream()
            .filter(p -> p.getDescription().toLowerCase().startsWith(p.getShotDesc().toLowerCase()))
            .count();
    assertThat(results).hasSize((int) expected);
  }

  @Test
  void description_contains_variable() {
    List<Product> results = query().where(product.description.contains(product.shotDesc)).fetch();
    long expected = data.stream().filter(p -> p.getDescription().contains(p.getShotDesc())).count();
    assertThat(results).hasSize((int) expected);
  }

  @Test
  void description_contains_ignoreCase_variable() {
    List<Product> results =
        query().where(product.description.containsIgnoreCase(product.shotDesc)).fetch();
    long expected =
        data.stream()
            .filter(p -> p.getDescription().toLowerCase().contains(p.getShotDesc().toLowerCase()))
            .count();
    assertThat(results).hasSize((int) expected);
  }

  @Test
  void description_endWith_variable() {
    List<Product> results = query().where(product.description.endsWith(product.shotDesc)).fetch();
    long expected = data.stream().filter(p -> p.getDescription().endsWith(p.getShotDesc())).count();
    assertThat(results).hasSize((int) expected);
  }

  @Test
  void description_endWith_ignoreCase_variable() {
    List<Product> results =
        query().where(product.description.endsWithIgnoreCase(product.shotDesc)).fetch();
    long expected =
        data.stream()
            .filter(p -> p.getDescription().toLowerCase().endsWith(p.getShotDesc().toLowerCase()))
            .count();
    assertThat(results).hasSize((int) expected);
  }

  private MorphiaQuery<Product> query() {
    return new MorphiaQuery<>(ds, product);
  }
}
