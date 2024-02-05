package com.querydsl.example.jpa.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tuser")
public class User extends BaseEntity {
  @Column(unique = true)
  private String username;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "poster")
  private Set<Tweet> tweets = new HashSet<Tweet>();

  public User() {}

  public User(Long id, String username) {
    setId(id);
    this.username = username;
  }

  public User(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setTweets(Set<Tweet> tweets) {
    this.tweets = tweets;
  }

  public Set<Tweet> getTweets() {
    return tweets;
  }
}
