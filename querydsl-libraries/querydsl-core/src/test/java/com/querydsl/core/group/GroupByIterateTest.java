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

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.group.GroupBy.map;
import static com.querydsl.core.group.GroupBy.set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.querydsl.core.CloseableIterator;
import com.querydsl.core.Pair;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class GroupByIterateTest extends AbstractGroupByTest {

  @Override
  @Test
  public void group_order() {
    CloseableIterator<Group> resultsIt =
        BASIC_RESULTS.transform(groupBy(postId).iterate(postName, set(commentId)));
    List<Group> results = CloseableIterator.asList(resultsIt);

    assertThat(results).hasSize(4);
  }

  @Override
  @Test
  public void first_set_and_list() {
    CloseableIterator<Group> resultsIt =
        BASIC_RESULTS.transform(
            groupBy(postId).iterate(postName, set(commentId), list(commentText)));
    List<Group> results = CloseableIterator.asList(resultsIt);

    assertThat(results).hasSize(4);

    var group = results.get(1);
    assertThat(group.getOne(postId)).isEqualTo(toInt(1));
    assertThat(group.getOne(postName)).isEqualTo("post 1");
    assertThat(group.getSet(commentId)).hasSameElementsAs(toSet(1, 2, 3));
    assertThat(group.getList(commentText))
        .containsExactlyElementsOf(Arrays.asList("comment 1", "comment 2", "comment 3"));
  }

  @Override
  @Test
  public void group_by_null() {
    CloseableIterator<Group> resultsIt =
        BASIC_RESULTS.transform(
            groupBy(postId).iterate(postName, set(commentId), list(commentText)));
    List<Group> results = CloseableIterator.asList(resultsIt);

    assertThat(results).hasSize(4);

    var group = results.getFirst();
    assertThat(group.getOne(postId)).isNull();
    assertThat(group.getOne(postName)).isEqualTo("null post");
    assertThat(group.getSet(commentId)).hasSameElementsAs(toSet(7, 8));
    assertThat(group.getList(commentText))
        .containsExactlyElementsOf(Arrays.asList("comment 7", "comment 8"));
  }

  @Override
  @Test
  public void noSuchElementException() {
    CloseableIterator<Group> resultsIt =
        BASIC_RESULTS.transform(
            groupBy(postId).iterate(postName, set(commentId), list(commentText)));
    List<Group> results = CloseableIterator.asList(resultsIt);
    assertThat(results).hasSize(4);
    var group = results.get(1);
    assertThatExceptionOfType(NoSuchElementException.class)
        .isThrownBy(() -> group.getSet(qComment));
  }

  @Override
  @Test
  public void classCastException() {
    CloseableIterator<Group> resultsIt =
        BASIC_RESULTS.transform(
            groupBy(postId).iterate(postName, set(commentId), list(commentText)));
    List<Group> results = CloseableIterator.asList(resultsIt);
    assertThat(results).hasSize(4);
    var group = results.get(1);
    assertThatExceptionOfType(ClassCastException.class).isThrownBy(() -> group.getList(commentId));
  }

  @Override
  @Test
  public void map1() {
    CloseableIterator<Group> resultsIt =
        MAP_RESULTS.transform(groupBy(postId).iterate(postName, map(commentId, commentText)));
    List<Group> results = CloseableIterator.asList(resultsIt);

    assertThat(results).hasSize(4);

    var group = results.get(1);
    Map<Integer, String> comments = group.getMap(commentId, commentText);
    assertThat(comments).hasSize(3).containsEntry(2, "comment 2");
  }

  @Override
  @Test
  public void map2() {
    CloseableIterator<Map<Integer, String>> resultsIt =
        MAP2_RESULTS.transform(groupBy(postId).iterate(map(commentId, commentText)));
    List<Map<Integer, String>> results = CloseableIterator.asList(resultsIt);

    assertThat(results).hasSize(4);

    var comments = results.get(1);
    assertThat(comments).hasSize(3).containsEntry(2, "comment 2");
  }

  @Test
  void map22() {
    CloseableIterator<Map<Integer, String>> results =
        MAP2_RESULTS.transform(groupBy(postId).iterate(map(commentId, commentText)));
    List<Map<Integer, String>> actual = CloseableIterator.asList(results);

    Object commentId = null;
    Map<Integer, String> comments = null;
    List<Map<Integer, String>> expected = new LinkedList<>();
    for (Iterator<Tuple> iterator = MAP2_RESULTS.iterate(); iterator.hasNext(); ) {
      var tuple = iterator.next();
      var array = tuple.toArray();

      if (comments == null
          || !(commentId == array[0] || commentId != null && commentId.equals(array[0]))) {
        comments = new LinkedHashMap<>();
        expected.add(comments);
      }
      commentId = array[0];
      @SuppressWarnings("unchecked")
      var pair = (Pair<Integer, String>) array[1];

      comments.put(pair.getFirst(), pair.getSecond());
    }
    assertThat(actual).hasToString(expected.toString());
  }

  @Override
  @Test
  public void map3() {
    CloseableIterator<Map<Integer, Map<Integer, String>>> results =
        MAP3_RESULTS.transform(groupBy(postId).iterate(map(postId, map(commentId, commentText))));
    List<Map<Integer, Map<Integer, String>>> actual = CloseableIterator.asList(results);

    Object postId = null;
    Map<Integer, Map<Integer, String>> posts = null;
    List<Map<Integer, Map<Integer, String>>> expected = new LinkedList<>();
    for (Iterator<Tuple> iterator = MAP3_RESULTS.iterate(); iterator.hasNext(); ) {
      var tuple = iterator.next();
      var array = tuple.toArray();

      if (posts == null || !(postId == array[0] || postId != null && postId.equals(array[0]))) {
        posts = new LinkedHashMap<>();
        expected.add(posts);
      }
      postId = array[0];
      @SuppressWarnings("unchecked")
      var pair = (Pair<Integer, Pair<Integer, String>>) array[1];
      var first = pair.getFirst();
      var comments = posts.computeIfAbsent(first, key -> new LinkedHashMap<Integer, String>());
      var second = pair.getSecond();
      comments.put(second.getFirst(), second.getSecond());
    }
    assertThat(actual).hasToString(expected.toString());
  }

  @Override
  @Test
  public void map4() {
    CloseableIterator<Map<Map<Integer, String>, String>> results =
        MAP4_RESULTS.transform(groupBy(postId).iterate(map(map(postId, commentText), postName)));
    List<Map<Map<Integer, String>, String>> actual = CloseableIterator.asList(results);

    Object commentId = null;
    Map<Map<Integer, String>, String> comments = null;
    List<Map<Map<Integer, String>, String>> expected = new LinkedList<>();
    for (Iterator<Tuple> iterator = MAP4_RESULTS.iterate(); iterator.hasNext(); ) {
      var tuple = iterator.next();
      var array = tuple.toArray();

      if (comments == null
          || !(commentId == array[0] || commentId != null && commentId.equals(array[0]))) {
        comments = new LinkedHashMap<>();
        expected.add(comments);
      }
      commentId = array[0];
      @SuppressWarnings("unchecked")
      var pair = (Pair<Pair<Integer, String>, String>) array[1];
      var first = pair.getFirst();
      Map<Integer, String> posts = Collections.singletonMap(first.getFirst(), first.getSecond());
      comments.put(posts, pair.getSecond());
    }
    assertThat(actual).hasToString(expected.toString());
  }

  @Override
  @Test
  public void array_access() {
    CloseableIterator<Group> resultsIt =
        BASIC_RESULTS.transform(
            groupBy(postId).iterate(postName, set(commentId), list(commentText)));
    List<Group> results = CloseableIterator.asList(resultsIt);

    assertThat(results).hasSize(4);

    var group = results.get(1);
    var array = group.toArray();
    assertThat(array[0]).isEqualTo(toInt(1));
    assertThat(array[1]).isEqualTo("post 1");
    assertThat(array[2]).isEqualTo(toSet(1, 2, 3));
    assertThat(array[3]).isEqualTo(Arrays.asList("comment 1", "comment 2", "comment 3"));
  }

  @Override
  @Test
  public void transform_results() {
    CloseableIterator<Post> resultsIt =
        POST_W_COMMENTS.transform(
            groupBy(postId)
                .iterate(Projections.constructor(Post.class, postId, postName, set(qComment))));
    List<Post> results = CloseableIterator.asList(resultsIt);

    assertThat(results).hasSize(4);

    var post = results.get(1);
    assertThat(post).isNotNull();
    assertThat(post.getId()).isEqualTo(toInt(1));
    assertThat(post.getName()).isEqualTo("post 1");
    assertThat(post.getComments()).hasSameElementsAs(toSet(comment(1), comment(2), comment(3)));
  }

  @Override
  @Test
  public void transform_as_bean() {
    CloseableIterator<Post> resultsIt =
        POST_W_COMMENTS.transform(
            groupBy(postId)
                .iterate(
                    Projections.bean(Post.class, postId, postName, set(qComment).as("comments"))));
    List<Post> results = CloseableIterator.asList(resultsIt);

    assertThat(results).hasSize(4);

    var post = results.get(1);
    assertThat(post).isNotNull();
    assertThat(post.getId()).isEqualTo(toInt(1));
    assertThat(post.getName()).isEqualTo("post 1");
    assertThat(post.getComments()).hasSameElementsAs(toSet(comment(1), comment(2), comment(3)));
  }

  @Override
  @Test
  public void oneToOneToMany_projection() {
    CloseableIterator<User> resultsIt =
        USERS_W_LATEST_POST_AND_COMMENTS.transform(
            groupBy(userName)
                .iterate(
                    Projections.constructor(
                        User.class,
                        userName,
                        Projections.constructor(Post.class, postId, postName, set(qComment)))));
    List<User> results = CloseableIterator.asList(resultsIt);

    assertThat(results).hasSize(2);

    var user = results.getFirst();
    var post = user.getLatestPost();
    assertThat(post.getId()).isEqualTo(toInt(2));
    assertThat(post.getName()).isEqualTo("post 2");
    assertThat(post.getComments()).hasSameElementsAs(toSet(comment(4), comment(5)));
  }

  @Override
  @Test
  public void oneToOneToMany_projection_as_bean() {
    CloseableIterator<User> resultsIt =
        USERS_W_LATEST_POST_AND_COMMENTS.transform(
            groupBy(userName)
                .iterate(
                    Projections.bean(
                        User.class,
                        userName,
                        Projections.bean(Post.class, postId, postName, set(qComment).as("comments"))
                            .as("latestPost"))));
    List<User> results = CloseableIterator.asList(resultsIt);

    assertThat(results).hasSize(2);

    var user = results.getFirst();
    var post = user.getLatestPost();
    assertThat(post.getId()).isEqualTo(toInt(2));
    assertThat(post.getName()).isEqualTo("post 2");
    assertThat(post.getComments()).hasSameElementsAs(toSet(comment(4), comment(5)));
  }

  @Override
  @Test
  public void oneToOneToMany_projection_as_bean_and_constructor() {
    CloseableIterator<User> resultsIt =
        USERS_W_LATEST_POST_AND_COMMENTS.transform(
            groupBy(userName)
                .iterate(
                    Projections.bean(
                        User.class,
                        userName,
                        Projections.constructor(Post.class, postId, postName, set(qComment))
                            .as("latestPost"))));
    List<User> results = CloseableIterator.asList(resultsIt);

    assertThat(results).hasSize(2);

    var user = results.getFirst();
    var post = user.getLatestPost();
    assertThat(post.getId()).isEqualTo(toInt(2));
    assertThat(post.getName()).isEqualTo("post 2");
    assertThat(post.getComments()).hasSameElementsAs(toSet(comment(4), comment(5)));
  }
}
