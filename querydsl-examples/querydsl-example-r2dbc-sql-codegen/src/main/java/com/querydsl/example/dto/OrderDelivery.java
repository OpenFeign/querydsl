package com.querydsl.example.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class OrderDelivery {

  private String deliveryStatusCode;

  private LocalDate reportedDate;
}
