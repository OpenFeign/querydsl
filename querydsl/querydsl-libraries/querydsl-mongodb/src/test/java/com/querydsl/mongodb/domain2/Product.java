package com.querydsl.mongodb.domain2;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.UUID;

@Entity
public class Product {
  @Id private String id = UUID.randomUUID().toString();
  private double totalStock;
  private double reservedStock;
  private double minStock;
  private double maxStock;
  private String shotDesc;
  private String description;

  public Product() {}

  public Product(
      double totalStock,
      double reservedStock,
      double minStock,
      double maxStock,
      String shotDesc,
      String description) {
    this.totalStock = totalStock;
    this.reservedStock = reservedStock;
    this.minStock = minStock;
    this.maxStock = maxStock;
    this.shotDesc = shotDesc;
    this.description = description;
  }

  public double getTotalStock() {
    return totalStock;
  }

  public void setTotalStock(double totalStock) {
    this.totalStock = totalStock;
  }

  public double getReservedStock() {
    return reservedStock;
  }

  public void setReservedStock(double reservedStock) {
    this.reservedStock = reservedStock;
  }

  public double getMinStock() {
    return minStock;
  }

  public void setMinStock(double minStock) {
    this.minStock = minStock;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public double getMaxStock() {
    return maxStock;
  }

  public void setMaxStock(double maxStock) {
    this.maxStock = maxStock;
  }

  public String getShotDesc() {
    return shotDesc;
  }

  public void setShotDesc(String shotDesc) {
    this.shotDesc = shotDesc;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
