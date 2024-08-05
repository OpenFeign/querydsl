package com.querydsl.mongodb.domain;

import com.querydsl.core.annotations.QuerySupertype;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

@QuerySupertype
public abstract class AbstractEntity {

  @Id private ObjectId id;

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  @Override
  public int hashCode() {
    final var prime = 31;
    var result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    var other = (AbstractEntity) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }

    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }
}
