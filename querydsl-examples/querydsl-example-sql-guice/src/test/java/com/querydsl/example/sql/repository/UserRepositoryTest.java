package com.querydsl.example.sql.repository;

import static org.junit.Assert.*;

import com.querydsl.example.sql.model.Tweet;
import com.querydsl.example.sql.model.Usert;
import jakarta.inject.Inject;
import java.util.List;
import org.junit.Test;

public class UserRepositoryTest extends AbstractPersistenceTest {
  @Inject private UserRepository repository;

  @Inject private TweetRepository tweetRepository;

  @Test
  public void save_and_get_by_id() {
    String username = "jackie";
    Usert user = new Usert();
    user.setUsername(username);
    Long id = repository.save(user);
    assertEquals(username, repository.findById(id).getUsername());
  }

  @Test
  public void get_all() {
    Usert user = new Usert();
    user.setUsername("jimmy");
    repository.save(user);
    assertTrue(repository.all().size() == 1);
  }

  @Test
  public void get_all_with_tweet_count() {
    Usert user = new Usert();
    user.setUsername("jimmy");
    Long posterId = repository.save(user);

    Tweet tw3 = new Tweet();
    tw3.setPosterId(posterId);
    tw3.setContent("#EpicFail");
    tweetRepository.save(tw3);

    List<UserInfo> infos = repository.allWithTweetCount();
    assertFalse(infos.isEmpty());
    for (UserInfo info : infos) {
      assertNotNull(info.getUsername());
    }
  }
}
