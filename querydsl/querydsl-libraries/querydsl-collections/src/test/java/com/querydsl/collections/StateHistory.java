package com.querydsl.collections;

import com.querydsl.core.annotations.QueryEntity;
import java.util.Date;

@QueryEntity
public class StateHistory {

  private Date changedAt;

  protected final Date getChangedAtTime() {
    return changedAt;
  }
}
