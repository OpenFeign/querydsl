package com.querydsl.codegen.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import javax.tools.JavaFileManager;
import javax.tools.ToolProvider;
import org.junit.Test;

/** Functional sanity-checks for MemFileSystemRegistry. */
public class MemFileSystemRegistryFunctionalityTest {

  @Test
  public void sameFileManager_returnsIdenticalPrefix() {
    JavaFileManager jfm =
        ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null);

    String first  = MemFileSystemRegistry.DEFAULT.getUrlPrefix(jfm);
    String second = MemFileSystemRegistry.DEFAULT.getUrlPrefix(jfm);

    assertThat(first).isEqualTo(second);
  }

  @Test
  public void prefix_canRoundTripToFileSystem() throws Exception {
    JavaFileManager jfm =
        ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null);

    String prefix = MemFileSystemRegistry.DEFAULT.getUrlPrefix(jfm);
    // Construct a dummy URL within that prefix; path component can be empty.
    URL url = new URL(prefix);

    assertThat(MemFileSystemRegistry.DEFAULT.getFileSystem(url)).isSameAs(jfm);
  }
}
