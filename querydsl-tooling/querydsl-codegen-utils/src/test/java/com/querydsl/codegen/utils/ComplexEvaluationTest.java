/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.querydsl.codegen.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import com.querydsl.codegen.utils.model.Types;
import com.querydsl.codegen.utils.support.Cat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class ComplexEvaluationTest {

  private EvaluatorFactory factory = new ECJEvaluatorFactory(getClass().getClassLoader());

  @Test
  @SuppressWarnings("unchecked")
  public void Complex() {
    var resultType = new ClassType(TypeCategory.LIST, List.class, Types.STRING);
    var source = new StringBuilder();
    source.append("java.util.List<String> rv = new java.util.ArrayList<String>();\n");
    source.append("for (String a : a_){\n");
    source.append("    for (String b : b_){\n");
    source.append("        if (a.equals(b)){\n");
    source.append("            rv.add(a);\n");
    source.append("        }\n");
    source.append("    }\n");
    source.append("}\n");
    source.append("return rv;");

    @SuppressWarnings("rawtypes") // cannot specify further than List.class
    Evaluator<List> evaluator =
        factory.createEvaluator(
            source.toString(),
            resultType,
            new String[] {"a_", "b_"},
            new Type[] {resultType, resultType},
            new Class<?>[] {List.class, List.class},
            Collections.<String, Object>emptyMap());

    List<String> a = Arrays.asList("1", "2", "3", "4");
    List<String> b = Arrays.asList("2", "4", "6", "8");

    assertThat(evaluator.evaluate(a, b)).isEqualTo(Arrays.asList("2", "4"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void ComplexClassLoading() {
    var resultType = new ClassType(TypeCategory.LIST, List.class, Types.OBJECTS);
    var source = new StringBuilder();
    source.append("java.util.List<Object[]> rv = new java.util.ArrayList<Object[]>();\n");
    source.append(
        """
        for (com.querydsl.codegen.utils.support.Cat cat :\
         (java.util.List<com.querydsl.codegen.utils.support.Cat>)cat_){
        """);
    source.append(
        """
        for (com.querydsl.codegen.utils.support.Cat otherCat :\
         (java.util.List<com.querydsl.codegen.utils.support.Cat>)otherCat_){
        """);
    source.append("rv.add(new Object[]{cat,otherCat});\n");
    source.append("}\n");
    source.append("}\n");
    source.append("return rv;\n");

    var fuzzy = new Cat("fuzzy");
    var spot = new Cat("spot");
    var mittens = new Cat("mittens");
    var sparkles = new Cat("sparkles");

    List<Cat> a = Arrays.asList(fuzzy, spot);
    List<Cat> b = Arrays.asList(mittens, sparkles);

    var argType = new ClassType(TypeCategory.LIST, List.class, new ClassType(Cat.class));
    @SuppressWarnings("rawtypes") // cannot specify further than List.class
    Evaluator<List> evaluator =
        factory.createEvaluator(
            source.toString(),
            resultType,
            new String[] {"cat_", "otherCat_"},
            new Type[] {argType, argType},
            new Class<?>[] {List.class, List.class},
            Collections.<String, Object>emptyMap());

    Object[][] expResults = {
      {fuzzy, mittens}, {fuzzy, sparkles}, {spot, mittens}, {spot, sparkles}
    };
    List<Object[]> result = evaluator.evaluate(a, b);
    assertThat(result).hasSize(expResults.length);

    for (var i = 0; i < expResults.length; i++) {
      assertThat(result.get(i).length).isEqualTo(expResults[i].length);
      for (var j = 0; j < expResults[i].length; j++) {
        assertThat(result.get(i)[j]).isEqualTo(expResults[i][j]);
      }
    }
  }

  @Test(expected = CodegenException.class)
  @SuppressWarnings("unchecked")
  public void ComplexClassLoadingFailure() {
    var resultType = new ClassType(TypeCategory.LIST, List.class, Types.STRING);
    var source = new StringBuilder();
    source.append(
        """
        java.util.List<String> rv = (java.util.List<String>) new\
         java.util.ArrayList<Franklin>();
        """);
    source.append("for (String a : a_){\n");
    source.append("    for (String b : b_){\n");
    source.append("        if (a.equals(b)){\n");
    source.append("            rv.add(a);\n");
    source.append("        }\n");
    source.append("    }\n");
    source.append("}\n");
    source.append("return rv;");

    @SuppressWarnings("rawtypes") // cannot specify further than List.class
    Evaluator<List> evaluator =
        factory.createEvaluator(
            source.toString(),
            resultType,
            new String[] {"a_", "b_"},
            new Type[] {resultType, resultType},
            new Class<?>[] {List.class, List.class},
            Collections.<String, Object>emptyMap());

    List<String> a = Arrays.asList("1", "2", "3", "4");
    List<String> b = Arrays.asList("2", "4", "6", "8");

    assertThat(evaluator.evaluate(a, b)).isEqualTo(Arrays.asList("2", "4"));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void ComplexPrimitiveType() {
    var resultType = new ClassType(TypeCategory.LIST, List.class, Types.BOOLEAN);
    var source = new StringBuilder();
    source.append("java.util.List<Boolean> rv = new java.util.ArrayList<Boolean>();\n");
    source.append("for (boolean a : a_){\n");
    source.append("    for (boolean b : b_){\n");
    source.append("        if (a == b){\n");
    source.append("            rv.add(a);\n");
    source.append("        }\n");
    source.append("    }\n");
    source.append("}\n");
    source.append("return rv;");

    @SuppressWarnings("rawtypes") // cannot specify further than List.class
    Evaluator<List> evaluator =
        factory.createEvaluator(
            source.toString(),
            resultType,
            new String[] {"a_", "b_"},
            new Type[] {resultType, resultType},
            new Class<?>[] {List.class, List.class},
            Collections.<String, Object>emptyMap());

    List<Boolean> a = Arrays.asList(true, true, true);
    List<Boolean> b = Arrays.asList(false, false, true);

    assertThat(evaluator.evaluate(a, b)).isEqualTo(Arrays.asList(true, true, true));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void ComplexEmbeddedClass() {
    var resultType = new ClassType(TypeCategory.LIST, List.class, Types.BOOLEAN);
    var source = new StringBuilder();
    source.append("java.util.List<Boolean> rv = new java.util.ArrayList<Boolean>();\n");
    source.append("for (boolean a : a_){\n");
    source.append("    for (boolean b : b_){\n");
    source.append("        if (a == b && new TestEmbedded().DO_RETURN()){\n");
    source.append("            rv.add(a);\n");
    source.append("        }\n");
    source.append("    }\n");
    source.append("}\n");
    source.append(
        """
        return rv;} private static class TestEmbedded { public TestEmbedded() {} public boolean\
         DO_RETURN() { return true; } \
        """);

    @SuppressWarnings("rawtypes") // cannot specify further than List.class
    Evaluator<List> evaluator =
        factory.createEvaluator(
            source.toString(),
            resultType,
            new String[] {"a_", "b_"},
            new Type[] {resultType, resultType},
            new Class<?>[] {List.class, List.class},
            Collections.<String, Object>emptyMap());

    List<Boolean> a = Arrays.asList(true, true, true);
    List<Boolean> b = Arrays.asList(false, false, true);

    assertThat(evaluator.evaluate(a, b)).isEqualTo(Arrays.asList(true, true, true));
  }

  public static final class SuperCat extends Cat {
    private SuperCat(String name) {
      super(name);
    }
  }

  @Test
  public void ComplexDifferentConstants() {
    var resultType = new ClassType(TypeCategory.LIST, List.class, new ClassType(Cat.class));
    var source =
        new StringBuilder()
            .append(
                """
                java.util.List<com.querydsl.codegen.utils.support.Cat> rv = new\
                 java.util.ArrayList<com.querydsl.codegen.utils.support.Cat>();
                """)
            .append(
                """
                for (com.querydsl.codegen.utils.support.Cat cat :\
                 (java.util.List<com.querydsl.codegen.utils.support.Cat>)cat_){
                """)
            .append("if (cat.equals(a1)) {\n")
            .append("rv.add(cat);\n")
            .append("}\n")
            .append("}\n")
            .append("return rv;\n")
            .toString();

    List<Cat> cats = Arrays.asList(new Cat("fuzzy"), new Cat("spot"));
    var names = new String[] {"cat_"};
    var types = new Type[] {new ClassType(TypeCategory.LIST, List.class, new ClassType(Cat.class))};
    var classes = new Class<?>[] {List.class};

    // first pass
    factory
        .createEvaluator(
            source,
            resultType,
            names,
            types,
            classes,
            Collections.singletonMap("a1", (Object) new SuperCat("superMittens")))
        .evaluate(cats);

    // second pass
    factory
        .createEvaluator(
            source,
            resultType,
            names,
            types,
            classes,
            Collections.singletonMap("a1", (Object) new Cat("normalMittens")))
        .evaluate(cats);
  }
}
