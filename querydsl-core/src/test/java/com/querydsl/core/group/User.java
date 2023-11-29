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

/** TODO: How to project an inner group, i.e. [User] 1->1 [Post] 1->N [Comment] */
public class User {

  private String name;

  private Post latestPost;

  public User() {}

  public User(String name, Post latestPost) {
    this.name = name;
    this.latestPost = latestPost;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Post getLatestPost() {
    return latestPost;
  }

  public void setLatestPost(Post latestPost) {
    this.latestPost = latestPost;
  }
}
