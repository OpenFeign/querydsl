package com.querydsl.codegen.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.tools.JavaFileManager;
import javax.tools.ToolProvider;
import org.junit.Test;

public class MemFileSystemRegistryConcurrencyTest {

  @Test
  public void getUrlPrefix_isThreadSafe() throws Exception {
    JavaFileManager jfm =
        ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null);
    Callable<String> task = () -> MemFileSystemRegistry.DEFAULT.getUrlPrefix(jfm);

    int threads = 8;
    ExecutorService exec = Executors.newFixedThreadPool(threads);
    List<Future<String>> futures = new ArrayList<>();
    for (int i = 0; i < threads; i++) {
      futures.add(exec.submit(task));
    }
    Set<String> results = new HashSet<>();
    for (Future<String> future : futures) {
      results.add(future.get());
    }
    exec.shutdownNow();
    assertThat(results).hasSize(1);
  }
}