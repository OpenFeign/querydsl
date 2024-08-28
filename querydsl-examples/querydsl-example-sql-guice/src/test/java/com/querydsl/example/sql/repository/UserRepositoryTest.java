package com.querydsl.example.sql.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.example.sql.model.Tweet;
import com.querydsl.example.sql.model.Usert;
import jakarta.inject.Inject;
import org.junit.Test;

public class UserRepositoryTest extends AbstractPersistenceTest {
  @Inject private UserRepository repository;

  @Inject private TweetRepository tweetRepository;

  @Test
  public void save_and_get_by_id() {
    var username = "jackie";
    var user = new Usert();
    user.setUsername(username);
    var id = repository.save(user);
    assertThat(repository.findById(id).getUsername()).isEqualTo(username);
  }

  @Test
  public void get_all() {
    var user = new Usert();
    user.setUsername("jimmy");
    repository.save(user);
    assertThat(repository.all().size() == 1).isTrue();
  }

  @Test
  public void get_all_with_tweet_count() {
    var user = new Usert();
    user.setUsername("jimmy");
    var posterId = repository.save(user);

    var tw3 = new Tweet();
    tw3.setPosterId(posterId);
    tw3.setContent("#EpicFail");
    tweetRepository.save(tw3);

    var infos = repository.allWithTweetCount();
    assertThat(infos).isNotEmpty();
    for (UserInfo info : infos) {
      assertThat(info.getUsername()).isNotNull();
    }
  }
}
