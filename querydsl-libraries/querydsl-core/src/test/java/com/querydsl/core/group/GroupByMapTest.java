/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.core.group;

import static com.querydsl.core.group.GroupBy.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import com.mysema.commons.lang.Pair;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import org.junit.Test;

public class GroupByMapTest extends AbstractGroupByTest {

  @Test
  public void compile() {
    StringExpression str = Expressions.stringPath("str");
    GroupExpression<String, String> strGroup = new GOne<String>(str);
    GroupBy.sortedMap(strGroup, str, null);
    GroupBy.sortedMap(str, strGroup, null);
  }

  @Test
  public void group_order() {
    Map<Integer, Group> results =
        BASIC_RESULTS.transform(groupBy(postId).as(postName, set(commentId)));
    assertThat(results).hasSize(4);
  }

  @Test
  public void set_by_sorted() {
    Map<Integer, Group> results =
        BASIC_RESULTS_UNORDERED.transform(groupBy(postId).as(postName, sortedSet(commentId)));

    Group group = results.get(1);
    Iterator<Integer> it = group.getSet(commentId).iterator();
    assertThat(it.next().intValue()).isEqualTo(1);
    assertThat(it.next().intValue()).isEqualTo(2);
    assertThat(it.next().intValue()).isEqualTo(3);
  }

  @Test
  public void set_by_sorted_reverse() {
    Map<Integer, Group> results =
        BASIC_RESULTS_UNORDERED.transform(
            groupBy(postId).as(postName, sortedSet(commentId, Comparator.reverseOrder())));

    Group group = results.get(1);
    Iterator<Integer> it = group.getSet(commentId).iterator();
    assertThat(it.next().intValue()).isEqualTo(3);
    assertThat(it.next().intValue()).isEqualTo(2);
    assertThat(it.next().intValue()).isEqualTo(1);
  }

  @Test
  public void first_set_and_list() {
    Map<Integer, Group> results =
        BASIC_RESULTS.transform(groupBy(postId).as(postName, set(commentId), list(commentText)));

    Group group = results.get(1);
    assertThat(group.getOne(postId)).isEqualTo(toInt(1));
    assertThat(group.getOne(postName)).isEqualTo("post 1");
    assertThat(group.getSet(commentId)).isEqualTo(toSet(1, 2, 3));
    assertThat(group.getList(commentText))
        .isEqualTo(Arrays.asList("comment 1", "comment 2", "comment 3"));
  }

  @Test
  public void group_by_null() {
    Map<Integer, Group> results =
        BASIC_RESULTS.transform(groupBy(postId).as(postName, set(commentId), list(commentText)));

    Group group = results.get(null);
    assertThat(group.getOne(postId)).isNull();
    assertThat(group.getOne(postName)).isEqualTo("null post");
    assertThat(group.getSet(commentId)).isEqualTo(toSet(7, 8));
    assertThat(group.getList(commentText)).isEqualTo(Arrays.asList("comment 7", "comment 8"));
  }

  @Test(expected = NoSuchElementException.class)
  public void noSuchElementException() {
    Map<Integer, Group> results =
        BASIC_RESULTS.transform(groupBy(postId).as(postName, set(commentId), list(commentText)));

    Group group = results.get(1);
    group.getSet(qComment);
  }

  @Test(expected = ClassCastException.class)
  public void classCastException() {
    Map<Integer, Group> results =
        BASIC_RESULTS.transform(groupBy(postId).as(postName, set(commentId), list(commentText)));

    Group group = results.get(1);
    group.getList(commentId);
  }

  @Test
  public void map1() {
    Map<Integer, Group> results =
        MAP_RESULTS.transform(groupBy(postId).as(postName, map(commentId, commentText)));

    Group group = results.get(1);

    Map<Integer, String> comments = group.getMap(commentId, commentText);
    assertThat(comments).hasSize(3);
    assertThat(comments).containsEntry(2, "comment 2");
  }

  @Test
  public void map_sorted() {
    Map<Integer, Group> results =
        MAP_RESULTS.transform(groupBy(postId).as(postName, sortedMap(commentId, commentText)));

    Group group = results.get(1);

    Iterator<Map.Entry<Integer, String>> it =
        group.getMap(commentId, commentText).entrySet().iterator();
    assertThat(it.next().getKey().intValue()).isEqualTo(1);
    assertThat(it.next().getKey().intValue()).isEqualTo(2);
    assertThat(it.next().getKey().intValue()).isEqualTo(3);
  }

