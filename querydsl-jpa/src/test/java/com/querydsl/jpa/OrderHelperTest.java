package com.querydsl.jpa;

import static com.querydsl.jpa.JPAExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.PathBuilder;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class OrderHelperTest {

  @Test
  public void order() {
    PathBuilder<Object> entity = new PathBuilder<Object>(Object.class, "project");
    List<String> order = new ArrayList<>();
    order.add("customer.name");
    order.add("department.superior.name");
    order.add("customer.company.name");
    order.add("previousProject.customer.company.name");
    order.add("department.name");

    JPQLSubQuery<?> query = select(entity);
    query.from(entity);
    OrderHelper.orderBy(query, entity, order);
    assertThat(query.toString())
        .isEqualTo(
            """
            select project
            from Object project
              left join project.customer as customer
              left join project.department as department
              left join department.superior as department_superior
              left join customer.company as customer_company
              left join project.previousProject as previousProject
              left join previousProject.customer as previousProject_customer
              left join previousProject_customer.company as previousProject_customer_company
            order by customer.name asc, department_superior.name asc, customer_company.name asc,\
             previousProject_customer_company.name asc, department.name asc\
            """);
  }
}
