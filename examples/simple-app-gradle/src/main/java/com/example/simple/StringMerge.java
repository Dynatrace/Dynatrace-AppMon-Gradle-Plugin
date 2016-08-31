package com.example.simple;

/**
 * Simple class for testing purposes
 */
public class StringMerge {
    private String a;
    private String b;

    public StringMerge(String a, String b) {
        this.a = a;
        this.b = b;
    }

    public StringMerge() {
        this.a = "";
        this.b = "";
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String doMerge() {
        return this.a + this.b;
    }
}
