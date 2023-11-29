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
import static org.junit.Assert.*;

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
          new Post(1, "Post 1", users.get(0)),
          new Post(2, "Post 2", users.get(0)),
          new Post(3, "Post 3", users.get(1)));

  private static final List<Comment> comments =
      Arrays.asList(
          new Comment(1, "Comment 1", users.get(0), posts.get(0)),
          new Comment(2, "Comment 2", users.get(1), posts.get(1)),
          new Comment(3, "Comment 3", users.get(2), posts.get(1)),
          new Comment(4, "Comment 4", users.get(0), posts.get(2)),
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

    assertEquals("Comment 1", results.get(1));
    assertEquals("Comment 2", results.get(2));
    assertEquals("Comment 4", results.get(3));
  }

  public void comments_by_post() {
    Map<Integer, List<Comment>> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(list(comment)));

    assertEquals(1, results.get(1).size());
    assertEquals(2, results.get(2).size());
    assertEquals(3, results.get(3).size());
  }

  @Test
  public void group_max() {
    Map<Integer, String> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(max(comment.text)));

    assertEquals("Comment 1", results.get(1));
    assertEquals("Comment 3", results.get(2));
    assertEquals("Comment 6", results.get(3));
  }

  @Test
  public void group_sum() {
    Map<Integer, Integer> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(sum(comment.id)));

    assertEquals(1, results.get(1).intValue());
    assertEquals(5, results.get(2).intValue());
    assertEquals(15, results.get(3).intValue());
  }

  @Test
  public void group_avg() {
    Map<Integer, Integer> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(avg(comment.id)));

    assertEquals(1, results.get(1).intValue());
    assertEquals(2, results.get(2).intValue());
    assertEquals(5, results.get(3).intValue());
  }

  @Test
  public void group_order() {
    Map<Integer, Group> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(post.name, set(comment.id)));

    assertEquals(3, results.size());
  }

  @Test
  public void first_set_and_list() {
    Map<Integer, Group> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(post.name, set(comment.id), list(comment.text)));

    Group group = results.get(1);
    assertEquals(toInt(1), group.getOne(post.id));
    assertEquals("Post 1", group.getOne(post.name));
    assertEquals(toSet(1), group.getSet(comment.id));
    assertEquals(Collections.singletonList("Comment 1"), group.getList(comment.text));
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
    assertNull(group.getOne(post.id));
    assertEquals("null post", group.getOne(post.name));
    assertEquals(toSet(7, 8), group.getSet(comment.id));
    assertEquals(Arrays.asList("comment 7", "comment 8"), group.getList(comment.text));
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
    assertEquals(1, comments.size());
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
    assertEquals(toInt(1), array[0]);
    assertEquals("Post 1", array[1]);
    assertEquals(toSet(1), array[2]);
    assertEquals(Collections.singletonList("Comment 1"), array[3]);
  }

  @Test
  public void transform_results() {
    Map<Integer, Post> results =
        CollQueryFactory.from(post, posts)
            .from(comment, comments)
            .where(comment.post.id.eq(post.id))
            .transform(groupBy(post.id).as(QPost.create(post.id, post.name, set(qComment))));

    Post post = results.get(1);
    assertNotNull(post);
    assertEquals(1, post.getId());
    assertEquals("Post 1", post.getName());
    assertEquals(1, post.getComments().size());
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
    assertNotNull(post);
    assertEquals(1, post.getId());
    assertEquals("Post 1", post.getName());
    assertEquals(1, post.getComments().size());
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

    assertEquals(2, results.size());

    User user = results.get("Jane");
    Post post = user.getLatestPost();
    assertEquals(3, post.getId());
    assertEquals("Post 3", post.getName());
    assertEquals(3, post.getComments().size());
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

    assertEquals(2, results.size());

    User user = results.get("Jane");
    Post post = user.getLatestPost();
    assertEquals(3, post.getId());
    assertEquals("Post 3", post.getName());
    assertEquals(3, post.getComments().size());
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

    assertEquals(2, results.size());

    User user = results.get("Jane");
    Post post = user.getLatestPost();
    assertEquals(3, post.getId());
    assertEquals("Post 3", post.getName());
    assertEquals(3, post.getComments().size());
  }

  private Integer toInt(int i) {
    return i;
  }

  private <T> Set<T> toSet(T... s) {
    return new HashSet<T>(Arrays.asList(s));
  }
}
