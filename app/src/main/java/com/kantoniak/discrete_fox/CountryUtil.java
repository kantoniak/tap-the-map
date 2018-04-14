package com.kantoniak.discrete_fox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CountryUtil {
    public HashMap<String, String> mapp;

    public CountryUtil() {
        mapp = new HashMap<>();
        List<String> shorty = Arrays.asList("at", "be", "bg", "cy", "cz", "de", "dk", "ee", "es", "fi", "fr", "gb", "gr", "hr", "hu", "ie", "it", "lt", "lu", "lv", "ne", "pl", "pt", "ro", "se", "si", "sk");
        List<String> longy = Arrays.asList(
                "Austria", "Belgium", "Bulgaria", "Cyprus", "Czech Republic", "Germany (until 1990 former territory of the FRG)",
        "Denmark", "Estonia", "Spain", "Finland", "France", "United Kingdom", "Greece",
        "Croatia", "Hungary", "Ireland", "Italy", "Lithuania", "Luxembourg", "Latvia", "Netherlands",
        "Poland", "Portugal", "Romania", "Sweden", "Slovenia", "Slovakia");
        for (int i = 0; i < shorty.size(); i++) {
            mapp.put(shorty.get(i), longy.get(i));
        }
    }
    public String convert(String abbr) {
        return mapp.get(abbr);
    }
}
