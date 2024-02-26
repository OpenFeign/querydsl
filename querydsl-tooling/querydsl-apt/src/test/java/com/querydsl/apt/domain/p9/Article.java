package com.querydsl.apt.domain.p9;

import jakarta.persistence.Entity;

@Entity
public class Article {

  String name;

  Content content;

  Person author;
}
