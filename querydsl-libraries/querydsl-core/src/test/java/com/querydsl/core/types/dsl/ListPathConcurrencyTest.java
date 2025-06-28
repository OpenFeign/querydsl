package com.querydsl.core.types.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.domain.QCat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;

public class ListPathConcurrencyTest {

  @Test
  public void get_isThreadSafe() throws Exception {
    QCat cat = QCat.cat;
    Callable<QCat> task = () -> cat.kittens(0);

    int threads = 8;
    ExecutorService exec = Executors.newFixedThreadPool(threads);
    List<Future<QCat>> futures = new ArrayList<>();
    for (int i = 0; i < threads; i++) {
      futures.add(exec.submit(task));
    }
    Set<QCat> results = new HashSet<>();
    for (Future<QCat> future : futures) {
      results.add(future.get());
    }
    exec.shutdownNow();
    assertThat(results).hasSize(1);
  }
}