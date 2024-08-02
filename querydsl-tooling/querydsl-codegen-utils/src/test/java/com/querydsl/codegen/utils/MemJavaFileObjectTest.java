/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.querydsl.codegen.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import javax.tools.JavaFileObject.Kind;
import org.junit.Test;

public class MemJavaFileObjectTest {

  @Test
  public void getCharContent() throws IOException {
    var obj = new MemJavaFileObject("mem", "Test", Kind.SOURCE);
    var writer = obj.openWriter();
    writer.write("Hello World");
    writer.flush();
    writer.close();
    assertThat(obj.getCharContent(true)).hasToString("Hello World");
  }

  @Test
  public void openInputStream() throws IOException {
    var obj = new MemJavaFileObject("mem", "Test", Kind.SOURCE);
    obj.openWriter().write("test");
    obj.openInputStream().close();
  }
}
