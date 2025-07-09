package com.querydsl.core.types;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;

public class TemplateFactoryConcurrencyTest {

  @Test
  public void create_isThreadSafe() throws Exception {
    TemplateFactory factory = new TemplateFactory('\\');
    String tpl = "{0}";
    Callable<Template> task = () -> factory.create(tpl);

    int threads = 8;
    ExecutorService exec = Executors.newFixedThreadPool(threads);
    List<Future<Template>> futures = new ArrayList<>();
    for (int i = 0; i < threads; i++) {
      futures.add(exec.submit(task));
    }
    Set<Template> results = new HashSet<>();
    for (Future<Template> future : futures) {
      results.add(future.get());
    }
    exec.shutdownNow();
    assertThat(results).hasSize(1);
  }
}