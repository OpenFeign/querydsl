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
package com.querydsl.collections;

import static com.querydsl.core.group.GroupBy.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.group.Group;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import java.util.*;
import org.junit.Ignore;
import org.junit.Test;

public class GroupByTest {

  private static final List<User> users =
      Arrays.asList(new User("Bob"), new User("Jane"), new User("Jack"));

  private static final List<Post> posts =
      Arrays.asList(
          new Post(1, "Post 1", users.getFirst()),
          new Post(2, "Post 2", users.getFirst()),
          new Post(3, "Post 3", users.get(1)));

  private static final List<Comment> comments =
      Arrays.asList(
          new Comment(1, "Comment 1", users.getFirst(), posts.getFirst()),
          new Comment(2, "Comment 2", users.get(1), posts.get(1)),
          new Comment(3, "Comment 3", users.get(2), posts.get(1)),
          new Comment(4, "Comment 4", users.getFirst(), posts.get(2)),
          new Comment(5, "Comment 5", users.get(1), posts.get(2)),
          new Comment(6, "Comment 6", users.get(2), posts.get(2)));

  private static final QUser user = QUser.user;

  private static final QComment comment = QComment.comment;

  private static final QPost post = QPost.post;

  private static final ConstructorExpression<Comment> qComment =
      QComment.create(comment.id, comment.text);

  @Test
  public void group_min() {
    Map<Integer, String> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(min(comment.text)));

