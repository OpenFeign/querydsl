package fluentq.collections;

import fluentq.core.annotations.QueryEntity;

@QueryEntity
public class StateHistoryOwner {

  private StateHistory stateHistory;

  protected final StateHistory getStateHistory() {
    return stateHistory;
  }
}
