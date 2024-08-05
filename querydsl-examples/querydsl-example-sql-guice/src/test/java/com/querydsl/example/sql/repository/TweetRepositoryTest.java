package com.querydsl.example.sql.repository;

import static com.querydsl.example.sql.model.QTweet.tweet;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.example.sql.model.Tweet;
import com.querydsl.example.sql.model.Usert;
import jakarta.inject.Inject;
import org.junit.Before;
import org.junit.Test;

public class TweetRepositoryTest extends AbstractPersistenceTest {
  @Inject private TweetRepository repository;

  @Inject private UserRepository userRepository;

  private Long posterId;

  @Before
  public void setUp() {
    var poster = new Usert();
    poster.setUsername("dr_frank");
    posterId = userRepository.save(poster);
  }

  @Test
  public void save_and_find_by_id() {
    var content = "I am alive! #YOLO";
    var tweet = new Tweet();
    tweet.setContent(content);
    tweet.setPosterId(posterId);
    var id = repository.save(tweet);
    assertThat(repository.findById(id).getContent()).isEqualTo(content);
  }

  @Test
  public void save_and_find_by_username() {
    var content = "I am alive! #YOLO";
    var tweet = new Tweet();
    tweet.setContent(content);
    tweet.setPosterId(posterId);
    repository.save(tweet);

    assertThat(repository.findOfUser("dr_frank")).isNotEmpty();
  }

  @Test
  public void save_with_mentions() {
    var other = new Usert();
    other.setUsername("dexter");
    var otherId = userRepository.save(other);

    var content = "I am alive! #YOLO";
    var tweet = new Tweet();
    tweet.setContent(content);
    tweet.setPosterId(posterId);
    var tweetId = repository.save(tweet, otherId);

    assertThat(repository.findWithMentioned(otherId).getFirst().getId()).isEqualTo(tweetId);
  }

  @Test
  public void find_list_by_predicate() {
    var tw1 = new Tweet();
    tw1.setPosterId(posterId);
    tw1.setContent("It is a alive! #YOLO");
    repository.save(tw1);

    var tw2 = new Tweet();
    tw2.setPosterId(posterId);
    tw2.setContent("Oh the humanity!");
    repository.save(tw2);

    var tw3 = new Tweet();
    tw3.setPosterId(posterId);
    tw3.setContent("#EpicFail");
    repository.save(tw3);

    assertThat(repository.findAll(tweet.content.contains("#YOLO"))).hasSize(1);
  }
}
