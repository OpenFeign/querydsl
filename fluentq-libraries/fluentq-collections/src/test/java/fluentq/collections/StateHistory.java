package fluentq.collections;

import fluentq.core.annotations.QueryEntity;
import java.util.Date;

@QueryEntity
public class StateHistory {

  private Date changedAt;

  protected final Date getChangedAtTime() {
    return changedAt;
  }
}
