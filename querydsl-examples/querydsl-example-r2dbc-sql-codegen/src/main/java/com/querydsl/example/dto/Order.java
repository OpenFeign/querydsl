package com.querydsl.example.dto;

import java.time.LocalDate;
import java.util.Set;
import lombok.Data;

@Data
public class Order {

  private Long id;

  private CustomerPaymentMethod customerPaymentMethod;

  private LocalDate orderPlacedDate, orderPaidDate;

  private String orderStatus;

  private Double totalOrderPrice;

  private Set<OrderProduct> orderProducts;
}
