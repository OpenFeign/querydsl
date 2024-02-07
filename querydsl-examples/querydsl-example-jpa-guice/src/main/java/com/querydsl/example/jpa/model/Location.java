package com.querydsl.example.jpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(
    name = "location",
    uniqueConstraints = @UniqueConstraint(columnNames = {"longitude", "latitude"}))
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Location extends BaseEntity {

  private Double longitude;

  private Double latitude;

  public Location() {}

  public Location(Double longitude, Double latitude) {
    this.longitude = longitude;
    this.latitude = latitude;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }
}
