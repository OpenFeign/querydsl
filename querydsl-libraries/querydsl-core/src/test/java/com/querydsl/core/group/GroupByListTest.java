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
import org.junit.Test;

public class GroupByListTest extends AbstractGroupByTest {

  @Override
  @Test
  public void group_order() {
    List<Group> results = BASIC_RESULTS.transform(groupBy(postId).list(postName, set(commentId)));

    assertThat(results).hasSize(4);
  }

  @Override
  @Test
  public void first_set_and_list() {
    List<Group> results =
        BASIC_RESULTS.transform(groupBy(postId).list(postName, set(commentId), list(commentText)));

    assertThat(results).hasSize(4);

    var group = results.get(1);
    assertThat(group.getOne(postId)).isEqualTo(toInt(1));
    assertThat(group.getOne(postName)).isEqualTo("post 1");
    assertThat(group.getSet(commentId)).isEqualTo(toSet(1, 2, 3));
    assertThat(group.getList(commentText))
        .isEqualTo(Arrays.asList("comment 1", "comment 2", "comment 3"));
  }

  @Override
  @Test
  public void group_by_null() {
    List<Group> results =
        BASIC_RESULTS.transform(groupBy(postId).list(postName, set(commentId), list(commentText)));

    assertThat(results).hasSize(4);

    var group = results.getFirst();
    assertThat(group.getOne(postId)).isNull();
    assertThat(group.getOne(postName)).isEqualTo("null post");
    assertThat(group.getSet(commentId)).isEqualTo(toSet(7, 8));
    assertThat(group.getList(commentText)).isEqualTo(Arrays.asList("comment 7", "comment 8"));
  }

  @Override
  @Test(expected = NoSuchElementException.class)
  public void noSuchElementException() {
    List<Group> results =
        BASIC_RESULTS.transform(groupBy(postId).list(postName, set(commentId), list(commentText)));

    assertThat(results).hasSize(4);

    var group = results.get(1);
    group.getSet(qComment);
  }

  @Override
  @Test(expected = ClassCastException.class)
  public void classCastException() {
    List<Group> results =
        BASIC_RESULTS.transform(groupBy(postId).list(postName, set(commentId), list(commentText)));

    assertThat(results).hasSize(4);

    var group = results.get(1);
    group.getList(commentId);
  }

  @Override
  @Test
  public void map1() {
    List<Group> results =
        MAP_RESULTS.transform(groupBy(postId).list(postName, map(commentId, commentText)));

    assertThat(results).hasSize(4);

    var group = results.get(1);
    Map<Integer, String> comments = group.getMap(commentId, commentText);
    assertThat(comments).hasSize(3).containsEntry(2, "comment 2");
  }

  @Override
  @Test
  public void map2() {
    List<Map<Integer, String>> results =
        MAP2_RESULTS.transform(groupBy(postId).list(map(commentId, commentText)));

    assertThat(results).hasSize(4);

    var comments = results.get(1);
    assertThat(comments).hasSize(3).containsEntry(2, "comment 2");
  }

  @Override
  @Test
  public void map3() {
    List<Map<Integer, Map<Integer, String>>> actual =
        MAP3_RESULTS.transform(groupBy(postId).list(map(postId, map(commentId, commentText))));

    Object postId = null;
    Map<Integer, Map<Integer, String>> posts = null;
    var expected = new LinkedList<Map<Integer, Map<Integer, String>>>();
    for (Iterator<Tuple> iterator = MAP3_RESULTS.iterate(); iterator.hasNext(); ) {
      var tuple = iterator.next();
      var array = tuple.toArray();

      if (posts == null || !(postId == array[0] || postId != null && postId.equals(array[0]))) {
        posts = findPostsById(expected, array[0]);
        if (posts == null) {
          posts = new LinkedHashMap<Integer, Map<Integer, String>>();
          expected.add(posts);
        }
      }
      postId = array[0];
      @SuppressWarnings("unchecked")
      var pair = (Pair<Integer, Pair<Integer, String>>) array[1];
      var first = pair.getFirst();
      var comments = posts.get(first);
      if (comments == null) {
        comments = new LinkedHashMap<Integer, String>();
        posts.put(first, comments);
      }
      var second = pair.getSecond();
      comments.put(second.getFirst(), second.getSecond());
    }
    assertThat(actual).hasToString(expected.toString());
  }

