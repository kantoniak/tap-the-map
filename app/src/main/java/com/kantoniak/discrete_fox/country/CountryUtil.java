package com.kantoniak.discrete_fox.country;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CountryUtil {
    private static int COUNTRYCOUNT = 28;
    private static HashMap<String, String> codeToName;

    private static List<String> eurostatCodes = Arrays.asList("at", "be", "bg", "cy", "cz", "de",
            "dk", "ee", "es", "fi", "fr", "uk", "el",
            "hr", "hu", "ie", "it", "lt", "lu", "lv", "nl",
            "pl", "pt", "ro", "se", "si", "sk", "mt");

    private static List<String> fullNames = Arrays.asList(
            "Austria", "Belgium", "Bulgaria", "Cyprus", "Czech Republic", "Germany",
            "Denmark", "Estonia", "Spain", "Finland", "France", "United Kingdom", "Greece",
            "Croatia", "Hungary", "Ireland", "Italy", "Lithuania", "Luxembourg", "Latvia", "Netherlands",
            "Poland", "Portugal", "Romania", "Sweden", "Slovenia", "Slovakia", "Malta");

    static {
        assert (eurostatCodes.size() == COUNTRYCOUNT);
        assert (fullNames.size() == COUNTRYCOUNT);

        codeToName = new HashMap<>();

        for (int i = 0; i < COUNTRYCOUNT; i++) {
            codeToName.put(eurostatCodes.get(i), fullNames.get(i));
        }
    }

    public static String codeToName(String abbr) {
        return codeToName.get(abbr);
    }

    public static List<String> getEurostatCodes() {
        return eurostatCodes;
    }
}
