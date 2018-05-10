package pl.gov.stat.tapthemap.scene;

import android.support.annotation.Nullable;

import pl.gov.stat.tapthemap.Country;

import org.rajawali3d.math.vector.Vector2;

import java.util.Collections;
import java.util.HashMap;

/**
 * Class representing the map.
 */
public class Map {
    private HashMap<Country, CountryInstance> countries = new HashMap<>();

    private static final java.util.Map<Country, Vector2> countryMiddles;

    static {
        java.util.Map<Country, Vector2> middles = new HashMap<>();
        middles.put(Country.Builder.fromEuCode("fi"), new Vector2(1.591, 2.102));
        middles.put(Country.Builder.fromEuCode("ee"), new Vector2(1.563, 1.565));
        middles.put(Country.Builder.fromEuCode("no"), new Vector2(1.121, 2.14));
        middles.put(Country.Builder.fromEuCode("se"), new Vector2(1.211, 1.968));
        middles.put(Country.Builder.fromEuCode("dk"), new Vector2(0.923, 1.362));
        middles.put(Country.Builder.fromEuCode("lv"), new Vector2(1.536, 1.426));
        middles.put(Country.Builder.fromEuCode("lt"), new Vector2(1.493, 1.316));
        middles.put(Country.Builder.fromEuCode("ru"), new Vector2(1.394, 1.27));
        middles.put(Country.Builder.fromEuCode("sk"), new Vector2(1.311, 0.874));
        middles.put(Country.Builder.fromEuCode("by"), new Vector2(1.663, 1.192));
        middles.put(Country.Builder.fromEuCode("ua"), new Vector2(1.798, 0.899));
        middles.put(Country.Builder.fromEuCode("md"), new Vector2(1.679, 0.783));
        middles.put(Country.Builder.fromEuCode("cy"), new Vector2(1.871, 0.119));
        middles.put(Country.Builder.fromEuCode("ro"), new Vector2(1.536, 0.704));
        middles.put(Country.Builder.fromEuCode("tr"), new Vector2(1.956, 0.327));
        middles.put(Country.Builder.fromEuCode("el"), new Vector2(1.447, 0.33));
        middles.put(Country.Builder.fromEuCode("bg"), new Vector2(1.548, 0.526));
        middles.put(Country.Builder.fromEuCode("mk"), new Vector2(1.404, 0.461));
        middles.put(Country.Builder.fromEuCode("al"), new Vector2(1.336, 0.435));
        middles.put(Country.Builder.fromEuCode("hu"), new Vector2(1.311, 0.779));
        middles.put(Country.Builder.fromEuCode("rs"), new Vector2(1.366, 0.61));
        middles.put(Country.Builder.fromEuCode("xk"), new Vector2(1.369, 0.515));
        middles.put(Country.Builder.fromEuCode("me"), new Vector2(1.303, 0.527));
        middles.put(Country.Builder.fromEuCode("ba"), new Vector2(1.243, 0.605));
        middles.put(Country.Builder.fromEuCode("hr"), new Vector2(1.186, 0.655));
        middles.put(Country.Builder.fromEuCode("si"), new Vector2(1.122, 0.716));
        middles.put(Country.Builder.fromEuCode("mt"), new Vector2(1.105, 0.164));
        middles.put(Country.Builder.fromEuCode("cz"), new Vector2(1.141, 0.938));
        middles.put(Country.Builder.fromEuCode("at"), new Vector2(1.098, 0.805));
        middles.put(Country.Builder.fromEuCode("it"), new Vector2(1.007, 0.539));
        middles.put(Country.Builder.fromEuCode("fr"), new Vector2(0.621, 0.751));
        middles.put(Country.Builder.fromEuCode("ie"), new Vector2(0.186, 1.164));
        middles.put(Country.Builder.fromEuCode("uk"), new Vector2(0.398, 1.237));
        middles.put(Country.Builder.fromEuCode("pt"), new Vector2(0.192, 0.361));
        middles.put(Country.Builder.fromEuCode("es"), new Vector2(0.375, 0.4));
        middles.put(Country.Builder.fromEuCode("lu"), new Vector2(0.767, 0.94));
        middles.put(Country.Builder.fromEuCode("be"), new Vector2(0.703, 0.998));
        middles.put(Country.Builder.fromEuCode("nl"), new Vector2(0.747, 1.099));
        middles.put(Country.Builder.fromEuCode("de"), new Vector2(0.943, 1.031));
        middles.put(Country.Builder.fromEuCode("pl"), new Vector2(1.309, 1.096));
        middles.put(Country.Builder.fromEuCode("ch"), new Vector2(0.852, 0.758));
        countryMiddles = Collections.unmodifiableMap(middles);
    }

    /**
     * Add country instance to the map.
     *
     * @param countryInstance Country instance that will be added
     */
    public void addCountryInstance(CountryInstance countryInstance) {
        countries.put(countryInstance.getCountry(), countryInstance);
    }

    /**
     * Get hashmap of the countries.
     *
     * @return Hashmap of the countries
     */
    public HashMap<Country, CountryInstance> getCountries() {
        return countries;
    }

    /**
     * Reset state of all country instances.
     */
    public void reset() {
        getCountries().forEach((country, instance) -> instance.resetState());
    }

    /**
     * Set the visibility state.
     *
     * @param visible New visibility state
     */
    public void setVisible(boolean visible) {
        getCountries().forEach((country, instance) -> instance.setVisible(visible));
    }

    /**
     * Get country.
     *
     * @param country Which country
     * @return Country instance of the given country
     */
    public @Nullable
    CountryInstance getCountry(Country country) {
        return getCountries().get(country);
    }

    /**
     * Enable country.
     *
     * @param country Which country
     */
    public void enableCountry(Country country) {
        CountryInstance countryInstance = getCountries().get(country);
        if (countryInstance == null) {
            return;
        }
        countryInstance.setDisabled(false);
    }

    /**
     * Set colors.
     *
     * @param minColor Minimum color
     * @param maxColor Maximum color
     */
    public void setColors(int minColor, int maxColor) {
        getCountries().forEach((country, instance) -> instance.setColors(minColor, maxColor));
    }

    /**
     * Get center of the country.
     *
     * @param country Which country
     * @return Position of the center
     */
    public Vector2 getCountryMiddle(Country country) {
        return countryMiddles.get(country);
    }
}