  private Map<Integer, Map<Integer, String>> findPostsById(
      List<Map<Integer, Map<Integer, String>>> allPosts, Object postId) {
    for (var posts : allPosts) {
      if (posts.containsKey(postId)) {
        return posts;
      }
    }
    return null;
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
    List<Group> results =
        BASIC_RESULTS.transform(groupBy(postId).list(postName, set(commentId), list(commentText)));

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
    List<Post> results =
        POST_W_COMMENTS.transform(
            groupBy(postId)
                .list(Projections.constructor(Post.class, postId, postName, set(qComment))));

    assertThat(results).hasSize(4);

    var post = results.get(1);
    assertThat(post).isNotNull();
    assertThat(post.getId()).isEqualTo(toInt(1));
    assertThat(post.getName()).isEqualTo("post 1");
    assertThat(post.getComments()).isEqualTo(toSet(comment(1), comment(2), comment(3)));
  }

  @Override
  @Test
  public void transform_as_bean() {
    List<Post> results =
        POST_W_COMMENTS.transform(
            groupBy(postId)
                .list(
                    Projections.bean(Post.class, postId, postName, set(qComment).as("comments"))));

    assertThat(results).hasSize(4);

    var post = results.get(1);
    assertThat(post).isNotNull();
    assertThat(post.getId()).isEqualTo(toInt(1));
    assertThat(post.getName()).isEqualTo("post 1");
    assertThat(post.getComments()).isEqualTo(toSet(comment(1), comment(2), comment(3)));
  }

  @Override
  @Test
  public void oneToOneToMany_projection() {
    List<User> results =
        USERS_W_LATEST_POST_AND_COMMENTS.transform(
            groupBy(userName)
                .list(
                    Projections.constructor(
                        User.class,
                        userName,
                        Projections.constructor(Post.class, postId, postName, set(qComment)))));

    assertThat(results).hasSize(2);

    var user = results.getFirst();
    var post = user.getLatestPost();
    assertThat(post.getId()).isEqualTo(toInt(2));
    assertThat(post.getName()).isEqualTo("post 2");
    assertThat(post.getComments()).isEqualTo(toSet(comment(4), comment(5)));
  }

  @Override
  @Test
  public void oneToOneToMany_projection_as_bean() {
    List<User> results =
        USERS_W_LATEST_POST_AND_COMMENTS.transform(
            groupBy(userName)
                .list(
                    Projections.bean(
                        User.class,
                        userName,
                        Projections.bean(Post.class, postId, postName, set(qComment).as("comments"))
                            .as("latestPost"))));

    assertThat(results).hasSize(2);

    var user = results.getFirst();
    var post = user.getLatestPost();
    assertThat(post.getId()).isEqualTo(toInt(2));
    assertThat(post.getName()).isEqualTo("post 2");
    assertThat(post.getComments()).isEqualTo(toSet(comment(4), comment(5)));
  }

  @Override
  @Test
  public void oneToOneToMany_projection_as_bean_and_constructor() {
    List<User> results =
        USERS_W_LATEST_POST_AND_COMMENTS.transform(
            groupBy(userName)
                .list(
                    Projections.bean(
                        User.class,
                        userName,
                        Projections.constructor(Post.class, postId, postName, set(qComment))
                            .as("latestPost"))));

    assertThat(results).hasSize(2);

    var user = results.getFirst();
    var post = user.getLatestPost();
    assertThat(post.getId()).isEqualTo(toInt(2));
    assertThat(post.getName()).isEqualTo("post 2");
    assertThat(post.getComments()).isEqualTo(toSet(comment(4), comment(5)));
  }
}
