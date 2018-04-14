package com.kantoniak.discrete_fox.scene;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Map {

    public static List<String> COUNTRY_CODES = Arrays.asList("at", "be", "bg", "cy", "cz", "de", "dk", "ee", "es", "fi", "fr", "gb", "gr", "hr", "hu", "ie", "it", "lt", "lu", "lv", "ne", "pl", "pt", "ro", "se", "si", "sk");
    List<Country> countries = new LinkedList<>();

    public void addCountry(Country country) {
        countries.add(country);
    }

    public List<Country> getCountries() {
        return countries;
    }
}