  @Test
  public void map_sorted_reverse() {
    Map<Integer, Group> results =
        MAP_RESULTS.transform(
            groupBy(postId)
                .as(postName, sortedMap(commentId, commentText, Comparator.reverseOrder())));

    Group group = results.get(1);

    Iterator<Map.Entry<Integer, String>> it =
        group.getMap(commentId, commentText).entrySet().iterator();
    assertThat(it.next().getKey().intValue()).isEqualTo(3);
    assertThat(it.next().getKey().intValue()).isEqualTo(2);
    assertThat(it.next().getKey().intValue()).isEqualTo(1);
  }

  @Test
  public void map2() {
    Map<Integer, Map<Integer, String>> results =
        MAP2_RESULTS.transform(groupBy(postId).as(map(commentId, commentText)));

    Map<Integer, String> comments = results.get(1);
    assertThat(comments).hasSize(3);
    assertThat(comments).containsEntry(2, "comment 2");
  }

  @Test
  public void map3() {
    Map<Integer, Map<Integer, Map<Integer, String>>> actual =
        MAP3_RESULTS.transform(groupBy(postId).as(map(postId, map(commentId, commentText))));

    Map<Integer, Map<Integer, Map<Integer, String>>> expected =
        new LinkedHashMap<Integer, Map<Integer, Map<Integer, String>>>();
    for (Iterator<Tuple> iterator = MAP3_RESULTS.iterate(); iterator.hasNext(); ) {
      Tuple tuple = iterator.next();
      Object[] array = tuple.toArray();

      Map<Integer, Map<Integer, String>> posts = expected.get(array[0]);
      if (posts == null) {
        posts = new LinkedHashMap<Integer, Map<Integer, String>>();
        expected.put((Integer) array[0], posts);
      }
      @SuppressWarnings("unchecked")
      Pair<Integer, Pair<Integer, String>> pair = (Pair<Integer, Pair<Integer, String>>) array[1];
      Integer first = pair.getFirst();
      Map<Integer, String> comments =
          posts.computeIfAbsent(first, k -> new LinkedHashMap<Integer, String>());
      Pair<Integer, String> second = pair.getSecond();
      comments.put(second.getFirst(), second.getSecond());
    }
    assertThat(actual).hasToString(expected.toString());
  }

  @Test
  public void map4() {
    Map<Integer, Map<Map<Integer, String>, String>> actual =
        MAP4_RESULTS.transform(groupBy(postId).as(map(map(postId, commentText), postName)));

    Map<Integer, Map<Map<Integer, String>, String>> expected =
        new LinkedHashMap<Integer, Map<Map<Integer, String>, String>>();
    for (Iterator<Tuple> iterator = MAP4_RESULTS.iterate(); iterator.hasNext(); ) {
      Tuple tuple = iterator.next();
      Object[] array = tuple.toArray();

      Map<Map<Integer, String>, String> comments = expected.get(array[0]);
      if (comments == null) {
        comments = new LinkedHashMap<Map<Integer, String>, String>();
        expected.put((Integer) array[0], comments);
      }
      @SuppressWarnings("unchecked")
      Pair<Pair<Integer, String>, String> pair = (Pair<Pair<Integer, String>, String>) array[1];
      Pair<Integer, String> first = pair.getFirst();
      Map<Integer, String> posts = Collections.singletonMap(first.getFirst(), first.getSecond());
      comments.put(posts, pair.getSecond());
    }
    assertThat(actual).hasToString(expected.toString());
  }

  @Test
  public void array_access() {
    Map<Integer, Group> results =
        BASIC_RESULTS.transform(groupBy(postId).as(postName, set(commentId), list(commentText)));

    Group group = results.get(1);
    Object[] array = group.toArray();
    assertThat(array[0]).isEqualTo(toInt(1));
    assertThat(array[1]).isEqualTo("post 1");
    assertThat(array[2]).isEqualTo(toSet(1, 2, 3));
    assertThat(array[3]).isEqualTo(Arrays.asList("comment 1", "comment 2", "comment 3"));
  }

  @Test
  public void transform_results() {
    Map<Integer, Post> results =
        POST_W_COMMENTS.transform(
            groupBy(postId)
                .as(Projections.constructor(Post.class, postId, postName, set(qComment))));

    Post post = results.get(1);
    assertThat(post).isNotNull();
    assertThat(post.getId()).isEqualTo(toInt(1));
    assertThat(post.getName()).isEqualTo("post 1");
    assertThat(post.getComments()).isEqualTo(toSet(comment(1), comment(2), comment(3)));
  }

  @Test
  public void transform_via_groupByProjection() {
    Map<Integer, Post> results =
        POST_W_COMMENTS2.transform(
            new GroupByProjection<Integer, Post>(postId, postName, set(qComment)) {
              @Override
              protected Post transform(Group group) {
                return new Post(
                    group.getOne(postId), group.getOne(postName), group.getSet(qComment));
              }
            });

    Post post = results.get(1);
    assertThat(post).isNotNull();
    assertThat(post.getId()).isEqualTo(toInt(1));
    assertThat(post.getName()).isEqualTo("post 1");
    assertThat(post.getComments()).isEqualTo(toSet(comment(1), comment(2), comment(3)));
  }

