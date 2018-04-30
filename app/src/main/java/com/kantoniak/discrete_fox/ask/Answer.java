package com.kantoniak.discrete_fox.ask;

import com.kantoniak.discrete_fox.country.Country;

public class Answer {
    private Country country;
    private String value;
    private double valueRaw;
    private int color;

    public Answer(Country country, String value, double valueRaw, int color) {
        this.country = country;
        this.value = value;
        this.valueRaw = valueRaw;
        this.color = color;
    }

    public double getValueRaw() {
        return valueRaw;
    }

    public String getCountry() {
        return country.getShortName();
    }

    public String getValue() {
        return value;
    }

    public int getColor() {
        return color;
    }
}