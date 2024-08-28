package com.querydsl.example.config;

import com.querydsl.example.dao.CustomerDao;
import com.querydsl.example.dao.OrderDao;
import com.querydsl.example.dao.PersonDao;
import com.querydsl.example.dao.ProductDao;
import com.querydsl.example.dao.SupplierDao;
import com.querydsl.example.dto.Address;
import com.querydsl.example.dto.Customer;
import com.querydsl.example.dto.CustomerAddress;
import com.querydsl.example.dto.CustomerPaymentMethod;
import com.querydsl.example.dto.Order;
import com.querydsl.example.dto.OrderProduct;
import com.querydsl.example.dto.Person;
import com.querydsl.example.dto.Product;
import com.querydsl.example.dto.ProductL10n;
import com.querydsl.example.dto.Supplier;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TestDataServiceImpl implements TestDataService {

  @Autowired CustomerDao customerDao;
  @Autowired OrderDao orderDao;
  @Autowired PersonDao personDao;
  @Autowired ProductDao productDao;
  @Autowired SupplierDao supplierDao;

  @Override
  public void addTestData() {
    // suppliers
    var supplier = new Supplier();
    supplier.setCode("acme");
    supplier.setName("ACME");
    supplierDao.save(supplier);

    var supplier2 = new Supplier();
    supplier2.setCode("bigs");
    supplier2.setName("BigS");
    supplierDao.save(supplier2);

    // products
    var product = new Product();
    product.setName("Screwdriver");
    product.setPrice(12.0);
    product.setSupplier(supplier);

    var l10nEn = new ProductL10n();
    l10nEn.setLang("en");
    l10nEn.setName("Screwdriver");

    var l10nDe = new ProductL10n();
    l10nDe.setLang("de");
    l10nDe.setName("Schraubenzieher");

    product.setLocalizations(new HashSet<>(Arrays.asList(l10nEn, l10nDe)));
    productDao.save(product);

    var product2 = new Product();
    product2.setName("Hammer");
    product2.setPrice(5.0);
    product2.setSupplier(supplier2);

    l10nEn = new ProductL10n();
    l10nEn.setLang("en");
    l10nEn.setName("Hammer");

    product2.setLocalizations(Collections.singleton(l10nEn));
    productDao.save(product2);

    // persons
    var person = new Person();
    person.setFirstName("John");
    person.setLastName("Doe");
    person.setEmail("john.doe@aexample.com");
    personDao.save(person);

    var person2 = new Person();
    person2.setFirstName("Mary");
    person2.setLastName("Blue");
    person2.setEmail("mary.blue@example.com");
    personDao.save(person2);

    // customers
    var address = new Address();
    address.setStreet("Mainstreet 1");
    address.setZip("00100");
    address.setTown("Helsinki");
    address.setCountry("FI");

    var customerAddress = new CustomerAddress();
    customerAddress.setAddress(address);
    customerAddress.setAddressTypeCode("office");
    customerAddress.setFromDate(LocalDate.now());

    var customer = new Customer();
    customer.setAddresses(Collections.singleton(customerAddress));
    customer.setContactPerson(person);
    customer.setName("SmallS");
    customerDao.save(customer);

    var customer2 = new Customer();
    customer2.setAddresses(Collections.<CustomerAddress>emptySet());
    customer2.setContactPerson(person);
    customer2.setName("MediumM");
    customerDao.save(customer2);

    // orders
    var orderProduct = new OrderProduct();
    orderProduct.setComments("my comments");
    orderProduct.setProductId(product.getId());
    orderProduct.setQuantity(4);

    var paymentMethod = new CustomerPaymentMethod();
    paymentMethod.setCardNumber("11111111111");
    paymentMethod.setCustomerId(customer.getId());
    paymentMethod.setFromDate(LocalDate.now());
    paymentMethod.setPaymentMethodCode("abc");

    var order = new Order();
    order.setCustomerPaymentMethod(paymentMethod);
    order.setOrderPlacedDate(LocalDate.now());
    order.setOrderProducts(Collections.singleton(orderProduct));
    order.setTotalOrderPrice(13124.00);
    orderDao.save(order);
  }
}
