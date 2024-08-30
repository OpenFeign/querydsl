package com.querydsl.core;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {

  @Override
  void close();

  public static <E> List<E> asList(CloseableIterator<E> iterator) {
    try (iterator) {
      return StreamSupport.stream(
              Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
          .collect(Collectors.toList());
    }
  }

  public static <E> CloseableIterator<E> of(Iterator<E> iterator) {
    return of(iterator, () -> {});
  }

  public static <E> CloseableIterator<E> of(CloseableIterator<E> iterator) {
    return of(iterator, iterator);
  }

  public static <E> CloseableIterator<E> of(Iterator<E> iterator, AutoCloseable closeable) {
    return new CloseableIterator<E>() {

      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public E next() {
        return iterator.next();
      }

      @Override
      public void close() {
        try {
          closeable.close();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  public static <E> List<E> asList(Iterator<E> iterator) {
    return asList(of(iterator));
  }
}
