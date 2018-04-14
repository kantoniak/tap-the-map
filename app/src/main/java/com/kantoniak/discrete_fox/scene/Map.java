package com.kantoniak.discrete_fox.scene;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Map {

    public static List<String> COUNTRY_CODES = Arrays.asList("at", "be", "bg", "cy", "cz", "de", "dk", "ee", "es", "fi", "fr", "gb", "gr", "hr", "hu", "ie", "it", "lt", "lu", "lv", "ne", "pl", "pt", "ro", "se", "si", "sk");
    HashMap<String, Country> countries = new HashMap<>();

    public void addCountry(Country country) {
        countries.put(country.getCode(), country);
    }

    public HashMap<String, Country> getCountries() {
        return countries;
    }
}
