package com.querydsl.core.types;

import static org.assertj.core.api.Assertions.fail;

public final class TemplatesTestUtils {

  public static void testPrecedence(Templates templates) {
    var likePrecedence = templates.getPrecedence(Ops.LIKE);
    var eqPrecedence = templates.getPrecedence(Ops.EQ);
    if (templates.getPrecedence(Ops.EQ_IGNORE_CASE) != eqPrecedence) {
      fail(
          "",
          "Unexpected precedence for EQ_IGNORE_CASE "
              + templates.getPrecedence(Ops.EQ_IGNORE_CASE));
    }
    for (Operator op : Ops.values()) {
      Template template = templates.getTemplate(op);
      var str = template.toString();
      var precedence = templates.getPrecedence(op);
      if (str.contains(" like ") && precedence != likePrecedence) {
        fail("Unexpected precedence for " + op + " with template " + template);
      } else if (!str.contains("(") && !str.contains(".") && precedence < 0) {
        fail("Unexpected precedence for " + op + " with template " + template);
      } else if (str.matches(".*[<>] ?\\-?\\d")) {
        if (precedence != Templates.Precedence.COMPARISON) {
          fail("Unsafe pattern for " + op + " with template " + template);
        }
      } else if (str.matches(".*[\\+\\-] ?\\-?\\d")) {
        if (precedence != Templates.Precedence.ARITH_LOW
            && precedence != Templates.Precedence.ARITH_HIGH) {
          fail("Unsafe pattern for " + op + " with template " + template);
        }
      }
    }
  }

  private TemplatesTestUtils() {}
}
