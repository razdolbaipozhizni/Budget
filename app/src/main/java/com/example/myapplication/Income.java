package com.example.myapplication;

import java.io.Serializable;

public class Income implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final double amount;

    public Income(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }
}