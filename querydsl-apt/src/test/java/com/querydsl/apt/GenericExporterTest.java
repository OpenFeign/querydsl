package com.querydsl.apt;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.fail;

import com.querydsl.apt.domain.AbstractEntityTest;
import com.querydsl.apt.domain.CustomCollection;
import com.querydsl.apt.domain.Generic2Test;
import com.querydsl.apt.hibernate.HibernateAnnotationProcessor;
import com.querydsl.codegen.GenericExporter;
import com.querydsl.codegen.Keywords;
import com.querydsl.codegen.PropertyHandling;
import com.querydsl.core.domain.A;
import jakarta.persistence.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class GenericExporterTest extends AbstractProcessorTest {

  private static final String PACKAGE_PATH = "src/test/java/com/querydsl/apt/domain/";

  private static final List<String> CLASSES = getFiles(PACKAGE_PATH);

  @Test
  public void execute() throws IOException {
    // via APT
    process(QuerydslAnnotationProcessor.class, CLASSES, "QuerydslAnnotationProcessor");

    // via GenericExporter
    GenericExporter exporter = new GenericExporter();
    exporter.setTargetFolder(new File("target/GenericExporterTest"));
    exporter.export(AbstractEntityTest.class.getPackage(), A.class.getPackage());

    List<String> expected = new ArrayList<String>();
    // delegates are not supported
    expected.add("QDelegateTest_SimpleUser.java");
    expected.add("QDelegateTest_SimpleUser2.java");
    expected.add("QDelegateTest_User.java");
    expected.add("QDelegate2Test_Entity.java");
    expected.add("QExampleEntity.java");
    expected.add("QQueryProjectionTest_DTOWithProjection.java");
    expected.add("QQueryProjectionTest_EntityWithProjection.java");
    expected.add("QEmbeddable3Test_EmbeddableClass.java");
    expected.add("QQueryEmbedded4Test_User.java");

    execute(expected, "GenericExporterTest", "QuerydslAnnotationProcessor");
  }

  @Test
  public void execute2() throws IOException {
    // via APT
    process(HibernateAnnotationProcessor.class, CLASSES, "HibernateAnnotationProcessor");

    // via GenericExporter
    GenericExporter exporter = new GenericExporter();
    exporter.setKeywords(Keywords.JPA);
    exporter.setEntityAnnotation(Entity.class);
    exporter.setEmbeddableAnnotation(Embeddable.class);
    exporter.setEmbeddedAnnotation(Embedded.class);
    exporter.setSupertypeAnnotation(MappedSuperclass.class);
    exporter.setSkipAnnotation(Transient.class);
    exporter.setTargetFolder(new File("target/GenericExporterTest2"));
    exporter.setStrictMode(true);
    exporter.setPropertyHandling(PropertyHandling.JPA);
    exporter.export(AbstractEntityTest.class.getPackage(), A.class.getPackage());

    List<String> expected = new ArrayList<String>();
    // GenericExporter doesn't include field/method selection
    expected.add("QTemporalTest_MyEntity.java");
    expected.add("QTemporal2Test_Cheque.java");
    expected.add("QQueryProjectionTest_DTOWithProjection.java");
    expected.add("QQueryProjectionTest_EntityWithProjection.java");
    expected.add("QEmbeddable3Test_EmbeddableClass.java");
    expected.add("QGeneric4Test_HidaBez.java");
    expected.add("QGeneric4Test_HidaBezGruppe.java");
    expected.add("QInterfaceType2Test_UserImpl.java");
    expected.add("QOrderTest_Order.java");
    expected.add("QManagedEmailTest_ManagedEmails.java");
    expected.add("QGeneric12Test_ChannelRole.java");
    expected.add("QManyToManyTest_Person.java");
    expected.add("QOneToOneTest_Person.java");
    expected.add("QGeneric16Test_HidaBez.java");
    expected.add("QGeneric16Test_HidaBezGruppe.java");

    execute(expected, "GenericExporterTest2", "HibernateAnnotationProcessor");
  }

  @Test
  public void execute3() {
    GenericExporter exporter = new GenericExporter();
    exporter.setKeywords(Keywords.JPA);
    exporter.setEntityAnnotation(Entity.class);
    exporter.setEmbeddableAnnotation(Embeddable.class);
    exporter.setEmbeddedAnnotation(Embedded.class);
    exporter.setSupertypeAnnotation(MappedSuperclass.class);
    exporter.setSkipAnnotation(Transient.class);
    exporter.setTargetFolder(new File("target/GenericExporterTest3"));
    exporter.setPropertyHandling(PropertyHandling.JPA);
    exporter.export(
        CustomCollection.MyCustomCollection.class,
        CustomCollection.MyCustomCollection2.class,
        CustomCollection.MyEntity.class);
  }

  @Test
  public void execute4() throws IOException {
    GenericExporter exporter = new GenericExporter();
    exporter.setKeywords(Keywords.JPA);
    exporter.setEntityAnnotation(Entity.class);
    exporter.setEmbeddableAnnotation(Embeddable.class);
    exporter.setEmbeddedAnnotation(Embedded.class);
    exporter.setSupertypeAnnotation(MappedSuperclass.class);
    exporter.setSkipAnnotation(Transient.class);
    exporter.setTargetFolder(new File("target/GenericExporterTest4"));
    exporter.setPropertyHandling(PropertyHandling.JPA);
    exporter.export(Generic2Test.class.getClasses());
  }

  private void execute(List<String> expected, String genericExporterFolder, String aptFolder)
      throws IOException {
    List<String> failures = new ArrayList<String>();
    int successes = 0;
    for (File file :
        new File("target/" + genericExporterFolder + "/com/querydsl/apt/domain").listFiles()) {
      File other = new File("target/" + aptFolder + "/com/querydsl/apt/domain", file.getName());
      if (!other.exists() || !other.isFile()) {
        continue;
      }
      String result1 = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
      String result2 = new String(Files.readAllBytes(other.toPath()), StandardCharsets.UTF_8);
      if (!result1.equals(result2)) {
        if (!expected.contains(file.getName())) {
          System.err.println(file.getName());
          failures.add(file.getName());
        } else {
          expected.remove(file.getName());
        }
      } else {
        successes++;
      }
    }
    expected.remove("QGeneric16Test_HidaBez.java"); // unstable
    expected.remove("QGeneric4Test_HidaBez.java"); // unstable
    expected.remove("QGeneric16Test_HidaBezGruppe.java"); // unstable
    expected.remove("QGeneric4Test_HidaBezGruppe.java"); // unstable
    if (!expected.isEmpty()) {
      fail("", "Following expected failures succeeded: " + expected);
    }

    if (!failures.isEmpty()) {
      for (String failure : failures) {
        System.err.println(failure);
      }
      fail(
          "",
          "Failed with " + failures.size() + " failures, " + successes + " succeeded, " + failures);
    }
  }
}
