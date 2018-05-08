package com.kantoniak.discrete_fox.gameplay;

import com.kantoniak.discrete_fox.Country;

/**
 * Class representing answer.
 */
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

    /**
     * Get raw value.
     *
     * @return Double with raw value
     */
    public double getValueRaw() {
        return valueRaw;
    }

    /**
     * Get country.
     *
     * @return Country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * Get value.
     *
     * @return Value
     */
    public String getValue() {
        return value;
    }

    /**
     * Get color.
     *
     * @return Color
     */
    public int getColor() {
        return color;
    }
}