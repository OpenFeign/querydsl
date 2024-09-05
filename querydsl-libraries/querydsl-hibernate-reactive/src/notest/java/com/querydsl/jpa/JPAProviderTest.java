package com.querydsl.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

// 5.664
public class JPAProviderTest {

  private EntityManagerFactory factory;

  private EntityManager em;

  @After
  public void tearDown() {
    if (em != null) {
      em.close();
    }
    if (factory != null) {
      factory.close();
    }
  }

  @Test
  public void hibernate() {
    Map<String, String> properties = new HashMap<>();
    factory = Persistence.createEntityManagerFactory("h2", properties);
    em = factory.createEntityManager();
    System.out.println(em.getDelegate().getClass());
    assertThat(JPAProvider.getTemplates(em)).isEqualTo(Hibernate5Templates.DEFAULT);
  }

  @Test
  public void hibernate_for_proxy() {
    factory = Persistence.createEntityManagerFactory("h2");
    em = factory.createEntityManager();
    InvocationHandler handler =
        new InvocationHandler() {
          @Override
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(em, args);
          }
        };
    var proxy =
        (EntityManager)
            Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {EntityManager.class},
                handler);
    assertThat(JPAProvider.getTemplates(proxy)).isEqualTo(Hibernate5Templates.DEFAULT);
  }

  @Test
  public void eclipseLink() {
    factory = Persistence.createEntityManagerFactory("h2-eclipselink");
    em = factory.createEntityManager();
    System.out.println(em.getDelegate().getClass());
    System.out.println(em.getProperties());
    assertThat(JPAProvider.getTemplates(em)).isEqualTo(EclipseLinkTemplates.DEFAULT);
  }

  @Test
  public void eclipseLink_for_proxy() {
    factory = Persistence.createEntityManagerFactory("h2-eclipselink");
    em = factory.createEntityManager();
    InvocationHandler handler =
        new InvocationHandler() {
          @Override
          public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(em, args);
          }
        };
    var proxy =
        (EntityManager)
            Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] {EntityManager.class},
                handler);
    assertThat(JPAProvider.getTemplates(proxy)).isEqualTo(EclipseLinkTemplates.DEFAULT);
  }

  @Test
  @Ignore // doesn't work on JDK 7
  public void openJPA() {
    factory = Persistence.createEntityManagerFactory("derby-openjpa");
    em = factory.createEntityManager();
    System.out.println(em.getDelegate().getClass());
    System.out.println(em.getProperties());
    assertThat(JPAProvider.getTemplates(em)).isEqualTo(OpenJPATemplates.DEFAULT);
  }

  @Test
  @Ignore // temporarily ignored, since Batoo hangs on EntityManager creation
  public void batoo() {
    factory = Persistence.createEntityManagerFactory("h2-batoo");
    em = factory.createEntityManager();
    System.out.println(em.getDelegate().getClass());
    System.out.println(em.getProperties());
    assertThat(JPAProvider.getTemplates(em)).isEqualTo(BatooTemplates.DEFAULT);
  }
}
