package com.querydsl.jpa;

import com.querydsl.core.Target;
import com.querydsl.core.testutil.EmptyStatement;
import java.util.Arrays;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author tiwe
 */
public class JPAProviderRule implements TestRule {

  @Override
  public Statement apply(Statement base, Description description) {
    var noEclipseLink = description.getAnnotation(NoEclipseLink.class);
    var noOpenJPA = description.getAnnotation(NoOpenJPA.class);
    var noBatooJPA = description.getAnnotation(NoBatooJPA.class);
    var noHibernate = description.getAnnotation(NoHibernate.class);
    var mode = Mode.mode.get();
    if (mode == null) {
      return base;
    } else if (noEclipseLink != null
        && applies(noEclipseLink.value())
        && mode.contains("-eclipselink")) {
      return EmptyStatement.DEFAULT;
    } else if (noOpenJPA != null && applies(noOpenJPA.value()) && mode.contains("-openjpa")) {
      return EmptyStatement.DEFAULT;
    } else if (noBatooJPA != null && applies(noBatooJPA.value()) && mode.contains("-batoo")) {
      return EmptyStatement.DEFAULT;
    } else if (noHibernate != null && applies(noHibernate.value()) && !mode.contains("-")) {
      return EmptyStatement.DEFAULT;
    } else {
      return base;
    }
  }

  private boolean applies(Target[] targets) {
    return targets.length == 0 || Arrays.asList(targets).contains(Mode.target.get());
  }
}
