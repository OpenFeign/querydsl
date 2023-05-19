package com.querydsl.apt.domain;

import java.util.Set;

import jakarta.persistence.Entity;

@Entity
public class Location {

    public Set<Path> paths;
}
