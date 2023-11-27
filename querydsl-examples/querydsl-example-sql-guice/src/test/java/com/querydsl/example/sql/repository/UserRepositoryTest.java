package com.querydsl.example.sql.repository;

import static org.assertj.core.api.Assertions.assertThat;
import com.querydsl.example.sql.model.Tweet;
import com.querydsl.example.sql.model.Usert;
import java.util.List;
import javax.inject.Inject;
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
    assertThat(repository.findById(id).getUsername()).isEqualTo(username);
  }

  @Test
  public void get_all() {
    Usert user = new Usert();
    user.setUsername("jimmy");
    repository.save(user);
    assertThat(repository.all().size() == 1).isTrue();
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
    assertThat(infos).isNotEmpty();
    for (UserInfo info : infos) {
      assertThat(info.getUsername()).isNotNull();
    }
  }
}
