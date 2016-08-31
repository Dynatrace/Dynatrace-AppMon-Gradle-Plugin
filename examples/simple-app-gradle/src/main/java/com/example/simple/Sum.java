package com.example.simple;

import java.util.ArrayList;

/**
 * Simple class for testing purposes
 */
public class Sum {
    ArrayList<Integer> list = new ArrayList<Integer>();

    public Integer getSum() {
        int sum = 0;

        for (Integer i : this.list) {
            sum += i;
        }

        return sum;
    }

    public void add(int i) {
        this.list.add(i);
    }
}
