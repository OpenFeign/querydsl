package com.querydsl.jpa.domain;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "book_")
public class Book implements Serializable {

  private static final long serialVersionUID = -9029792723035681319L;

  @Id @GeneratedValue private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  private String title;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @ManyToOne
  @JoinColumn(name = "AUTHOR_ID")
  private Author author;

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }
}
