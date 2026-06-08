package com.querydsl.jpa.support;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Template;
import com.querydsl.sql.HSQLDBTemplates;
import com.querydsl.sql.SQLServer2012Templates;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.spi.TypeConfiguration;
import org.junit.jupiter.api.Test;

public class DialectSupportTest {

  @Test
  public void convert() {
    Template trim = HSQLDBTemplates.DEFAULT.getTemplate(Ops.TRIM);
    assertThat(DialectSupport.convert(trim)).isEqualTo("trim(both from ?1)");
    Template concat = HSQLDBTemplates.DEFAULT.getTemplate(Ops.CONCAT);
    assertThat(DialectSupport.convert(concat)).isEqualTo("?1 || ?2");
  }

  @Test
  public void createFunction_returns_null_type_for_operators_without_a_hibernate_basic_type() {
    // CURRENT_DATE is declared with a generic Comparable type that does not map to a Hibernate
    // BasicType, so HibernateUtil.getType(...) returns null.
    var template =
        DialectSupport.createFunction(SQLServer2012Templates.DEFAULT, Ops.DateTimeOps.CURRENT_DATE);

    assertThat(template.type()).isNull();
    assertThat(template.pattern()).isEqualTo("cast(getdate() as date)");
  }

  @Test
  public void extendRegistry_registers_pattern_without_type_when_type_is_null() {
    var functionRegistry = new SqmFunctionRegistry();
    FunctionContributions contributions =
        contributionsBackedBy(functionRegistry, new TypeConfiguration());

    var template =
        DialectSupport.createFunction(SQLServer2012Templates.DEFAULT, Ops.DateTimeOps.CURRENT_DATE);
    assertThat(template.type()).isNull();

    // The null-type branch must register the pattern without resolving a return type instead of
    // failing on the null type.
    DialectSupport.extendRegistry("current_date", template, contributions);

    assertThat(functionRegistry.findFunctionDescriptor("current_date")).isNotNull();
  }

  @Test
  public void extendRegistry_registers_typed_patterns_from_templates() {
    var functionRegistry = new SqmFunctionRegistry();
    FunctionContributions contributions =
        contributionsBackedBy(functionRegistry, new TypeConfiguration());

    // The bulk overload registers the date-part functions, whose types (e.g. Integer) do map to a
    // Hibernate BasicType and therefore exercise the typed branch.
    DialectSupport.extendRegistry(SQLServer2012Templates.DEFAULT, contributions);

    assertThat(functionRegistry.findFunctionDescriptor("year")).isNotNull();
    assertThat(functionRegistry.findFunctionDescriptor("month")).isNotNull();
    assertThat(functionRegistry.findFunctionDescriptor("second")).isNotNull();
  }

  private static FunctionContributions contributionsBackedBy(
      SqmFunctionRegistry functionRegistry, TypeConfiguration typeConfiguration) {
    return new FunctionContributions() {
      @Override
      public SqmFunctionRegistry getFunctionRegistry() {
        return functionRegistry;
      }

      @Override
      public TypeConfiguration getTypeConfiguration() {
        return typeConfiguration;
      }

      @Override
      public ServiceRegistry getServiceRegistry() {
        return null;
      }
    };
  }
}
