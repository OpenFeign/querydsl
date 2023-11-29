package com.querydsl.example.jpa.repository;

import static com.querydsl.example.jpa.model.QTweet.tweet;

import com.google.inject.persist.Transactional;
import com.querydsl.core.types.Predicate;
import com.querydsl.example.jpa.model.Tweet;
import java.util.List;

@Transactional
public class TweetRepository extends AbstractRepository<Tweet> {

  public Tweet save(Tweet tweet) {
    return persistOrMerge(tweet);
  }

  @Override
  public Tweet findById(Long id) {
    return find(Tweet.class, id);
  }

  public List<Tweet> findAll(Predicate expr) {
    return selectFrom(tweet).where(expr).fetch();
  }

  public List<Tweet> findAllWithHibernateQuery(Predicate expr) {
    return selectFromHibernateQuery(tweet).where(expr).fetch();
  }
}
