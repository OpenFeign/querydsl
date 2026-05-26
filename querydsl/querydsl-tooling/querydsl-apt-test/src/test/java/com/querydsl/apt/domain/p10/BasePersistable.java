package com.querydsl.apt.domain.p10;

import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class BasePersistable<T extends Serializable> extends AbstractPersistable<T>
    implements UpdateInfo {}
