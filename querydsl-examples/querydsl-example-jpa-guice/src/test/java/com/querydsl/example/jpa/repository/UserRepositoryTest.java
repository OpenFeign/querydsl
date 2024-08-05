package com.querydsl.example.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.example.jpa.model.User;
import jakarta.inject.Inject;
import org.junit.Test;

public class UserRepositoryTest extends AbstractPersistenceTest {
  @Inject private UserRepository repository;

  @Test
  public void save_and_get_by_id() {
    var username = "jackie";
    var user = new User(username);
    repository.save(user);
    assertThat(repository.findById(user.getId()).getUsername()).isEqualTo(username);
  }

  @Test
  public void get_all() {
    repository.save(new User("jimmy"));
    assertThat(repository.all().size() == 1).isTrue();
  }
}
