package com.querydsl.jpa.suites;

import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

import com.querydsl.core.Target;
import com.querydsl.core.testutil.H2;
import com.querydsl.jpa.*;

@Category(H2.class)
public class H2SuiteTest extends AbstractSuite {

    // FIXME new h2 not compatible with old hibernate/jpa
    // public static class JPA extends JPABase { }
    public static class JPASQL extends JPASQLBase { }
    public static class JPAIntegration extends JPAIntegrationBase { }
    public static class Serialization extends SerializationBase { }
    // FIXME new h2 not compatible with old hibernate/jpa
    // public static class Hibernate extends HibernateBase { }
    public static class HibernateSQL extends HibernateSQLBase { }

    @BeforeClass
    public static void setUp() throws Exception {
        Mode.mode.set("h2");
        Mode.target.set(Target.H2);
    }

}
