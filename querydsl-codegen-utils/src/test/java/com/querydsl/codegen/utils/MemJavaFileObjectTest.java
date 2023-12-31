/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.querydsl.codegen.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.Writer;
import javax.tools.JavaFileObject.Kind;
import org.junit.Test;

public class MemJavaFileObjectTest {

  @Test
  public void getCharContent() throws IOException {
    MemJavaFileObject obj = new MemJavaFileObject("mem", "Test", Kind.SOURCE);
    Writer writer = obj.openWriter();
    writer.write("Hello World");
    writer.flush();
    writer.close();
    assertThat(obj.getCharContent(true).toString()).isEqualTo("Hello World");
  }

  @Test
  public void openInputStream() throws IOException {
    MemJavaFileObject obj = new MemJavaFileObject("mem", "Test", Kind.SOURCE);
    obj.openWriter().write("test");
    obj.openInputStream().close();
  }
}
