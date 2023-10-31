/*
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.sql.codegen;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.validation.constraints.NotNull;

import com.querydsl.core.util.ReflectionUtils;
import com.querydsl.sql.Connections;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import com.querydsl.codegen.utils.SimpleCompiler;
import com.querydsl.codegen.BeanSerializer;
import com.querydsl.core.util.FileUtils;

public class MetaDataExporterTest {

    private static Connection connection;

    private boolean clean = true;

    private boolean exportColumns = false;

    private boolean schemaToPackage = false;

    private DatabaseMetaData metadata;

    private JavaCompiler compiler = new SimpleCompiler();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void setUpClass() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        String url = "jdbc:h2:mem:testdb" + System.currentTimeMillis();
        connection = DriverManager.getConnection(url, "sa", "");
        createTables(connection);
    }

    static void createTables(Connection connection) throws SQLException {

        try (Statement stmt = connection.createStatement()) {
            // reserved words
            stmt.execute("create table reserved (id int, while int)");

            stmt.execute("create table class (id int)");

            // underscore
            stmt.execute("create table underscore (e_id int, c_id int)");

            // bean generation
            stmt.execute("create table beangen1 (\"SEP_Order\" int)");

            // default instance clash
            stmt.execute("create table definstance (id int, definstance int, definstance1 int)");

            // class with pk and fk classes
            stmt.execute("create table pkfk (id int primary key, pk int, fk int)");

            // camel case
            stmt.execute("create table \"camelCase\" (id int)");
            stmt.execute("create table \"vwServiceName\" (id int)");

            // simple types
            stmt.execute("create table date_test (d date)");
            stmt.execute("create table date_time_test (dt datetime)");

            // complex type
            stmt.execute("create table survey (id int, name varchar(30))");

            // new line
            stmt.execute("create table \"new\nline\" (id int)");

            stmt.execute("create table newline2 (id int, \"new\nline\" int)");

            stmt.execute("create table employee("
                    + "id INT, "
                    + "firstname VARCHAR(50), "
                    + "lastname VARCHAR(50), "
                    + "salary DECIMAL(10, 2), "
                    + "datefield DATE, "
                    + "timefield TIME, "
                    + "superior_id int, "
                    + "survey_id int, "
                    + "survey_name varchar(30), "
                    + "CONSTRAINT PK_employee PRIMARY KEY (id), "
                    + "CONSTRAINT FK_superior FOREIGN KEY (superior_id) REFERENCES employee(id))");

            // multi key
            stmt.execute("create table multikey(id INT, id2 VARCHAR, id3 INT, CONSTRAINT pk_multikey PRIMARY KEY (id, id2, id3) )");

            //  M_PRODUCT_BOM_ID
            stmt.execute("create table product(id int, "
                    + "m_product_bom_id int, "
                    + "m_productbom_id int, "
                    + "constraint product_bom foreign key (m_productbom_id) references product(id))");
        }
    }

    @AfterClass
    public static void tearDownClass() throws SQLException {
        connection.close();
    }

    @Before
    public void setUp() throws ClassNotFoundException, SQLException {
        metadata = connection.getMetaData();
    }


    private static final NamingStrategy defaultNaming = new DefaultNamingStrategy();

    private static final NamingStrategy originalNaming = new OriginalNamingStrategy();

    private String beanPackageName = null;

    @Test
    public void normalSettings_repetition() throws SQLException {
        test("Q", "", "", "", defaultNaming, folder.getRoot(), false, false, false);

        File file = new File(folder.getRoot(), "test/QEmployee.java");
        long lastModified = file.lastModified();
        assertTrue(file.exists());

        clean = false;
        test("Q", "", "", "", defaultNaming, folder.getRoot(), false, false, false);
        assertEquals(lastModified, file.lastModified());
    }

    @Test
    public void explicit_configuration() throws SQLException {
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setCatalogPattern("%TESTDB%");
        exporter.setSchemaPattern("PUBLIC");
        exporter.setNamePrefix("Q");
        exporter.setPackageName("test");
        exporter.setTargetFolder(folder.getRoot());
        exporter.setNamingStrategy(new DefaultNamingStrategy());
        exporter.setBeanSerializer(new BeanSerializer());
        exporter.setBeanPackageName("test2");
        exporter.export(metadata);

        assertTrue(new File(folder.getRoot(), "test/QDateTest.java").exists());
        assertTrue(new File(folder.getRoot(), "test2/DateTest.java").exists());
    }


    @Test
    public void validation_annotations_are_not_added_to_columns_with_default_values() throws SQLException, ClassNotFoundException, MalformedURLException {
        Statement stmt = connection.createStatement();
        stmt.execute("CREATE TABLE foo ("
                + "id INTEGER PRIMARY KEY AUTO_INCREMENT NOT NULL,"
                + "name VARCHAR(255) NOT NULL DEFAULT 'some default')");

        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setSchemaPattern("PUBLIC");
        exporter.setNamePrefix("Q");
        exporter.setPackageName("test");
        exporter.setTableNamePattern("FOO");
        exporter.setTargetFolder(folder.getRoot());
        exporter.setBeanSerializer(new BeanSerializer());
        exporter.setValidationAnnotations(true);
        exporter.export(metadata);

        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {folder.getRoot().toURI().toURL()});
        compiler.run(null, null, null, folder.getRoot().getAbsoluteFile()  + "/test/Foo.java");
        Class<?> cls = Class.forName("test.Foo", true, classLoader);
        assertThat(ReflectionUtils.getAnnotatedElement(cls, "id", Integer.class).getAnnotation(NotNull.class), is(nullValue()));
        assertThat(ReflectionUtils.getAnnotatedElement(cls, "name", String.class).getAnnotation(NotNull.class), is(nullValue()));

        stmt.execute("DROP TABLE foo");
    }

    @Test
    public void validation_annotations_are_added_to_columns_without_default_values() throws SQLException, ClassNotFoundException, MalformedURLException {
        Statement stmt = connection.createStatement();
        stmt.execute("CREATE TABLE bar ("
                + "id VARCHAR(10) PRIMARY KEY NOT NULL,"
                + "name VARCHAR(255) NOT NULL)");

        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setSchemaPattern("PUBLIC");
        exporter.setNamePrefix("Q");
        exporter.setPackageName("test");
        exporter.setTableNamePattern("BAR");
        exporter.setTargetFolder(folder.getRoot());
        exporter.setBeanSerializer(new BeanSerializer());
        exporter.setValidationAnnotations(true);
        exporter.export(metadata);

        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {folder.getRoot().toURI().toURL()});
        compiler.run(null, null, null, folder.getRoot().getAbsoluteFile()  + "/test/Bar.java");
        Class<?> cls = Class.forName("test.Bar", true, classLoader);
        assertThat(ReflectionUtils.getAnnotatedElement(cls, "id", Integer.class).getAnnotation(NotNull.class), is(notNullValue()));
        assertThat(ReflectionUtils.getAnnotatedElement(cls, "name", String.class).getAnnotation(NotNull.class), is(notNullValue()));

        stmt.execute("DROP TABLE bar");
    }

    @Test
    public void minimal_configuration() throws SQLException {
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setSchemaPattern("PUBLIC");
        exporter.setPackageName("test");
        exporter.setTargetFolder(folder.getRoot());
        exporter.export(metadata);

        assertTrue(new File(folder.getRoot(), "test/QDateTest.java").exists());
    }

    @Test
    public void minimal_configuration_with_schemas() throws SQLException {
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setSchemaPattern("PUBLIC2,PUBLIC");
        exporter.setPackageName("test");
        exporter.setTargetFolder(folder.getRoot());
        exporter.export(metadata);

        assertTrue(new File(folder.getRoot(), "test/QDateTest.java").exists());
    }

    @Test
    public void minimal_configuration_with_schemas_and_tables() throws SQLException {
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setSchemaPattern("PUBLIC2,PUBLIC");
        exporter.setTableNamePattern("RESERVED,UNDERSCORE,BEANGEN1");
        exporter.setPackageName("test");
        exporter.setTargetFolder(folder.getRoot());
        exporter.export(metadata);

        assertTrue(new File(folder.getRoot(), "test/QBeangen1.java").exists());
        assertTrue(new File(folder.getRoot(), "test/QReserved.java").exists());
        assertTrue(new File(folder.getRoot(), "test/QUnderscore.java").exists());
        assertFalse(new File(folder.getRoot(), "test/QDefinstance.java").exists());
    }

    @Test
    public void minimal_configuration_with_tables() throws SQLException {
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setSchemaPattern("PUBLIC");
        exporter.setTableNamePattern("RESERVED,UNDERSCORE,BEANGEN1");
        exporter.setPackageName("test");
        exporter.setTargetFolder(folder.getRoot());
        exporter.export(metadata);

        assertTrue(new File(folder.getRoot(), "test/QBeangen1.java").exists());
        assertTrue(new File(folder.getRoot(), "test/QReserved.java").exists());
        assertTrue(new File(folder.getRoot(), "test/QUnderscore.java").exists());
        assertFalse(new File(folder.getRoot(), "test/QDefinstance.java").exists());
    }

    @Test(expected = IllegalStateException.class)
    public void minimal_configuration_with_duplicate_tables() throws SQLException {
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setSchemaPattern("PUBLIC");
        exporter.setTableNamePattern("%,%");
        exporter.setPackageName("test");
        exporter.setTargetFolder(folder.getRoot());
        exporter.export(metadata);

        assertTrue(new File(folder.getRoot(), "test/QBeangen1.java").exists());
        assertTrue(new File(folder.getRoot(), "test/QReserved.java").exists());
        assertTrue(new File(folder.getRoot(), "test/QUnderscore.java").exists());
        assertFalse(new File(folder.getRoot(), "test/QDefinstance.java").exists());
    }

    @Test
    public void minimal_configuration_with_suffix() throws SQLException {
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setSchemaPattern("PUBLIC");
        exporter.setPackageName("test");
        exporter.setNamePrefix("");
        exporter.setNameSuffix("Type");
        exporter.setTargetFolder(folder.getRoot());
        exporter.export(metadata);

        assertTrue(new File(folder.getRoot(), "test/DateTestType.java").exists());
    }

    @Test
    public void minimal_configuration_without_keys() throws SQLException {
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setSchemaPattern("PUBLIC");
        exporter.setPackageName("test");
        exporter.setNamePrefix("");
        exporter.setNameSuffix("Type");
        exporter.setTargetFolder(folder.getRoot());
        exporter.setExportForeignKeys(false);
        exporter.export(metadata);

        assertTrue(new File(folder.getRoot(), "test/DateTestType.java").exists());
    }

    @Test
    public void minimal_configuration_only_direct_foreign_keys() throws SQLException {
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setSchemaPattern("PUBLIC");
        exporter.setPackageName("test");
        exporter.setNamePrefix("");
        exporter.setNameSuffix("Type");
        exporter.setTargetFolder(folder.getRoot());
        exporter.setExportInverseForeignKeys(false);
        exporter.export(metadata);

        assertTrue(new File(folder.getRoot(), "test/DateTestType.java").exists());
    }

    @Test
    public void minimal_configuration_with_bean_prefix() throws SQLException {
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setSchemaPattern("PUBLIC");
        exporter.setPackageName("test");
        exporter.setNamePrefix("");
        exporter.setBeanPrefix("Bean");
        exporter.setBeanSerializer(new BeanSerializer());
        exporter.setTargetFolder(folder.getRoot());
        exporter.export(metadata);

        assertTrue(new File(folder.getRoot(), "test/DateTest.java").exists());
        assertTrue(new File(folder.getRoot(), "test/BeanDateTest.java").exists());
    }

    @Test
    public void minimal_configuration_with_bean_suffix() throws SQLException {
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setSchemaPattern("PUBLIC");
        exporter.setPackageName("test");
        exporter.setNamePrefix("");
        exporter.setBeanSuffix("Bean");
        exporter.setBeanSerializer(new BeanSerializer());
        exporter.setTargetFolder(folder.getRoot());
        exporter.export(metadata);

        assertTrue(new File(folder.getRoot(), "test/DateTest.java").exists());
        assertTrue(new File(folder.getRoot(), "test/DateTestBean.java").exists());
    }

    @Test
    public void minimal_configuration_with_bean_folder() throws SQLException, IOException {
        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setSchemaPattern("PUBLIC");
        exporter.setPackageName("test");
        exporter.setNamePrefix("");
        exporter.setBeanSuffix("Bean");
        exporter.setBeanSerializer(new BeanSerializer());
        exporter.setTargetFolder(folder.getRoot());
        exporter.setBeansTargetFolder(folder.newFolder("beans"));
        exporter.export(metadata);

        assertTrue(new File(folder.getRoot(), "test/DateTest.java").exists());
        assertTrue(new File(folder.getRoot(), "beans/test/DateTestBean.java").exists());
    }

//    @Test FIXME can't get mysql admin access working with circle CI, might need to move to something else
    public void catalog_pattern() throws SQLException, IOException, ClassNotFoundException {
        Connections.initMySQL();
        Connection connection = Connections.getConnection();
        Statement stmt = Connections.getStatement();
        try {
            stmt.execute("CREATE DATABASE IF NOT EXISTS catalog_test_one");
            stmt.execute("CREATE TABLE IF NOT EXISTS catalog_test_one.test_catalog_table_one(id INT PRIMARY KEY, foo VARCHAR(32) NOT NULL)");
            stmt.execute("CREATE DATABASE IF NOT EXISTS catalog_test_two");
            stmt.execute("CREATE TABLE IF NOT EXISTS catalog_test_two.test_catalog_table_two(id INT PRIMARY KEY, foo VARCHAR(32) NOT NULL)");

            MetaDataExporter exporter = new MetaDataExporter();
            exporter.setSchemaPattern("PUBLIC");
            exporter.setCatalogPattern("catalog_test_one");
            exporter.setPackageName("test");
            exporter.setNamePrefix("");
            exporter.setBeanSuffix("Bean");
            exporter.setBeanSerializer(new BeanSerializer());
            exporter.setTargetFolder(folder.getRoot());
            exporter.setBeansTargetFolder(folder.newFolder("beans"));

            exporter.export(connection.getMetaData());
            assertTrue(new File(folder.getRoot(), "test/TestCatalogTableOne.java").exists());
            assertTrue(new File(folder.getRoot(), "beans/test/TestCatalogTableOneBean.java").exists());

            assertFalse(new File(folder.getRoot(), "test/TestCatalogTableTwo.java").exists());
            assertFalse(new File(folder.getRoot(), "beans/test/TestCatalogTableTwoBean.java").exists());

            exporter.setCatalogPattern("catalog_test_two");
            exporter.export(connection.getMetaData());

            assertTrue(new File(folder.getRoot(), "test/TestCatalogTableTwo.java").exists());
            assertTrue(new File(folder.getRoot(), "beans/test/TestCatalogTableTwoBean.java").exists());
        } finally {
            stmt.execute("DROP DATABASE IF EXISTS catalog_test_one");
            stmt.execute("DROP DATABASE IF EXISTS catalog_test_two");
            stmt.close();
            Connections.close();
        }
    }

    private void test(String namePrefix, String nameSuffix, String beanPrefix, String beanSuffix,
            NamingStrategy namingStrategy, File targetDir, boolean withBeans,
            boolean withInnerClasses, boolean withOrdinalPositioning) throws SQLException {
        if (clean) {
            try {
                if (targetDir.exists()) {
                    FileUtils.delete(targetDir);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        MetaDataExporter exporter = new MetaDataExporter();
        exporter.setColumnAnnotations(exportColumns);
        exporter.setSchemaPattern("PUBLIC");
        exporter.setNamePrefix(namePrefix);
        exporter.setNameSuffix(nameSuffix);
        exporter.setBeanPrefix(beanPrefix);
        exporter.setBeanSuffix(beanSuffix);
        exporter.setInnerClassesForKeys(withInnerClasses);
        exporter.setPackageName("test");
        exporter.setBeanPackageName(beanPackageName);
        exporter.setTargetFolder(targetDir);
        exporter.setNamingStrategy(namingStrategy);
        exporter.setSchemaToPackage(schemaToPackage);
        if (withBeans) {
            exporter.setBeanSerializer(new BeanSerializer());
        }
        if (withOrdinalPositioning) {
            exporter.setColumnComparatorClass(OrdinalPositionComparator.class);
        }
        exporter.export(metadata);

        Set<String> classes = exporter.getClasses();
        int compilationResult = compiler.run(null, System.out, System.err,
                classes.toArray(new String[0]));
        if (compilationResult != 0) {
            Assert.fail("Compilation Failed for " + targetDir.getAbsolutePath());
        }
    }

}
