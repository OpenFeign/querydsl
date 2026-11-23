package fluentq.apt;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class NoteTest extends AbstractProcessorTest {

  private Collection<String> aptOptions;

  private ByteArrayOutputStream err = new ByteArrayOutputStream();

  private static final String packagePath = "src/test/java/fluentq/apt/";

  public void process() throws IOException {
    var classes = getFiles(packagePath);
    process(FluentQAnnotationProcessor.class, classes, "includedClasses");
  }

  @Override
  protected Collection<String> getAPTOptions() {
    return aptOptions;
  }

  @Override
  protected ByteArrayOutputStream getStdErr() {
    return err;
  }

  /**
   * Whether the processor emitted info-level notes (controlled by {@code fluentq.logInfo}). Cannot
   * simply check that stderr is empty: the processor also reports unrelated warnings (e.g. circular
   * Q-class references) regardless of the logInfo option.
   */
  protected boolean hasInfoNotes() {
    return getStdErr().toString().contains("Note:");
  }

  @Test
  public void processDefault() throws IOException {
    aptOptions = Collections.emptyList();
    process();
    assertThat(hasInfoNotes()).isFalse();
  }

  @Test
  public void processEnabled() throws IOException {
    aptOptions = Collections.singletonList("-Afluentq.logInfo=true");
    process();
    assertThat(hasInfoNotes()).isTrue();
  }

  @Test
  public void processDisabled() throws IOException {
    aptOptions = Collections.singletonList("-Afluentq.logInfo=false");
    process();
    assertThat(hasInfoNotes()).isFalse();
  }
}
