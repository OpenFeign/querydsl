package com.querydsl.example.sql.repository;

import static com.querydsl.example.sql.model.QLocation.location;
import static com.querydsl.example.sql.model.QTweet.tweet;
import static com.querydsl.example.sql.model.QTweetUser.tweetUser;

import com.querydsl.core.types.Predicate;
import com.querydsl.example.sql.guice.Transactional;
import com.querydsl.example.sql.model.QUsert;
import com.querydsl.example.sql.model.Tweet;
import com.querydsl.example.sql.model.TweetUser;
import java.util.List;

public class TweetRepository extends AbstractRepository {
  private static final QUsert user = QUsert.usert;

  @Transactional
  public Long save(Tweet entity) {
    if (entity.getId() != null) {
      update(tweet).populate(entity).execute();
      return entity.getId();
    }
    return insert(tweet).populate(entity).executeWithKey(user.id);
  }

  @Transactional
  public Long save(Tweet tweet, Long... mentions) {
    var tweetId = save(tweet);
    var insert = insert(tweetUser);
    for (Long mentionsId : mentions) {
      var tu = new TweetUser();
      tu.setTweetId(tweetId);
      tu.setMentionsId(mentionsId);
      insert.populate(tu).addBatch();
    }
    insert.execute();
    return tweetId;
  }

  @Transactional
  public Tweet findById(Long id) {
    return selectFrom(tweet).where(tweet.id.eq(id)).fetchOne();
  }

  @Transactional
  public List<Tweet> findOfUser(String username) {
    return select(tweet).from(user).innerJoin(tweet).on(tweet.posterId.eq(user.id)).fetch();
  }

  @Transactional
  public List<Tweet> findWithMentioned(Long userId) {
    return selectFrom(tweet)
        .innerJoin(tweetUser)
        .on(tweet.id.eq(tweetUser.tweetId))
        .where(tweetUser.mentionsId.eq(userId))
        .fetch();
  }

  @Transactional
  public List<Tweet> findOfArea(double[] pointA, double[] pointB) {
    return selectFrom(tweet)
        .innerJoin(location)
        .on(tweet.locationId.eq(location.id))
        .where(
            location.longitude.between(pointA[0], pointB[0]),
            location.latitude.between(pointA[1], pointB[1]))
        .fetch();
  }

  @Transactional
  public List<Tweet> findAll(Predicate expr) {
    return selectFrom(tweet).where(expr).fetch();
  }
}
