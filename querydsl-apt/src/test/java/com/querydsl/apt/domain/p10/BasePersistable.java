package com.querydsl.apt.domain.p10;

import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class BasePersistable<T extends Serializable> extends AbstractPersistable<T> implements UpdateInfo {

}