package com.querydsl.example.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerPaymentMethod {

    private Long id;

    private Long customerId;

    private String cardNumber, otherDetails, paymentMethodCode;

    private LocalDate fromDate, toDate;

}