    assertThat(results).containsEntry(1, "Comment 1");
    assertThat(results).containsEntry(2, "Comment 2");
    assertThat(results).containsEntry(3, "Comment 4");
  }

  public void comments_by_post() {
    Map<Integer, List<Comment>> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(list(comment)));

    assertThat(results.get(1)).hasSize(1);
    assertThat(results.get(2)).hasSize(2);
    assertThat(results.get(3)).hasSize(3);
  }

  @Test
  public void group_max() {
    Map<Integer, String> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(max(comment.text)));

    assertThat(results).containsEntry(1, "Comment 1");
    assertThat(results).containsEntry(2, "Comment 3");
    assertThat(results).containsEntry(3, "Comment 6");
  }

  @Test
  public void group_sum() {
    Map<Integer, Integer> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(sum(comment.id)));

    assertThat(results.get(1).intValue()).isEqualTo(1);
    assertThat(results.get(2).intValue()).isEqualTo(5);
    assertThat(results.get(3).intValue()).isEqualTo(15);
  }

  @Test
  public void group_avg() {
    Map<Integer, Integer> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(avg(comment.id)));

    assertThat(results.get(1).intValue()).isEqualTo(1);
    assertThat(results.get(2).intValue()).isEqualTo(2);
    assertThat(results.get(3).intValue()).isEqualTo(5);
  }

  @Test
  public void group_order() {
    Map<Integer, Group> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(post.name, set(comment.id)));

    assertThat(results).hasSize(3);
  }

  @Test
  public void first_set_and_list() {
    Map<Integer, Group> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(post.name, set(comment.id), list(comment.text)));

    Group group = results.get(1);
    assertThat(group.getOne(post.id)).isEqualTo(toInt(1));
    assertThat(group.getOne(post.name)).isEqualTo("Post 1");
    assertThat(group.getSet(comment.id)).isEqualTo(toSet(1));
    assertThat(group.getList(comment.text)).isEqualTo(Collections.singletonList("Comment 1"));
  }

  @Test
  @Ignore
  public void group_by_null() {
    Map<Integer, Group> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(post.name, set(comment.id), list(comment.text)));

    Group group = results.get(null);
    assertThat(group.getOne(post.id)).isNull();
    assertThat(group.getOne(post.name)).isEqualTo("null post");
    assertThat(group.getSet(comment.id)).isEqualTo(toSet(7, 8));
    assertThat(group.getList(comment.text)).isEqualTo(Arrays.asList("comment 7", "comment 8"));
  }

  //    @Test(expected=NoSuchElementException.class)
  //    public void noSuchElementException() {
  //        Map<Integer, Group> results = BASIC_RESULTS.transform(
  //            groupBy(postId, postName, set(commentId), list(commentText)));
  //
  //        Group group = results.get(1);
  //        group.getSet(qComment);
  //    }

  @Test(expected = ClassCastException.class)
  public void classCastException() {
    Map<Integer, Group> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(post.name, set(comment.id), list(comment.text)));

    Group group = results.get(1);
    group.getList(comment.id);
  }

  @Test
  @Ignore
  public void map_() {
    Map<Integer, Group> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(post.name, map(comment.id, comment.text)));

    Group group = results.get(1);
    Map<Integer, String> comments = group.getMap(comment.id, comment.text);
    assertThat(comments).hasSize(1);
    //        assertEquals("comment 2", comments.get(2));
  }

  @Test
  public void array_access() {
    Map<Integer, Group> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(post.name, set(comment.id), list(comment.text)));

    Group group = results.get(1);
    Object[] array = group.toArray();
    assertThat(array[0]).isEqualTo(toInt(1));
    assertThat(array[1]).isEqualTo("Post 1");
    assertThat(array[2]).isEqualTo(toSet(1));
    assertThat(array[3]).isEqualTo(Collections.singletonList("Comment 1"));
  }

  @Test
  public void transform_results() {
    Map<Integer, Post> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(QPost.create(post.id, post.name, set(qComment))));

    Post post = results.get(1);
    assertThat(post).isNotNull();
    assertThat(post.getId()).isEqualTo(1);
    assertThat(post.getName()).isEqualTo("Post 1");
    assertThat(post.getComments()).hasSize(1);
  }

  @Test
  public void transform_as_bean() {
    Map<Integer, Post> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(
                groupBy(post.id)
                    .as(
                        Projections.bean(
                            Post.class, post.id, post.name, set(qComment).as("comments"))));

    Post post = results.get(1);
    assertThat(post).isNotNull();
    assertThat(post.getId()).isEqualTo(1);
    assertThat(post.getName()).isEqualTo("Post 1");
    assertThat(post.getComments()).hasSize(1);
  }

  @Test
  public void oneToOneToMany_projection() {
    Map<String, User> results =
        CollQueryFactory.from(user, users)
            .from(post, posts)
            .from(comment, comments)
            .where(user.name.eq(post.user.name), post.id.eq(comment.post.id))
            .transform(
                groupBy(user.name)
                    .as(
                        Projections.constructor(
                            User.class,
                            user.name,
                            QPost.create(post.id, post.name, set(qComment)))));

    assertThat(results).hasSize(2);

    User user = results.get("Jane");
    Post post = user.getLatestPost();
    assertThat(post.getId()).isEqualTo(3);
    assertThat(post.getName()).isEqualTo("Post 3");
    assertThat(post.getComments()).hasSize(3);
  }

  @Test
  public void oneToOneToMany_projection_as_bean() {
    Map<String, User> results =
        CollQueryFactory.from(user, users)
            .from(post, posts)
            .from(comment, comments)
            .where(user.name.eq(post.user.name), post.id.eq(comment.post.id))
            .transform(
                groupBy(user.name)
                    .as(
                        Projections.bean(
                            User.class,
                            user.name,
                            Projections.bean(
                                    Post.class, post.id, post.name, set(qComment).as("comments"))
                                .as("latestPost"))));

    assertThat(results).hasSize(2);

    User user = results.get("Jane");
    Post post = user.getLatestPost();
    assertThat(post.getId()).isEqualTo(3);
    assertThat(post.getName()).isEqualTo("Post 3");
    assertThat(post.getComments()).hasSize(3);
  }

  @Test
  public void oneToOneToMany_projection_as_bean_and_constructor() {
    Map<String, User> results =
        CollQueryFactory.from(user, users)
            .from(post, posts)
            .from(comment, comments)
            .where(user.name.eq(post.user.name), post.id.eq(comment.post.id))
            .transform(
                groupBy(user.name)
                    .as(
                        Projections.bean(
                            User.class,
                            user.name,
                            QPost.create(post.id, post.name, set(qComment)).as("latestPost"))));

    assertThat(results).hasSize(2);

    User user = results.get("Jane");
    Post post = user.getLatestPost();
    assertThat(post.getId()).isEqualTo(3);
    assertThat(post.getName()).isEqualTo("Post 3");
    assertThat(post.getComments()).hasSize(3);
  }

  private Integer toInt(int i) {
    return i;
  }

  private <T> Set<T> toSet(T... s) {
    return new HashSet<T>(Arrays.asList(s));
  }
}
