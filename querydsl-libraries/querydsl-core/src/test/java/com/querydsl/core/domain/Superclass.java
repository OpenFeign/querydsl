package com.querydsl.core.domain;

import com.querydsl.core.annotations.QueryEmbedded;
import com.querydsl.core.annotations.QueryEntity;
import java.util.ArrayList;
import java.util.List;

@QueryEntity
public class Superclass {

  @QueryEmbedded private List<IdNamePair<String>> fooOfSuperclass = new ArrayList<>();

  public List<IdNamePair<String>> getFooOfSuperclass() {
    return fooOfSuperclass;
  }

  public void setFooOfSuperclass(List<IdNamePair<String>> fooOfSuperclass) {
    this.fooOfSuperclass = fooOfSuperclass;
  }
}
