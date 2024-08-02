package com.querydsl.apt;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import org.junit.Test;

public class NoteTest extends AbstractProcessorTest {

  private Collection<String> aptOptions;

  private ByteArrayOutputStream err = new ByteArrayOutputStream();

  private static final String packagePath = "src/test/java/com/querydsl/apt/";

  public void process() throws IOException {
    var classes = getFiles(packagePath);
    process(QuerydslAnnotationProcessor.class, classes, "includedClasses");
  }

  @Override
  protected Collection<String> getAPTOptions() {
    return aptOptions;
  }

  @Override
  protected ByteArrayOutputStream getStdErr() {
    return err;
  }

  protected boolean isStdErrEmpty() {
    return getStdErr().toByteArray().length == 0;
  }

  @Test
  public void processDefault() throws IOException {
    aptOptions = Collections.emptyList();
    process();
    assertThat(isStdErrEmpty()).isTrue();
  }

  @Test
  public void processEnabled() throws IOException {
    aptOptions = Collections.singletonList("-Aquerydsl.logInfo=true");
    process();
    assertThat(isStdErrEmpty()).isFalse();
  }

  @Test
  public void processDisabled() throws IOException {
    aptOptions = Collections.singletonList("-Aquerydsl.logInfo=false");
    process();
    assertThat(isStdErrEmpty()).isTrue();
  }
}
