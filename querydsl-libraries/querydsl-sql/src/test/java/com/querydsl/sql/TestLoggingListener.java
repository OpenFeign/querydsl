package com.querydsl.sql;

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

/** */
public class TestLoggingListener implements SQLDetailedListener {
  private static boolean enabled = false;

  /** Called to enable logging in tests */
  public static void enable() {
    enabled = true;
  }

  /** Called to disable logging in tests */
  public static void disable() {
    enabled = false;
  }

  @Override
  public void start(SQLListenerContext context) {
    if (enabled) {
      System.out.println("\n\tstart %s".formatted(context));
    }
  }

  @Override
  public void preRender(SQLListenerContext context) {
    if (enabled) {
      System.out.println("\t\tpreRender %s".formatted(context));
    }
  }

  @Override
  public void rendered(SQLListenerContext context) {
    if (enabled) {
      System.out.println("\t\t\trendered %s".formatted(context));
    }
  }

  @Override
  public void prePrepare(SQLListenerContext context) {
    if (enabled) {
      System.out.println("\t\tprePrepare %s".formatted(context));
    }
  }

  @Override
  public void prepared(SQLListenerContext context) {
    if (enabled) {
      System.out.println("\t\t\tprepared %s".formatted(context));
    }
  }

  @Override
  public void preExecute(SQLListenerContext context) {
    if (enabled) {
      System.out.println("\t\tpreExecute %s".formatted(context));
    }
  }

  @Override
  public void executed(SQLListenerContext context) {
    if (enabled) {
      System.out.println("\t\t\texecuted %s".formatted(context));
    }
  }

  @Override
  public void exception(SQLListenerContext context) {
    if (enabled) {
      System.out.println("\t\texception %s".formatted(context));
    }
  }

  @Override
  public void end(SQLListenerContext context) {
    if (enabled) {
      System.out.println("\tend %s\n\n".formatted(context));
    }
  }

  @Override
  public void notifyQuery(QueryMetadata md) {
    if (enabled) {
      System.out.println("\t\t\tnotifyQuery %s".formatted(md));
    }
  }

  @Override
  public void notifyDelete(RelationalPath<?> entity, QueryMetadata md) {
    if (enabled) {
      System.out.println("\t\t\tnotifyDelete %s".formatted(entity));
    }
  }

  @Override
  public void notifyDeletes(RelationalPath<?> entity, List<QueryMetadata> batches) {
    if (enabled) {
      System.out.println("\t\t\tnotifyDeletes %s".formatted(entity));
    }
  }

  @Override
  public void notifyMerge(
      RelationalPath<?> entity,
      QueryMetadata md,
      List<Path<?>> keys,
      List<Path<?>> columns,
      List<Expression<?>> values,
      SubQueryExpression<?> subQuery) {
    if (enabled) {
      System.out.println("\t\t\tnotifyMerge %s".formatted(entity));
    }
  }

  @Override
  public void notifyMerges(
      RelationalPath<?> entity, QueryMetadata md, List<SQLMergeBatch> batches) {
    if (enabled) {
      System.out.println("\t\t\tnotifyMerges %s".formatted(entity));
    }
  }

  @Override
  public void notifyMergeUsing(
      RelationalPath<?> entity,
      QueryMetadata md,
      SimpleExpression<?> usingExpression,
      Predicate usingOn,
      List<SQLMergeUsingCase> whens) {
    if (enabled) {
      System.out.println("\t\t\tnotifyMergeUsing %s".formatted(entity));
    }
  }

  @Override
  public void notifyInsert(
      RelationalPath<?> entity,
      QueryMetadata md,
      List<Path<?>> columns,
      List<Expression<?>> values,
      SubQueryExpression<?> subQuery) {
    if (enabled) {
      System.out.println("\t\t\tnotifyInsert %s".formatted(entity));
    }
  }

  @Override
  public void notifyInserts(
      RelationalPath<?> entity, QueryMetadata md, List<SQLInsertBatch> batches) {
    if (enabled) {
      System.out.println("\t\t\tnotifyInserts %s".formatted(entity));
    }
  }

  @Override
  public void notifyUpdate(
      RelationalPath<?> entity, QueryMetadata md, Map<Path<?>, Expression<?>> updates) {
    if (enabled) {
      System.out.println("\t\t\tnotifyUpdate %s".formatted(entity));
    }
  }

  @Override
  public void notifyUpdates(RelationalPath<?> entity, List<SQLUpdateBatch> batches) {
    if (enabled) {
      System.out.println("\t\t\tnotifyUpdates %s".formatted(entity));
    }
  }
}
