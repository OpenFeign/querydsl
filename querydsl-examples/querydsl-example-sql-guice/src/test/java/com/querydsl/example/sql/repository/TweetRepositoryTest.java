package com.querydsl.example.sql.repository;

import static com.querydsl.example.sql.model.QTweet.tweet;
import static org.assertj.core.api.Assertions.assertThat;
import com.querydsl.example.sql.model.Tweet;
import com.querydsl.example.sql.model.Usert;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;

public class TweetRepositoryTest extends AbstractPersistenceTest {
  @Inject private TweetRepository repository;

  @Inject private UserRepository userRepository;

  private Long posterId;

  @Before
  public void setUp() {
    Usert poster = new Usert();
    poster.setUsername("dr_frank");
    posterId = userRepository.save(poster);
  }

  @Test
  public void save_and_find_by_id() {
    String content = "I am alive! #YOLO";
    Tweet tweet = new Tweet();
    tweet.setContent(content);
    tweet.setPosterId(posterId);
    Long id = repository.save(tweet);
    assertThat(repository.findById(id).getContent()).isEqualTo(content);
  }

  @Test
  public void save_and_find_by_username() {
    String content = "I am alive! #YOLO";
    Tweet tweet = new Tweet();
    tweet.setContent(content);
    tweet.setPosterId(posterId);
    repository.save(tweet);

    assertThat(repository.findOfUser("dr_frank")).isNotEmpty();
  }

  @Test
  public void save_with_mentions() {
    Usert other = new Usert();
    other.setUsername("dexter");
    Long otherId = userRepository.save(other);

    String content = "I am alive! #YOLO";
    Tweet tweet = new Tweet();
    tweet.setContent(content);
    tweet.setPosterId(posterId);
    Long tweetId = repository.save(tweet, otherId);

    assertThat(repository.findWithMentioned(otherId).get(0).getId()).isEqualTo(tweetId);
  }

  @Test
  public void find_list_by_predicate() {
    Tweet tw1 = new Tweet();
    tw1.setPosterId(posterId);
    tw1.setContent("It is a alive! #YOLO");
    repository.save(tw1);

    Tweet tw2 = new Tweet();
    tw2.setPosterId(posterId);
    tw2.setContent("Oh the humanity!");
    repository.save(tw2);

    Tweet tw3 = new Tweet();
    tw3.setPosterId(posterId);
    tw3.setContent("#EpicFail");
    repository.save(tw3);

    assertThat(repository.findAll(tweet.content.contains("#YOLO"))).hasSize(1);
  }
}
