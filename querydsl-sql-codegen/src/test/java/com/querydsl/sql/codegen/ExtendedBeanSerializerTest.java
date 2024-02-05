package com.querydsl.sql.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.codegen.EntityType;
import com.querydsl.codegen.Property;
import com.querydsl.codegen.SimpleSerializerConfig;
import com.querydsl.codegen.utils.JavaWriter;
import com.querydsl.codegen.utils.SimpleCompiler;
import com.querydsl.codegen.utils.model.ClassType;
import com.querydsl.codegen.utils.model.SimpleType;
import com.querydsl.codegen.utils.model.Type;
import com.querydsl.codegen.utils.model.TypeCategory;
import com.querydsl.sql.ColumnImpl;
import com.querydsl.sql.codegen.support.PrimaryKeyData;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ExtendedBeanSerializerTest {

  private static final String CLASS_NAME = "DomainClass";
  private static final String[] PATH = {"com", "querydsl", "test", "gen"};
  private static final String PACKAGE = String.join(".", PATH);
  private static final String FULL_NAME = PACKAGE + "." + CLASS_NAME;

  @Rule public TemporaryFolder compileFolder = new TemporaryFolder();

  private EntityType type;

  private File srcFile;

  @Before
  public void setUp() throws IOException {
    Type typeModel =
        new SimpleType(TypeCategory.ENTITY, FULL_NAME, PACKAGE, CLASS_NAME, false, false);
    type = new EntityType(typeModel);
    File srcFolder = compileFolder.newFolder(PATH);
    srcFile = new File(srcFolder, CLASS_NAME + ".java");
  }

  @Test
  public void equals_hashcode_tostring() throws Exception {
    Property idCol = new Property(type, "id", new ClassType(Integer.class));
    idCol.addAnnotation(new ColumnImpl("ID"));
    Property subIdCol = new Property(type, "sub_id", new ClassType(String.class));
    subIdCol.addAnnotation(new ColumnImpl("SUB_ID"));
    Property nameCol = new Property(type, "name", new ClassType(String.class));
    nameCol.addAnnotation(new ColumnImpl("NAME"));

    type.addProperty(idCol);
    type.addProperty(subIdCol);
    type.addProperty(nameCol);

    type.getData()
        .put(
            PrimaryKeyData.class,
            Arrays.asList(new PrimaryKeyData("PK", new String[] {"ID", "SUB_ID"})));

    ExtendedBeanSerializer extendedBeanSerializer = new ExtendedBeanSerializer();
    extendedBeanSerializer.setAddToString(true);

    FileWriter fw = new FileWriter(srcFile);
    extendedBeanSerializer.serialize(type, SimpleSerializerConfig.DEFAULT, new JavaWriter(fw));
    fw.close();

    URLClassLoader classLoader =
        URLClassLoader.newInstance(new URL[] {compileFolder.getRoot().toURI().toURL()});
    int retCode = new SimpleCompiler().run(null, System.out, System.err, srcFile.getAbsolutePath());
    assertThat(retCode).as("The generated source should compile").isEqualTo(0);

    Class<?> cls = Class.forName(FULL_NAME, true, classLoader);
    ReflectionHelper reflection = new ReflectionHelper(cls);
    Object obj1 = cls.getDeclaredConstructor().newInstance();
    Object obj1a = cls.getDeclaredConstructor().newInstance();
    Object obj2 = cls.getDeclaredConstructor().newInstance();
    reflection.setValues(obj1, 1, "##", "X");
    reflection.setValues(obj1a, 1, "##", null);
    reflection.setValues(obj2, 2, "--", "Y");

    assertThat(obj1a).isEqualTo(obj1);
    assertThat(obj1a.hashCode()).isEqualTo(obj1.hashCode());
    assertThat(obj2).isNotEqualTo(obj1);

    assertThat("DomainClass#1;##").isEqualTo(obj1.toString());
  }

  private static class ReflectionHelper {
    private final Map<String, Method> methodByName = new HashMap<String, Method>();

    ReflectionHelper(Class<?> cls) {
      for (Method m : cls.getDeclaredMethods()) {
        methodByName.put(m.getName(), m);
      }
    }

    private void setValues(Object instance, int id, String subId, String name) throws Exception {
      methodByName.get("setId").invoke(instance, id);
      methodByName.get("setSub_id").invoke(instance, subId);
      methodByName.get("setName").invoke(instance, name);
    }
  }
}
