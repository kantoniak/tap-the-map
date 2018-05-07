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

    /**
     * Creates object from two letter Eurostat code.
     * @param euCode two letter Eurostat code
     */
    private Country(String euCode) {
        this.euCode = euCode;
    }

    /**
     * Returns Eurostat code.
     * @return Eurostat code
     */
    public String getEuCode() {
        return euCode;
    }

    /**
     * Returns the stringID from the resources.
     * @param resources Resources of the application
     * @return Localized stringID
     */
    private int getLocalizedStringId(Resources resources) {
        return resources.getIdentifier("country_name_" + euCode, "string", BuildConfig.APPLICATION_ID);
    }

    /**
     * Returns the localized name.
     * @param resources Resources of the application
     * @return Localized name
     */
    public String getLocalizedName(Resources resources) {
        return resources.getString(getLocalizedStringId(resources));
    }

    /**
     * Compare two objects of class Country.
     * @param o The other object
     * @return True if the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(euCode, country.euCode);
    }

    /**
     * Returns hash code of the code.
     * @return Hash code of the code
     */
    @Override
    public int hashCode() {
        return Objects.hash(euCode);
    }

    /**
     * Helper class for creating Country objects.
     */
    public static class Builder {

        /**
         * Create object from given Eurostat code.
         * @param code Eurostat code
         * @return Created object
         */
        public static Country fromEuCode(String code) {
            if (!EU_CODES.contains(code)) {
                throw new IllegalArgumentException("Country inexistent/not supported");
            }
            return new Country(code);
        }
    }
}
