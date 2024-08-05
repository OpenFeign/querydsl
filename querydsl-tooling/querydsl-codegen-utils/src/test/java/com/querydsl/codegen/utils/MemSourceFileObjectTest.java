/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.querydsl.codegen.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.Test;

public class MemSourceFileObjectTest {

  @Test
  public void Simple() {
    var obj = new MemSourceFileObject("Test", "Hello World");
    assertThat(obj.getCharContent(true)).hasToString("Hello World");
  }

  @Test
  public void OpenWriter() throws IOException {
    var obj = new MemSourceFileObject("Test");
    obj.openWriter().write("Hello World");
    assertThat(obj.getCharContent(true)).hasToString("Hello World");
  }

  @Test
  public void OpenWriter2() throws IOException {
    var obj = new MemSourceFileObject("Test");
    obj.openWriter().append("Hello World");
    assertThat(obj.getCharContent(true)).hasToString("Hello World");
  }
}
