package pl.gov.stat.tapthemap.gameplay;

import android.support.v4.graphics.ColorUtils;

import pl.gov.stat.tapthemap.Country;
import com.trivago.triava.util.UnitFormatter;
import com.trivago.triava.util.UnitSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing single question.
 */
public class Question {
    private final String mlink;
    private HashMap<Country, Integer> ans;
    private HashMap<Country, Double> ansDouble;

    double midThres;
    double highThres;
    String mdesc;
    List<Country> mcountries;
    String munit;
    QuestionCategory mcategory;

    Question(String link, HashMap<String, Double> data, String desc, String unit, QuestionCategory category, int multiplier) {
        mcategory = category;
        mdesc = desc;
        mlink = link;
        munit = unit;
        ansDouble = new HashMap<>();
        ArrayList<Double> valueList = new ArrayList<>();
        for (Object o : data.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            Double val = ((Double) pair.getValue()) * multiplier;
            valueList.add(val);
            Country country = Country.Builder.fromEuCode((String) pair.getKey());
            ansDouble.put(country, val);
        }
        setCountries();
        createThresholds(valueList);
        createAnswers(ansDouble);
    }

    /**
     * Set countries for question.
     */
    private void setCountries() {
        List<Country> availableCountries = new ArrayList<>(ansDouble.keySet());
        assert (availableCountries.size() > Gameplay.Settings.COUNTRIES_PER_QUESTION);
        Collections.shuffle(availableCountries);

        mcountries = availableCountries.subList(0, Gameplay.Settings.COUNTRIES_PER_QUESTION);
    }

    /**
     * Create thresholds for low, medium and high values.
     *
     * @param valueList List of values
     */
    private void createThresholds(ArrayList<Double> valueList) {
        double mid = 0;
        double high = 0;

        double mini = Collections.min(valueList);
        double maxi = Collections.max(valueList);

        mid = mini + (maxi - mini) / 3;
        high = mid + (maxi - mini) / 3;

        setThreshold(mid, high);
    }

    /**
     * Get description of the question.
     *
     * @return Description of the question
     */
    public String getDesc() {
        return mdesc;
    }

    /**
     * Get category of the question.
     *
     * @return Category of the question
     */
    public QuestionCategory getCategory() {
        return mcategory;
    }

    /**
     * Create answers.
     *
     * @param data Data for each country
     */
    private void createAnswers(HashMap<Country, Double> data) {
        ans = new HashMap<>();
        for (Object o : data.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            Double val = (Double) pair.getValue();
            int l = 1;
            if (val > highThres) {
                l = 3;
            } else if (val > midThres) {
                l = 2;
            }
            ans.put((Country) pair.getKey(), l);
        }
    }

    /**
     * Set thresholds.
     *
     * @param mid  Threshold for medium
     * @param high Threshold for high
     */
    private void setThreshold(double mid, double high) {
        midThres = Math.round(mid * 100.0) / 100.0;
        highThres = Math.round(high * 100.0) / 100.0;
    }

    /**
     * Gets correct answer for given country.
     *
     * @param country Country for which we need correct answer
     * @return Correct answer
     */
    public Integer getCorrectAnswer(Country country) {
        return ans.get(country);
    }

    /**
     * Get countries.
     *
     * @return List of countries
     */
    public List<Country> getCountries() {
        return mcountries;
    }

    /**
     * Get label for low value.
     *
     * @return Label
     */
    public String getMinLabel() {
        switch (munit) {
            case "%":
                return "<" + midThres + munit;
            case "D":
                return "<" + midThres;
            default:
                return "<" + UnitFormatter.formatAsUnit((long) midThres, UnitSystem.SI, munit);
        }
    }

    /**
     * Get label for mid value.
     *
     * @return Label
     */
    public String getMidLabel() {
        switch (munit) {
            case "%":
                return midThres + munit + " - " + highThres + munit;
            case "D":
                return midThres + " - " + highThres;
            default:
                return UnitFormatter.formatAsUnit((long) midThres, UnitSystem.SI, munit) + " - " + UnitFormatter.formatAsUnit((long) highThres, UnitSystem.SI, munit);
        }
    }

    /**
     * Get label for high value.
     *
     * @return Label
     */
    public String getMaxLabel() {
        switch (munit) {
            case "%":
                return ">" + highThres + munit;
            case "D":
                return ">" + highThres;
            default:
                return ">" + UnitFormatter.formatAsUnit((long) highThres, UnitSystem.SI, munit);
        }
    }

    /**
     * Get exact answer (Double).
     *
     * @return Hashmap with exact answers
     */
    public HashMap<Country, Double> getAnsDouble() {
        return ansDouble;
    }

    /**
     * Get base unit of the question.
     *
     * @return Unit
     */
    public String getUnit() {
        return munit;
    }

    /**
     * Get answers.
     *
     * @return List of answers
     */
    public List<Answer> getAnswers() {
        List<Answer> list = new ArrayList<>();
        for (Country country : mcountries) {
            double value = 0.0;
            try {
                value = ansDouble.get(country);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String valuePresented;
            switch (munit) {
                case "%":
                    valuePresented = String.format("%.2f", value) + munit;
                    break;
                case "D":
                    valuePresented = String.format("%.2f", value);
                    break;
                default:
                    valuePresented = UnitFormatter.formatAsUnit((long) value, UnitSystem.SI, munit);
                    break;
            }
            int color = ColorUtils.blendARGB(mcategory.getMinColor(), mcategory.getMaxColor(), (ans.get(country) - 1) * 0.5f);
            Answer answer = new Answer(country, valuePresented, value, color);
            list.add(answer);
        }
        return list;
    }
}
