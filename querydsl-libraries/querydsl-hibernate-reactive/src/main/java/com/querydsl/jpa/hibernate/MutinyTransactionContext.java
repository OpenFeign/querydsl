package com.querydsl.jpa.hibernate;

import org.hibernate.reactive.mutiny.Mutiny;

public record MutinyTransactionContext(Mutiny.Session session, Mutiny.Transaction transaction) {}
