package com.querydsl.example.dao;

import static com.querydsl.core.types.Projections.bean;
import static com.querydsl.example.sql.QCustomer.customer;
import static com.querydsl.example.sql.QCustomerAddress.customerAddress;
import static com.querydsl.example.sql.QPerson.person;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.QBean;
import com.querydsl.example.dto.Customer;
import com.querydsl.example.dto.CustomerAddress;
import com.querydsl.example.dto.Person;
import com.querydsl.sql.SQLQueryFactory;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerDaoImpl implements CustomerDao {

  @Autowired SQLQueryFactory queryFactory;

  final QBean<CustomerAddress> customerAddressBean =
      bean(
          CustomerAddress.class,
          customerAddress.addressTypeCode,
          customerAddress.fromDate,
          customerAddress.toDate,
          customerAddress.address);

  final QBean<Customer> customerBean =
      bean(
          Customer.class,
          customer.id,
          customer.name,
          bean(Person.class, person.all()).as("contactPerson"),
          GroupBy.set(customerAddressBean).as("addresses"));

  @Override
  public Customer findById(long id) {
    var customers = findAll(customer.id.eq(id));
    return customers.isEmpty() ? null : customers.get(0);
  }

  @Override
  public List<Customer> findAll(Predicate... where) {
    return queryFactory
        .from(customer)
        .leftJoin(customer.contactPersonFk, person)
        .leftJoin(customer._customer3Fk, customerAddress)
        .where(where)
        .transform(GroupBy.groupBy(customer.id).list(customerBean));
  }

  @Override
  public Customer save(Customer c) {
    var id = c.getId();

    if (id == null) {
      id =
          queryFactory
              .insert(customer)
              .set(customer.name, c.getName())
              .set(customer.contactPersonId, c.getContactPerson().getId())
              .executeWithKey(customer.id);
      c.setId(id);
    } else {
      queryFactory
          .update(customer)
          .set(customer.name, c.getName())
          .set(customer.contactPersonId, c.getContactPerson().getId())
          .where(customer.id.eq(c.getId()))
          .execute();

      // delete address rows
      queryFactory.delete(customerAddress).where(customerAddress.customerId.eq(id)).execute();
    }

    var insert = queryFactory.insert(customerAddress);
    for (CustomerAddress ca : c.getAddresses()) {
      insert
          .set(customerAddress.customerId, id)
          .set(customerAddress.address, ca.getAddress())
          .set(customerAddress.addressTypeCode, ca.getAddressTypeCode())
          .set(customerAddress.fromDate, ca.getFromDate())
          .set(customerAddress.toDate, ca.getToDate())
          .addBatch();
    }
    insert.execute();

    c.setId(id);
    return c;
  }

  @Override
  public long count() {
    return queryFactory.from(customer).fetchCount();
  }

  @Override
  public void delete(Customer c) {
    // TODO use combined delete clause
    queryFactory.delete(customerAddress).where(customerAddress.customerId.eq(c.getId())).execute();

    queryFactory.delete(customer).where(customer.id.eq(c.getId())).execute();
  }
}
