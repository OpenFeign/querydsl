package com.querydsl.codegen;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import java.lang.annotation.Annotation;

public class ElementCollectionImpl implements ElementCollection {

  @Override
  public Class<? extends Annotation> annotationType() {
    return ElementCollection.class;
  }

  @Override
  public Class targetClass() {
    return null;
  }

  @Override
  public FetchType fetch() {
    return null;
  }
}
