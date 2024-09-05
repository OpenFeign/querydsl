package com.querydsl.jpa.hibernate;

import org.hibernate.reactive.mutiny.Mutiny;

public record MutinySessionContext(Mutiny.Session session) {}
