package com.querydsl.example.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class CustomerPaymentMethod {

  private Long id;

  private Long customerId;

  private String cardNumber, otherDetails, paymentMethodCode;
  private LocalDate fromDate, toDate;
}
