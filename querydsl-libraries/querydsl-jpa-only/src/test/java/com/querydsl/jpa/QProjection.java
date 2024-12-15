package com.querydsl.jpa;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.domain.Cat;
import com.querydsl.jpa.domain.QCat;

public class QProjection extends ConstructorExpression<Projection> {

  private static final long serialVersionUID = -5866362075090550839L;

  public QProjection(StringExpression str, QCat cat) {
    super(Projection.class, new Class<?>[] {String.class, Cat.class}, new Expression[] {str, cat});
  }

  public QProjection(NumberExpression<Integer> i, BooleanExpression b) {
    super(Projection.class, new Class<?>[] {int.class, boolean.class}, new Expression[] {i, b});
  }
}
