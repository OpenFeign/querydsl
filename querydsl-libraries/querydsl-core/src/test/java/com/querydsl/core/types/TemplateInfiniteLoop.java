package com.querydsl.core.types;

import java.util.concurrent.Executors;

public final class TemplateInfiniteLoop {

  private TemplateInfiniteLoop() {}

  static String[] templates = createTemplates();

  public static void main(String[] args) {
    var executorService = Executors.newFixedThreadPool(3);
    executorService.execute(new Runner());
    executorService.execute(new Runner());
    executorService.execute(new Runner());
    executorService.shutdown();
  }

  private static class Runner implements Runnable {
    @Override
    public void run() {
      for (var i = 0; i < 100000; i++) {
        TemplateFactory.DEFAULT.create(templates[i % templates.length]);
      }
    }
  }

  /**
   * Generates array of strings: "\0a", "\0\0a", "\0\0\0a" etc. all with the same hashCode
   *
   * @return
   */
  private static String[] createTemplates() {
    var tab = new String[10000];
    var builder = new StringBuilder();
    for (var i = 0; i < tab.length; i++) {
      builder.append('\0');
      tab[i] = builder.toString() + 'a';
    }
    return tab;
  }
}
