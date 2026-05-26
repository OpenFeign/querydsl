/*
 * Copyright 2015, The FluentQ Team (http://www.fluentq.com/team)
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
package fluentq.mongodb.morphia;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import dev.morphia.Datastore;
import fluentq.core.types.EntityPath;
import fluentq.mongodb.AbstractMongodbQuery;

/**
 * {@code MorphiaQuery} extends {@link AbstractMongodbQuery} with Morphia specific transformations
 *
 * <p>Example
 *
 * <pre>{@code
 * QUser user = QUser.user;
 * MorphiaQuery<User> query = new MorphiaQuery<User>(morphia, datastore, user);
 * List<User> list = query
 *     .where(user.firstName.eq("Bob"))
 *     .fetch();
 * }</pre>
 *
 * @param <K> result type
 * @author laimw
 * @author tiwe
 */
public class MorphiaQuery<K> extends AbstractMongodbQuery<K, MorphiaQuery<K>> {

  private final Datastore datastore;

  public MorphiaQuery(Datastore datastore, EntityPath<K> entityPath) {
    this(datastore, entityPath.getType());
  }

  public MorphiaQuery(final Datastore datastore, final Class<? extends K> entityType) {
    super(
        datastore.getCollection(entityType),
        dbObject -> datastore.getMapper().getId(dbObject),
        new MorphiaSerializer(datastore));
    this.datastore = datastore;
  }

  @Override
  protected FindIterable<K> createCursor() {
    return super.createCursor();
  }

  @Override
  protected MongoCollection getCollection(Class<?> type) {
    return datastore.getCollection(type);
  }
}