  @Test
  public void transform_as_bean() {
    Map<Integer, Post> results =
        POST_W_COMMENTS.transform(
            groupBy(postId)
                .as(Projections.bean(Post.class, postId, postName, set(qComment).as("comments"))));

    Post post = results.get(1);
    assertThat(post).isNotNull();
    assertThat(post.getId()).isEqualTo(toInt(1));
    assertThat(post.getName()).isEqualTo("post 1");
    assertThat(post.getComments()).isEqualTo(toSet(comment(1), comment(2), comment(3)));
  }

  @Test
  public void oneToOneToMany_projection() {
    Map<String, User> results =
        USERS_W_LATEST_POST_AND_COMMENTS.transform(
            groupBy(userName)
                .as(
                    Projections.constructor(
                        User.class,
                        userName,
                        Projections.constructor(Post.class, postId, postName, set(qComment)))));

    assertThat(results).hasSize(2);

    User user = results.get("Jane");
    Post post = user.getLatestPost();
    assertThat(post.getId()).isEqualTo(toInt(2));
    assertThat(post.getName()).isEqualTo("post 2");
    assertThat(post.getComments()).isEqualTo(toSet(comment(4), comment(5)));
  }

  @Test
  public void oneToOneToMany_projection_as_bean() {
    Map<String, User> results =
        USERS_W_LATEST_POST_AND_COMMENTS.transform(
            groupBy(userName)
                .as(
                    Projections.bean(
                        User.class,
                        userName,
                        Projections.bean(Post.class, postId, postName, set(qComment).as("comments"))
                            .as("latestPost"))));

    assertThat(results).hasSize(2);

    User user = results.get("Jane");
    Post post = user.getLatestPost();
    assertThat(post.getId()).isEqualTo(toInt(2));
    assertThat(post.getName()).isEqualTo("post 2");
    assertThat(post.getComments()).isEqualTo(toSet(comment(4), comment(5)));
  }

  @Test
  public void oneToOneToMany_projection_as_bean_and_constructor() {
    Map<String, User> results =
        USERS_W_LATEST_POST_AND_COMMENTS.transform(
            groupBy(userName)
                .as(
                    Projections.bean(
                        User.class,
                        userName,
                        Projections.constructor(Post.class, postId, postName, set(qComment))
                            .as("latestPost"))));

    assertThat(results).hasSize(2);

    User user = results.get("Jane");
    Post post = user.getLatestPost();
    assertThat(post.getId()).isEqualTo(toInt(2));
    assertThat(post.getName()).isEqualTo("post 2");
    assertThat(post.getComments()).isEqualTo(toSet(comment(4), comment(5)));
  }

  @Test
  public void signature() {
    StringPath str = Expressions.stringPath("str");
    NumberPath<BigDecimal> bigd = Expressions.numberPath(BigDecimal.class, "bigd");

    ResultTransformer<
            Map<String, SortedMap<BigDecimal, SortedMap<BigDecimal, Map<String, String>>>>>
        resultTransformer =
            GroupBy.groupBy(str)
                .as(
                    GroupBy.sortedMap(
                        bigd,
                        GroupBy.sortedMap(
                            bigd,
                            GroupBy.map(str, str),
                            Comparator.nullsLast(Comparator.naturalOrder())),
                        Comparator.nullsFirst(Comparator.naturalOrder())));
    assertThat(resultTransformer).isNotNull();
  }

  @Test
  public void average_with_default_math_context() {
    Map<Integer, Double> results = POSTS_W_COMMENTS_SCORE.transform(groupBy(postId).as(avg(score)));
    assertThat(results.get(null)).isCloseTo(1.5, within(0.0));
    assertThat(results.get(1)).isCloseTo(((1.5 + 2.0 + 0.5) / 3), within(0.0));
    assertThat(results.get(2)).isCloseTo(((1.0 + 2.0) / 2), within(0.0));
  }

  @Test
  public void average_with_user_provided_math_context() {
    MathContext oneDigitMathContext = new MathContext(2, RoundingMode.HALF_EVEN);
    Map<Integer, Double> results =
        POSTS_W_COMMENTS_SCORE.transform(groupBy(postId).as(avg(score, oneDigitMathContext)));
    assertThat(results.get(null)).isCloseTo(1.5, within(0.0));
    assertThat(results.get(1)).isCloseTo(1.3, within(0.0));
    assertThat(results.get(2)).isCloseTo(1.5, within(0.0));
  }
}
