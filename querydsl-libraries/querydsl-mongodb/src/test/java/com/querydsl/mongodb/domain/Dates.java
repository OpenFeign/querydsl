package com.querydsl.mongodb.domain;

import dev.morphia.annotations.Entity;
import java.util.Date;

@Entity
public class Dates extends AbstractEntity {

  private Date date;

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
