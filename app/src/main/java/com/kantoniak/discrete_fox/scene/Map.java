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

    public static List<String> COUNTRY_CODES = CountryUtil.getEurostatCodes();
    HashMap<String, Country> countries = new HashMap<>();

    private static final java.util.Map<String, Vector2> countryMiddles;

    static {
        java.util.Map<String, Vector2> middles = new HashMap<>();
        middles.put("fi", new Vector2(1.591, 2.102));
        middles.put("ee", new Vector2(1.563, 1.565));
        middles.put("no", new Vector2(1.121, 2.14));
        middles.put("se", new Vector2(1.211, 1.968));
        middles.put("dk", new Vector2(0.923, 1.362));
        middles.put("lv", new Vector2(1.536, 1.426));
        middles.put("lt", new Vector2(1.493, 1.316));
        middles.put("ru", new Vector2(1.394, 1.27));
        middles.put("sk", new Vector2(1.311, 0.874));
        middles.put("by", new Vector2(1.663, 1.192));
        middles.put("ua", new Vector2(1.798, 0.899));
        middles.put("md", new Vector2(1.679, 0.783));
        middles.put("cy", new Vector2(1.871, 0.119));
        middles.put("ro", new Vector2(1.536, 0.704));
        middles.put("tr", new Vector2(1.956, 0.327));
        middles.put("el", new Vector2(1.447, 0.33));
        middles.put("bg", new Vector2(1.548, 0.526));
        middles.put("mk", new Vector2(1.404, 0.461));
        middles.put("al", new Vector2(1.336, 0.435));
        middles.put("hu", new Vector2(1.311, 0.779));
        middles.put("rs", new Vector2(1.366, 0.61));
        middles.put("xk", new Vector2(1.369, 0.515));
        middles.put("me", new Vector2(1.303, 0.527));
        middles.put("ba", new Vector2(1.243, 0.605));
        middles.put("hr", new Vector2(1.186, 0.655));
        middles.put("si", new Vector2(1.122, 0.716));
        middles.put("mt", new Vector2(1.105, 0.164));
        middles.put("cz", new Vector2(1.141, 0.938));
        middles.put("at", new Vector2(1.098, 0.805));
        middles.put("it", new Vector2(1.007, 0.539));
        middles.put("fr", new Vector2(0.621, 0.751));
        middles.put("ie", new Vector2(0.186, 1.164));
        middles.put("uk", new Vector2(0.398, 1.237));
        middles.put("pt", new Vector2(0.192, 0.361));
        middles.put("es", new Vector2(0.375, 0.4));
        middles.put("lu", new Vector2(0.767, 0.94));
        middles.put("be", new Vector2(0.703, 0.998));
        middles.put("nl", new Vector2(0.747, 1.099));
        middles.put("de", new Vector2(0.943, 1.031));
        middles.put("pl", new Vector2(1.309, 1.096));
        middles.put("ch", new Vector2(0.852, 0.758));
        countryMiddles = Collections.unmodifiableMap(middles);
    }

    public void addCountry(Country country) {
        countries.put(country.getCode(), country);
    }

    public HashMap<String, Country> getCountries() {
        return countries;
    }

    public void reset() {
        for (java.util.Map.Entry<String, Country> entry: getCountries().entrySet()) {
            Country country = entry.getValue();
            country.resetState();
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
