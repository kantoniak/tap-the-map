package com.kantoniak.discrete_fox;

import android.content.res.Resources;
import android.support.annotation.VisibleForTesting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Identifies single country. Can act as an identifier.
 */
public class Country {

    // @formatter:off
    @VisibleForTesting
    public static final Set<String> EU_CODES = new HashSet<>(Arrays.asList(
            "al", "at", "ba", "be", "bg", "by", "ch", "cy", "cz", "de", "dk", "ee", "el", "es",
            "fi", "fr", "hr", "hu", "ie", "it", "lt", "lu", "lv", "md", "me", "mk", "mt", "nl",
            "no", "pl", "pt", "ro", "rs", "ru", "se", "si", "sk", "tr", "ua", "uk", "xk"));
    // @formatter:on

    /**
     * Code as given by EU, then ISO if the former not present.
     */
    private final String euCode;

    private Country(String euCode) {
        this.euCode = euCode;
    }

    public String getEuCode() {
        return euCode;
    }

    private int getLocalizedStringId(Resources resources) {
        return resources.getIdentifier("country_name_" + euCode, "string", BuildConfig.APPLICATION_ID);
    }

    public String getLocalizedName(Resources resources) {
        return resources.getString(getLocalizedStringId(resources));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(euCode, country.euCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(euCode);
    }

    public static class Builder {

        public static Country fromEuCode(String code) {
            if (!EU_CODES.contains(code)) {
                throw new IllegalArgumentException("Country inexistent/not supported");
            }
            return new Country(code);
        }
    }
}
