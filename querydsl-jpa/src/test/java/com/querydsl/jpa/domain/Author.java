package com.querydsl.jpa.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "author_")
public class Author implements Serializable {

  private static final long serialVersionUID = -1893968697250846661L;

  @Id @GeneratedValue private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @OneToMany(mappedBy = "author")
  @OrderBy("title")
  private Set<Book> books;

  public Set<Book> getBooks() {
    return books;
  }

  public void setBooks(Set<Book> books) {
    this.books = books;
  }
}
