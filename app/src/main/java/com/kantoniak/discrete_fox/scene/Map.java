package com.kantoniak.discrete_fox.scene;

import android.support.annotation.Nullable;

import com.kantoniak.discrete_fox.CountryUtil;

import org.rajawali3d.math.vector.Vector2;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Map {

    public static List<String> COUNTRY_CODES = CountryUtil.getIsoCodes();
    HashMap<String, Country> countries = new HashMap<>();

    private static final java.util.Map<String, Vector2> countryMiddles;

    static {
        java.util.Map<String, Vector2> middles = new HashMap<>();
        middles.put(COUNTRY_CODES.get(0), new Vector2(-1.34, -1.92));
        middles.put(COUNTRY_CODES.get(1), new Vector2(-3.44, -0.56));
        middles.put(COUNTRY_CODES.get(2), new Vector2(+2.14, -3.57));
        middles.put(COUNTRY_CODES.get(3), new Vector2(+5.58, -6.10));
        middles.put(COUNTRY_CODES.get(4), new Vector2(-0.64, -1.09));
        middles.put(COUNTRY_CODES.get(5), new Vector2(-2.00, -0.73));
        middles.put(COUNTRY_CODES.get(6), new Vector2(-2.06, +1.50));
        middles.put(COUNTRY_CODES.get(7), new Vector2(+0.98, +2.45));
        middles.put(COUNTRY_CODES.get(8), new Vector2(-6.65, -3.85));
        middles.put(COUNTRY_CODES.get(9), new Vector2(+0.71, +5.20));
        middles.put(COUNTRY_CODES.get(10), new Vector2(-4.15, -1.89));
        middles.put(COUNTRY_CODES.get(11), new Vector2(-4.88, +1.23));
        middles.put(COUNTRY_CODES.get(12), new Vector2(+1.79, -5.16));
        middles.put(COUNTRY_CODES.get(13), new Vector2(-0.50, -2.95));
        middles.put(COUNTRY_CODES.get(14), new Vector2(+0.15, -2.02));
        middles.put(COUNTRY_CODES.get(15), new Vector2(-6.08, +1.54));
        middles.put(COUNTRY_CODES.get(16), new Vector2(-1.44, -4.27));
        middles.put(COUNTRY_CODES.get(17), new Vector2(+1.00, +1.16));
        middles.put(COUNTRY_CODES.get(18), new Vector2(-3.18, -0.90));
        middles.put(COUNTRY_CODES.get(19), new Vector2(+1.07, +1.85));
        middles.put(COUNTRY_CODES.get(20), new Vector2(-3.17, +0.04));
        middles.put(COUNTRY_CODES.get(21), new Vector2(+0.06, -0.25));
        middles.put(COUNTRY_CODES.get(22), new Vector2(-8.33, -3.78));
        middles.put(COUNTRY_CODES.get(23), new Vector2(+1.86, -2.65));
        middles.put(COUNTRY_CODES.get(24), new Vector2(-0.75, +3.84));
        middles.put(COUNTRY_CODES.get(25), new Vector2(-1.00, -2.56));
        middles.put(COUNTRY_CODES.get(26), new Vector2(+0.18, -1.43));
        countryMiddles = Collections.unmodifiableMap(middles);
    }

    public void addCountry(Country country) {
        countries.put(country.getCode(), country);
    }

    public HashMap<String, Country> getCountries() {
        return countries;
    }

    public void disableAllCountries() {
        for (java.util.Map.Entry<String, Country> entry: getCountries().entrySet()) {
            Country country = entry.getValue();
            country.setDisabled(true);
        }
    }

    public @Nullable Country getCountry(String code) {
        return getCountries().get(code);
    }

    public void enableCountry(String code) {
        Country country = getCountries().get(code);
        if (country == null) {
            return;
        }
        country.setDisabled(false);
    }

    public void setColors(int minColor, int maxColor) {
        for (java.util.Map.Entry<String, Country> entry: getCountries().entrySet()) {
            Country country = entry.getValue();
            country.setColors(minColor, maxColor);
        }
    }

    public Vector2 getCountryMiddle(String code) {
        Vector2 pos = countryMiddles.get(code);
        return pos;
    }
}
