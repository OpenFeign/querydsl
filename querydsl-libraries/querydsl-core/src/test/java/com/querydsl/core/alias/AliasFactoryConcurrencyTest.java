package com.querydsl.core.alias;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;

public class AliasFactoryConcurrencyTest {

  @Test
  public void createAliasForExpr_isThreadSafe() throws Exception {
    AliasFactory factory = new AliasFactory(new DefaultPathFactory(), new DefaultTypeSystem());
    Expression<Object> expr = Expressions.path(Object.class, "obj");
    Callable<Object> task = () -> factory.createAliasForExpr(Object.class, expr);

    int threads = 8;
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    List<Future<Object>> futures = new ArrayList<>();
    for (int i = 0; i < threads; i++) {
      futures.add(executor.submit(task));
    }
    Set<Object> results = new HashSet<>();
    for (Future<Object> future : futures) {
      results.add(future.get());
    }
    executor.shutdownNow();
    assertThat(results).hasSize(1);
  }
}