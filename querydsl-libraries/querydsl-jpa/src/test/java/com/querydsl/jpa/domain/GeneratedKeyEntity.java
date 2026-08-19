package com.querydsl.jpa.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "generated_key_entity")
public class GeneratedKeyEntity implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  public enum Status {
    PENDING,
    ACTIVE,
    DONE
  }

  /** A second enum used to reinforce ORDINAL (numeric) storage tests. */
  public enum Priority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name_")
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "status_string_", length = 32)
  private Status statusString;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "status_ordinal_")
  private Status statusOrdinal;

  /** No {@code @Enumerated}: JPA default is {@link EnumType#ORDINAL}. */
  @Column(name = "status_default_")
  private Status statusDefault;

  @Convert(converter = GeneratedKeyStatusCodeConverter.class)
  @Column(name = "status_converted_", length = 32)
  private Status statusConverted;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "priority_ordinal_")
  private Priority priorityOrdinal;

  @Enumerated(EnumType.STRING)
  @Column(name = "priority_string_", length = 16)
  private Priority priorityString;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Status getStatusString() {
    return statusString;
  }

  public void setStatusString(Status statusString) {
    this.statusString = statusString;
  }

  public Status getStatusOrdinal() {
    return statusOrdinal;
  }

  public void setStatusOrdinal(Status statusOrdinal) {
    this.statusOrdinal = statusOrdinal;
  }

  public Status getStatusDefault() {
    return statusDefault;
  }

  public void setStatusDefault(Status statusDefault) {
    this.statusDefault = statusDefault;
  }

  public Status getStatusConverted() {
    return statusConverted;
  }

  public void setStatusConverted(Status statusConverted) {
    this.statusConverted = statusConverted;
  }

  public Priority getPriorityOrdinal() {
    return priorityOrdinal;
  }

  public void setPriorityOrdinal(Priority priorityOrdinal) {
    this.priorityOrdinal = priorityOrdinal;
  }

  public Priority getPriorityString() {
    return priorityString;
  }

  public void setPriorityString(Priority priorityString) {
    this.priorityString = priorityString;
  }
}
