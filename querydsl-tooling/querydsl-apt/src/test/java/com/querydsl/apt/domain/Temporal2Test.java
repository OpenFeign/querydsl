package com.querydsl.apt.domain;

import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import org.junit.Test;

public class Temporal2Test {

  @Entity
  public static class Cheque {
    @Temporal(TemporalType.DATE)
    private Date dataVencimento;

    @Column(precision = 15, scale = 2)
    private BigDecimal valor;

    @QueryProjection
    public Cheque(Date param0, BigDecimal param1) {
      this.dataVencimento = param0;
      this.valor = param1;
    }
  }

  @Test
  public void test() {
    DatePath<Date> datePath = Expressions.datePath(Date.class, "date");
    NumberPath<BigDecimal> numberPath = Expressions.numberPath(BigDecimal.class, "num");
    new QTemporal2Test_Cheque(datePath, numberPath);
  }
}
