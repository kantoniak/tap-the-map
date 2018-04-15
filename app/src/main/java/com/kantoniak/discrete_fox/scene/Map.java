package com.kantoniak.discrete_fox.scene;

import android.support.annotation.Nullable;

import org.rajawali3d.math.vector.Vector2;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Map {

    public static List<String> COUNTRY_CODES = Arrays.asList("at", "be", "bg", "cy", "cz", "de", "dk", "ee", "es", "fi", "fr", "gb", "gr", "hr", "hu", "ie", "it", "lt", "lu", "lv", "ne", "pl", "pt", "ro", "se", "si", "sk");
    HashMap<String, Country> countries = new HashMap<>();

    private static final java.util.Map<String, Vector2> countryMiddles;

    static {
        java.util.Map<String, Vector2> middles = new HashMap<>();
        middles.put("at", new Vector2(-1.34, -1.92));
        middles.put("be", new Vector2(-3.44, -0.56));
        middles.put("bg", new Vector2(+2.14, -3.57));
        middles.put("cy", new Vector2(+5.58, -6.10));
        middles.put("cz", new Vector2(-0.64, -1.09));
        middles.put("de", new Vector2(-2.00, -0.73));
        middles.put("dk", new Vector2(-2.06, +1.50));
        middles.put("ee", new Vector2(+0.98, +2.45));
        middles.put("es", new Vector2(-6.65, -3.85));
        middles.put("fi", new Vector2(+0.71, +5.20));
        middles.put("fr", new Vector2(-4.15, -1.89));
        middles.put("gb", new Vector2(-4.88, +1.23));
        middles.put("gr", new Vector2(+1.79, -5.16));
        middles.put("hr", new Vector2(-0.50, -2.95));
        middles.put("hu", new Vector2(+0.15, -2.02));
        middles.put("ie", new Vector2(-6.08, +1.54));
        middles.put("it", new Vector2(-1.44, -4.27));
        middles.put("lt", new Vector2(+1.00, +1.16));
        middles.put("lu", new Vector2(-3.18, -0.90));
        middles.put("lv", new Vector2(+1.07, +1.85));
        middles.put("ne", new Vector2(-3.17, +0.04));
        middles.put("pl", new Vector2(+0.06, -0.25));
        middles.put("pt", new Vector2(-8.33, -3.78));
        middles.put("ro", new Vector2(+1.86, -2.65));
        middles.put("se", new Vector2(-0.75, +3.84));
        middles.put("si", new Vector2(-1.00, -2.56));
        middles.put("sk", new Vector2(+0.18, -1.43));
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
