package com.querydsl.sql;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.Set;
import org.junit.Test;

public class KeywordsEncodingTest {

    @SuppressWarnings("unchecked")
    @Test
    public void readLines_usesUtf8() throws Exception {
        Method m = Keywords.class.getDeclaredMethod("readLines", String.class);
        m.setAccessible(true);
        Set<String> lines = (Set<String>) m.invoke(null, "encoding-test");
        assertThat(lines).containsExactly("SELECT", "ÄÖÜ");
    }
}