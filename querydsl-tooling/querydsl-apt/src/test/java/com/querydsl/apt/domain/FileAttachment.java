package com.querydsl.apt.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.io.File;

@Embeddable
public class FileAttachment {

  @Transient Object model;
  @Transient String name;
  @Transient File f;
  public String filename;

  public FileAttachment() {}

  FileAttachment(Object model, String name) {
    this.model = model;
    this.name = name;
  }

  public File get() {
    return f;
  }

  public void set(File file) {
    f = file;
  }

  public boolean isSet() {
    return f != null || get() != null;
  }

  public static File getStore() {
    return null;
  }

  public boolean exists() {
    return isSet();
  }

  public long length() {
    return get().length();
  }
}
