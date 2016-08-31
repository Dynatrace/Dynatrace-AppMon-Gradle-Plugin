package com.example.simple;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Simple test suite
 */
public class StringMergeTest {

    @Test
    public void doMergeFromConstructor() throws Exception {
        StringMerge stringMerge = new StringMerge("abc", "def");

        assertThat(stringMerge.getA(), is("abc"));
        assertThat(stringMerge.getB(), is("def"));

        assertThat(stringMerge.doMerge(), is("abcdef"));
    }

    @Test
    public void doMergeFromSetter() throws Exception {
        StringMerge stringMerge = new StringMerge();

        stringMerge.setA("123");
        stringMerge.setB("456");

        assertThat(stringMerge.getA(), is("123"));
        assertThat(stringMerge.getB(), is("456"));

        assertThat(stringMerge.doMerge(), is("123456"));
    }

}