package com.example.simple;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Test suite with some Thread.sleep() calls
 */
public class SumTest {

    @Test
    public void sumWithoutAdding() throws Exception {
        Sum sum = new Sum();

        assertThat(sum.getSum(), is(0));
        Thread.sleep(500);
    }


    @Test
    public void sumWithPositiveNumbers() throws Exception {
        Sum sum = new Sum();

        sum.add(1);
        sum.add(2);
        sum.add(7);
        assertThat(sum.getSum(), is(10));
        Thread.sleep(500);
    }

    @Test
    public void sumWithNegativeNumbers() throws Exception {
        Sum sum = new Sum();

        sum.add(-1);
        sum.add(-2);
        sum.add(-7);
        assertThat(sum.getSum(), is(-10));
        Thread.sleep(500);
    }

    @Test
    public void sumWithMixedNumbers() throws Exception {
        Sum sum = new Sum();

        sum.add(-1);
        sum.add(7);
        sum.add(-2);
        sum.add(1);
        sum.add(-7);
        sum.add(2);
        assertThat(sum.getSum(), is(0));
        Thread.sleep(500);
    }
}