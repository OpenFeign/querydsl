package com.querydsl.apt.domain;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.junit.Test;

import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.TimePath;

public class TemporalTest {

    @Entity
    public static class MyEntity {

        @Temporal(value = TemporalType.DATE)
        private Date date;

        @Temporal(value = TemporalType.TIME)
        private Date time;
    }

    @Test
    public void test() {
        assertEquals(DatePath.class, QTemporalTest_MyEntity.myEntity.date.getClass());
        assertEquals(TimePath.class, QTemporalTest_MyEntity.myEntity.time.getClass());
    }

}
