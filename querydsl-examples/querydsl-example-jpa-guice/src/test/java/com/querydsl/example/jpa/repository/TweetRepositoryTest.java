package com.querydsl.example.jpa.repository;

import static com.querydsl.example.jpa.model.QTweet.tweet;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.example.jpa.model.Tweet;
import com.querydsl.example.jpa.model.User;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class TweetRepositoryTest extends AbstractPersistenceTest {
  @Inject private TweetRepository repository;

  @Inject private UserRepository userRepository;

  @Test
  public void save_and_find_by_id() {
    User poster = new User("dr_frank");
    userRepository.save(poster);

    String content = "I am alive! #YOLO";
    Tweet tweet = new Tweet(poster, content, Collections.<User>emptyList(), null);
    repository.save(tweet);
    assertThat(repository.findById(tweet.getId()).getContent()).isEqualTo(content);
  }

  @Test
  public void find_list_by_predicate() {
    User poster = new User("dr_frank");
    userRepository.save(poster);

    repository.save(new Tweet(poster, "It is a alive! #YOLO", Collections.<User>emptyList(), null));
    repository.save(new Tweet(poster, "Oh the humanity!", Collections.<User>emptyList(), null));
    repository.save(new Tweet(poster, "#EpicFail", Collections.<User>emptyList(), null));
    assertThat(repository.findAll(tweet.content.contains("#YOLO"))).hasSize(1);
  }

  @Test
  public void find_list_by_predicate_with_hibernate() {
    User poster = new User("dr_frank");
    userRepository.save(poster);

    repository.save(new Tweet(poster, "It is a alive! #YOLO", Collections.<User>emptyList(), null));
    repository.save(new Tweet(poster, "Oh the humanity!", Collections.<User>emptyList(), null));
    repository.save(new Tweet(poster, "#EpicFail", Collections.<User>emptyList(), null));
    assertThat(repository.findAllWithHibernateQuery(tweet.content.contains("#YOLO"))).hasSize(1);
  }

  @Test
  public void find_list_by_complex_predicate() {
    List<String> usernames = Arrays.asList("dr_frank", "mike", "maggie", "liza");
    List<User> users = new ArrayList<>();
    for (String username : usernames) {
      users.add(userRepository.save(new User(username)));
    }
    User poster = new User("duplo");
    userRepository.save(poster);
    for (int i = 0; i < 100; i++) {
      repository.save(new Tweet(poster, "spamming @dr_frank " + i, users.subList(0, 1), null));
    }
    assertThat(repository.findAll(tweet.mentions.contains(users.get(1)))).isEmpty();

    assertThat(repository.findAll(tweet.mentions.contains(users.getFirst()))).hasSize(100);

    assertThat(repository.findAll(tweet.mentions.any().username.eq("duplo"))).isEmpty();

    assertThat(repository.findAll(tweet.mentions.any().username.eq("dr_frank"))).hasSize(100);
  }

  @Test
  public void find_list_by_complex_predicate_hibernate() {
    List<String> usernames = Arrays.asList("dr_frank", "mike", "maggie", "liza");
    List<User> users = new ArrayList<>();
    for (String username : usernames) {
      users.add(userRepository.save(new User(username)));
    }
    User poster = new User("duplo");
    userRepository.save(poster);
    for (int i = 0; i < 100; i++) {
      repository.save(new Tweet(poster, "spamming @dr_frank " + i, users.subList(0, 1), null));
    }
    assertThat(repository.findAllWithHibernateQuery(tweet.mentions.contains(users.get(1))))
        .isEmpty();

    assertThat(repository.findAllWithHibernateQuery(tweet.mentions.contains(users.getFirst())))
        .hasSize(100);

    assertThat(repository.findAllWithHibernateQuery(tweet.mentions.any().username.eq("duplo")))
        .isEmpty();

    assertThat(repository.findAllWithHibernateQuery(tweet.mentions.any().username.eq("dr_frank")))
        .hasSize(100);
  }
}
