package com.querydsl.example.ksp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

/**
 * A Java entity living alongside the Kotlin entities in this example.
 *
 * <p>Demonstrates the typical reason to reach for {@code querydsl-ksp-codegen}:
 * with {@code querydsl-apt} a mixed Java/Kotlin Gradle project hits a chicken-and-egg
 * compile order — Kotlin compiles before Java, so the Java Q-classes APT generates
 * aren't on the classpath when Kotlin sources reference them. KSP runs as part of
 * {@code kspKotlin}, sees Java sources alongside Kotlin sources, and emits {@code .kt}
 * Q-classes that compile cleanly with the rest of the Kotlin code.
 *
 * <p>Also exercises the self-reference case: {@code parent} is itself a {@code Branch},
 * which under eager initialisation would stack-overflow at construction. The KSP
 * processor emits the field as {@code by lazy} so each access creates one more level
 * on demand.
 */
@Entity
public class Branch {

    @Id private Long id;

    private String name;

    @ManyToOne private Branch parent;

    public Branch() {}

    public Branch(Long id, String name, Branch parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Branch getParent() {
        return parent;
    }

    public void setParent(Branch parent) {
        this.parent = parent;
    }
}
