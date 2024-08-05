package com.querydsl.example.dto;

import io.github.openfeign.querydsl.sql.json.*;
import lombok.Data;

@Data
public class Address implements JsonEntity {

  private Long id;

  private String street, zip, town, state, country;

  private String otherDetails;
}
