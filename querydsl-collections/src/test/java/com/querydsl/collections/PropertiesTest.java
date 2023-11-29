package com.querydsl.collections;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class PropertiesTest {

  @Test
  public void hidden() {
    QStateHistory history = QStateHistory.stateHistory;
    List<StateHistory> histories = Collections.singletonList(new StateHistory());
    assertEquals(
        1,
        CollQueryFactory.from(history, histories).where(history.changedAt.isNull()).fetch().size());
  }

  @Test
  public void hidden2() {
    QStateHistoryOwner historyOwner = QStateHistoryOwner.stateHistoryOwner;
    List<StateHistoryOwner> historyOwners = Collections.singletonList(new StateHistoryOwner());
    assertEquals(
        1,
        CollQueryFactory.from(historyOwner, historyOwners)
            .where(historyOwner.stateHistory.isNull())
            .fetch()
            .size());
  }
}
