package com.querydsl.example.config;

import com.querydsl.example.dao.*;
import com.querydsl.example.dto.*;
import com.querydsl.r2dbc.R2DBCQueryFactory;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TestDataService {

  @Autowired R2DBCQueryFactory r2DBCQueryFactory;
  @Autowired CustomerDao customerDao;
  @Autowired OrderDao orderDao;
  @Autowired PersonDao personDao;
  @Autowired ProductDao productDao;
  @Autowired SupplierDao supplierDao;
  public Long customer1;
  public Long order1;
  public Long product1;
  public Long person1;
  public Long supplier1;

  public void addTestData() {
    // suppliers
    Supplier supplier = new Supplier();
    supplier.setCode("acme");
    supplier.setName("ACME");
    supplier1 = supplierDao.save(supplier).block().getId();

    Supplier supplier2 = new Supplier();
    supplier2.setCode("bigs");
    supplier2.setName("BigS");
    supplierDao.save(supplier2).block();

    // products
    Product product = new Product();
    product.setName("Screwdriver");
    product.setPrice(12.0);
    product.setSupplier(supplier);

    ProductL10n l10nEn = new ProductL10n();
    l10nEn.setLang("en");
    l10nEn.setName("Screwdriver");

    ProductL10n l10nDe = new ProductL10n();
    l10nDe.setLang("de");
    l10nDe.setName("Schraubenzieher");

    product.setLocalizations(Set.of(l10nEn, l10nDe));
    product1 = productDao.save(product).block().getId();

    Product product2 = new Product();
    product2.setName("Hammer");
    product2.setPrice(5.0);
    product2.setSupplier(supplier2);

    l10nEn = new ProductL10n();
    l10nEn.setLang("en");
    l10nEn.setName("Hammer");

    product2.setLocalizations(Set.of(l10nEn));
    productDao.save(product2).block();

    // persons
    Person person = new Person();
    person.setFirstName("John");
    person.setLastName("Doe");
    person.setEmail("john.doe@aexample.com");
    person1 = personDao.save(person).block().getId();

    Person person2 = new Person();
    person2.setFirstName("Mary");
    person2.setLastName("Blue");
    person2.setEmail("mary.blue@example.com");
    personDao.save(person2).block();

    // customers
    Address address = new Address();
    address.setStreet("Mainstreet 1");
    address.setZip("00100");
    address.setTown("Helsinki");
    address.setCountry("FI");

    CustomerAddress customerAddress = new CustomerAddress();
    customerAddress.setAddress(address);
    customerAddress.setAddressTypeCode("office");
    customerAddress.setFromDate(LocalDate.now());

    Customer customer = new Customer();
    customer.setAddresses(Set.of(customerAddress));
    customer.setContactPerson(person);
    customer.setName("SmallS");
    customer1 = customerDao.save(customer).block().getId();

    Customer customer2 = new Customer();
    customer2.setAddresses(Set.<CustomerAddress>of());
    customer2.setContactPerson(person);
    customer2.setName("MediumM");
    customerDao.save(customer2).block();

    // orders
    OrderProduct orderProduct = new OrderProduct();
    orderProduct.setComments("my comments");
    orderProduct.setProductId(product.getId());
    orderProduct.setQuantity(4);

    CustomerPaymentMethod paymentMethod = new CustomerPaymentMethod();
    paymentMethod.setCardNumber("11111111111");
    paymentMethod.setCustomerId(customer.getId());
    paymentMethod.setFromDate(LocalDate.now());
    paymentMethod.setPaymentMethodCode("abc");

    Order order = new Order();
    order.setCustomerPaymentMethod(paymentMethod);
    order.setOrderPlacedDate(LocalDate.now());
    order.setOrderProducts(Set.of(orderProduct));
    order.setTotalOrderPrice(13124.00);
    order1 = orderDao.save(order).block().getId();
  }

  public void removeTestData() {
    Connection c = r2DBCQueryFactory.getConnection().block();
    Statement s;

    // Disable FK
    s = c.createStatement("SET REFERENTIAL_INTEGRITY FALSE");
    Mono.from(s.execute()).block();

    // Find all tables and truncate them
    s =
        c.createStatement(
            "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES  where TABLE_SCHEMA='PUBLIC'");
    List<String> tables =
        Flux.from(s.execute())
            .flatMap(result -> result.map((row, meta) -> row.get(0, String.class)))
            .collectList()
            .block();
    for (String table : tables) {
      s = c.createStatement("TRUNCATE TABLE " + table);
      Mono.from(s.execute()).block();
    }

    // Idem for sequences
    s =
        c.createStatement(
            "SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES WHERE"
                + " SEQUENCE_SCHEMA='PUBLIC'");
    List<String> sequences =
        Flux.from(s.execute())
            .flatMap(result -> result.map((row, meta) -> row.get(0, String.class)))
            .collectList()
            .block();
    for (String seq : sequences) {
      s = c.createStatement("ALTER SEQUENCE " + seq + " RESTART WITH 1");
      Mono.from(s.execute()).block();
    }

    // Enable FK
    s = c.createStatement("SET REFERENTIAL_INTEGRITY TRUE");
    Mono.from(s.execute()).block();
  }
}
