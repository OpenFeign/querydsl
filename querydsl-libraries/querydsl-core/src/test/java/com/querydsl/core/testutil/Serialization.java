package com.querydsl.core.testutil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class Serialization {

  private Serialization() {}

  @SuppressWarnings("unchecked")
  public static <T> T serialize(T obj) {
    try {
      // serialize
      var baos = new ByteArrayOutputStream();
      var out = new ObjectOutputStream(baos);
      out.writeObject(obj);
      out.close();

      // deserialize
      var bais = new ByteArrayInputStream(baos.toByteArray());
      var in = new ObjectInputStream(bais);
      var rv = (T) in.readObject();
      in.close();
      return rv;
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
