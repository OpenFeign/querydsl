package com.querydsl.example.sql.repository;

import static com.querydsl.example.sql.model.QTweet.tweet;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.example.sql.guice.Transactional;
import com.querydsl.example.sql.model.QUsert;
import com.querydsl.example.sql.model.Usert;
import java.util.List;

public class UserRepository extends AbstractRepository {

  private static final QUsert user = QUsert.usert;

  @Transactional
  public Usert findById(Long id) {
    return selectFrom(user).where(user.id.eq(id)).fetchOne();
  }

  @Transactional
  public Long save(Usert entity) {
    if (entity.getId() != null) {
      update(user).populate(entity).execute();
      return entity.getId();
    }
    return insert(user).populate(entity).executeWithKey(user.id);
  }

  @Transactional
  public List<UserInfo> allWithTweetCount() {
    return select(Projections.constructor(UserInfo.class, user.username, tweet.id.count()))
        .from(user)
        .leftJoin(tweet)
        .on(user.id.eq(tweet.posterId))
        .groupBy(user.username)
        .fetch();
  }

  @Transactional
  public List<Usert> findAll(Predicate expr) {
    return selectFrom(user).where(expr).fetch();
  }

  @Transactional
  public List<Usert> all() {
    return selectFrom(user).fetch();
  }
}
