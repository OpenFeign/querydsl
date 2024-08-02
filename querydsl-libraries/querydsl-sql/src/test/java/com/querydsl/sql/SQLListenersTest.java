package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.sql.dml.SQLInsertBatch;
import com.querydsl.sql.dml.SQLMergeBatch;
import com.querydsl.sql.dml.SQLMergeUsingCase;
import com.querydsl.sql.dml.SQLUpdateBatch;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class SQLListenersTest {

  @Test
  public void notifyQuery() {
    var listener = (SQLListener) createMock(SQLListener.class);
    var listeners = new SQLListeners();
    listeners.add(listener);

    QueryMetadata md = new DefaultQueryMetadata();
    listener.notifyQuery(md);
    replay(listener);

    listeners.notifyQuery(md);
    verify(listener);
  }

  @Test
  public void notifyQuery_parent() {
    var listener = (SQLListener) createMock(SQLListener.class);
    var listeners = new SQLListeners(listener);

    QueryMetadata md = new DefaultQueryMetadata();
    listener.notifyQuery(md);
    replay(listener);

    listeners.notifyQuery(md);
    verify(listener);
  }

  @Test
  public void notifyQuery_detailedListener_start() {
    var sqlListenerContext = (SQLListenerContext) createMock(SQLListenerContext.class);
    var listenerParent = (SQLDetailedListener) createMock(SQLDetailedListener.class);
    var listener1 = (SQLDetailedListener) createMock(SQLDetailedListener.class);
    var listener2 = (SQLDetailedListener) createMock(SQLDetailedListener.class);

    listenerParent.start(sqlListenerContext);
    replay(listenerParent);

    listener1.start(sqlListenerContext);
    replay(listener1);

    listener2.start(sqlListenerContext);
    replay(listener2);

    var listeners = new SQLListeners(listenerParent);
    listeners.add(listener1);
    listeners.add(listener2);

    listeners.start(sqlListenerContext);
    verify(listenerParent);
    verify(listener1);
    verify(listener2);
  }

  @Test
  public void notifyQuery_detailedListener_contexSetting() {
    SQLListenerContext sqlListenerContext = new SQLListenerContextImpl(new DefaultQueryMetadata());
    SQLDetailedListener listenerParent = new AssertingDetailedListener("keyParent", "valueParent");
    SQLDetailedListener listener1 = new AssertingDetailedListener("key1", "value1");
    SQLDetailedListener listener2 = new AssertingDetailedListener("key1", "value1");

    var listeners = new SQLListeners(listenerParent);
    listeners.add(listener1);
    listeners.add(listener2);

    listeners.start(sqlListenerContext);
    listeners.preRender(sqlListenerContext);
    listeners.rendered(sqlListenerContext);
    listeners.prePrepare(sqlListenerContext);
    listeners.prepared(sqlListenerContext);
    listeners.preExecute(sqlListenerContext);
    listeners.preExecute(sqlListenerContext);
  }

  static class AssertingDetailedListener implements SQLDetailedListener {
    private final String key;
    private final Object value;

    AssertingDetailedListener(String key, Object value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public void start(SQLListenerContext context) {
      context.setData(key, value);
    }

    @Override
    public void preRender(SQLListenerContext context) {
      assertThat(this.value).isEqualTo(context.getData(key));
    }

    @Override
    public void rendered(SQLListenerContext context) {
      assertThat(this.value).isEqualTo(context.getData(key));
    }

    @Override
    public void prePrepare(SQLListenerContext context) {
      assertThat(this.value).isEqualTo(context.getData(key));
    }

    @Override
    public void prepared(SQLListenerContext context) {
      assertThat(this.value).isEqualTo(context.getData(key));
    }

    @Override
    public void preExecute(SQLListenerContext context) {
      assertThat(this.value).isEqualTo(context.getData(key));
    }

    @Override
    public void executed(SQLListenerContext context) {
      assertThat(this.value).isEqualTo(context.getData(key));
    }

    @Override
    public void exception(SQLListenerContext context) {
      assertThat(this.value).isEqualTo(context.getData(key));
    }

    @Override
    public void end(SQLListenerContext context) {
      assertThat(this.value).isEqualTo(context.getData(key));
    }

    @Override
    public void notifyQuery(QueryMetadata md) {}

    @Override
    public void notifyDelete(RelationalPath<?> entity, QueryMetadata md) {}

    @Override
    public void notifyDeletes(RelationalPath<?> entity, List<QueryMetadata> batches) {}

    @Override
    public void notifyMerge(
        RelationalPath<?> entity,
        QueryMetadata md,
        List<Path<?>> keys,
        List<Path<?>> columns,
        List<Expression<?>> values,
        SubQueryExpression<?> subQuery) {}

    @Override
    public void notifyMerges(
        RelationalPath<?> entity, QueryMetadata md, List<SQLMergeBatch> batches) {}

    @Override
    public void notifyMergeUsing(
        RelationalPath<?> entity,
        QueryMetadata md,
        SimpleExpression<?> usingExpression,
        Predicate usingOn,
        List<SQLMergeUsingCase> whens) {}

    @Override
    public void notifyInsert(
        RelationalPath<?> entity,
        QueryMetadata md,
        List<Path<?>> columns,
        List<Expression<?>> values,
        SubQueryExpression<?> subQuery) {}

    @Override
    public void notifyInserts(
        RelationalPath<?> entity, QueryMetadata md, List<SQLInsertBatch> batches) {}

    @Override
    public void notifyUpdate(
        RelationalPath<?> entity, QueryMetadata md, Map<Path<?>, Expression<?>> updates) {}

    @Override
    public void notifyUpdates(RelationalPath<?> entity, List<SQLUpdateBatch> batches) {}
  }
}
