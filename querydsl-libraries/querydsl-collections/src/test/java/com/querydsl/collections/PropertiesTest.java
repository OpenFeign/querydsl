package com.querydsl.collections;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class PropertiesTest {

  @Test
  public void hidden() {
    var history = QStateHistory.stateHistory;
    List<StateHistory> histories = Collections.singletonList(new StateHistory());
    assertThat(CollQueryFactory.from(history, histories).where(history.changedAt.isNull()).fetch())
        .hasSize(1);
  }

  @Test
  public void hidden2() {
    var historyOwner = QStateHistoryOwner.stateHistoryOwner;
    List<StateHistoryOwner> historyOwners = Collections.singletonList(new StateHistoryOwner());
    assertThat(
            CollQueryFactory.from(historyOwner, historyOwners)
                .where(historyOwner.stateHistory.isNull())
                .fetch())
        .hasSize(1);
  }
}
