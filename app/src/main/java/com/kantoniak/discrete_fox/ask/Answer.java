package com.kantoniak.discrete_fox.ask;

public class Answer {
    private String country;
    private String value;
    private double valueRaw;
    private int color;

    public Answer(String country, String value, double valueRaw, int color) {
        this.country = country;
        this.value = value;
        this.valueRaw = valueRaw;
        this.color = color;
    }

    public double getValueRaw() {
        return valueRaw;
    }

    public String getCountry() {
        return country;
    }

    public String getValue() {
        return value;
    }

    public int getColor() {
        return color;
    }
}