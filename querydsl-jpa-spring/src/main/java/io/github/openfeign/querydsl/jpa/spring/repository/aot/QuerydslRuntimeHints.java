package io.github.openfeign.querydsl.jpa.spring.repository.aot;

import io.github.openfeign.querydsl.jpa.spring.repository.QuerydslJpaRepositoryImpl;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.lang.Nullable;

class QuerydslRuntimeHints implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {

    hints
        .reflection()
        .registerType(
            TypeReference.of(QuerydslJpaRepositoryImpl.class),
            hint ->
                hint.withMembers(
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_PUBLIC_METHODS));
  }
}
