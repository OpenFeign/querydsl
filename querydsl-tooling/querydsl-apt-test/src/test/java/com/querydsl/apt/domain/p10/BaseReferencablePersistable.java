package com.querydsl.apt.domain.p10;

import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class BaseReferencablePersistable<PK extends Serializable>
    extends BasePersistable<PK> {}
