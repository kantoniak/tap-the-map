package com.kantoniak.discrete_fox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CountryUtil {
    private static int COUNTRYCOUNT = 27;
    private static HashMap<String, String> isoCodesToNames;
    private static HashMap<String, String> isoCodesToEurostat;
    private static HashMap<String, String> eurostatCodesToEurostat;
    private static HashMap<String, String> eurostatCodesToNames;

    private static List<String> iso = Arrays.asList("at", "be", "bg", "cy", "cz", "de",
            "dk", "ee", "es", "fi", "fr", "gb", "gr",
            "hr", "hu", "ie", "it", "lt", "lu", "lv", "nl",
            "pl", "pt", "ro", "se", "si", "sk");
    private static List<String> eurostatCodes = Arrays.asList("at", "be", "bg", "cy", "cz", "de",
            "dk", "ee", "es", "fi", "fr", "uk", "el",
            "hr", "hu", "ie", "it", "lt", "lu", "lv", "nl",
            "pl", "pt", "ro", "se", "si", "sk");
    private static List<String> fullNames = Arrays.asList(
            "Austria", "Belgium", "Bulgaria", "Cyprus", "Czech Republic", "Germany",
            "Denmark", "Estonia", "Spain", "Finland", "France", "United Kingdom", "Greece",
            "Croatia", "Hungary", "Ireland", "Italy", "Lithuania", "Luxembourg", "Latvia", "Netherlands",
            "Poland", "Portugal", "Romania", "Sweden", "Slovenia", "Slovakia");
    private static List<String> eurostatFull = Arrays.asList(
            "Austria", "Belgium", "Bulgaria", "Cyprus", "Czech Republic", "Germany (until 1990 former territory of the FRG)",
            "Denmark", "Estonia", "Spain", "Finland", "France", "United Kingdom", "Greece",
            "Croatia", "Hungary", "Ireland", "Italy", "Lithuania", "Luxembourg", "Latvia", "Netherlands",
            "Poland", "Portugal", "Romania", "Sweden", "Slovenia", "Slovakia");

    static {
        assert (iso.size() == COUNTRYCOUNT);
        assert (eurostatCodes.size() == COUNTRYCOUNT);
        assert (fullNames.size() == COUNTRYCOUNT);
        assert (eurostatFull.size() == COUNTRYCOUNT);

        isoCodesToNames = new HashMap<>();
        isoCodesToEurostat = new HashMap<>();
        eurostatCodesToNames = new HashMap<>();
        eurostatCodesToEurostat = new HashMap<>();

        for (int i = 0; i < COUNTRYCOUNT; i++) {
            isoCodesToNames.put(iso.get(i), fullNames.get(i));
            isoCodesToEurostat.put(iso.get(i), eurostatFull.get(i));
            eurostatCodesToNames.put(eurostatCodes.get(i), fullNames.get(i));
            eurostatCodesToEurostat.put(eurostatCodes.get(i), eurostatFull.get(i));
        }
    }

    public static String isoToName(String abbr) {
        return isoCodesToNames.get(abbr);
    }

    public static String isoToEurostat(String abbr) {
        return isoCodesToEurostat.get(abbr);
    }

    public static String eurostatToName(String abbr) {
        return eurostatCodesToNames.get(abbr);
    }

    public static String eurostatToEurostat(String abbr) {
        return eurostatCodesToEurostat.get(abbr);
    }

    public static List<String> getIsoCodes() {
        return iso;
    }
}
