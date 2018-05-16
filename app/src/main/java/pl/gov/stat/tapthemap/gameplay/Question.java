package pl.gov.stat.tapthemap.gameplay;

import android.support.v4.graphics.ColorUtils;

import com.trivago.triava.util.UnitFormatter;
import com.trivago.triava.util.UnitSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pl.gov.stat.tapthemap.BuildConfig;
import pl.gov.stat.tapthemap.Country;

/**
 * Class representing single question.
 */
public class Question {
    private final String mLink;
    private HashMap<Country, Integer> ans;
    private HashMap<Country, Double> ansDouble;

    private double midThres;
    private double highThres;
    private String mdesc;
    private List<Country> mCountries;
    private List<Country> mRemainingCountries;
    private String mUnit;
    private QuestionCategory mCategory;
    private HashMap<Country, Boolean> isCorrectAnswer;

    Question(String link, HashMap<String, Double> data, String desc, String unit, QuestionCategory category, int multiplier) {
        mCategory = category;
        mdesc = desc;
        mLink = link;
        mUnit = unit;
        ansDouble = new HashMap<>();
        isCorrectAnswer = new HashMap<>();
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
        if (BuildConfig.SEEDENABLED) {
            Collections.shuffle(availableCountries, new Random(BuildConfig.SEEDVALUE));
        } else {
            Collections.shuffle(availableCountries);
        }

        mCountries = availableCountries.subList(0, Gameplay.Settings.COUNTRIES_PER_QUESTION);
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
        return mCategory;
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
        return mCountries;
    }

    /**
     * Get label for low value.
     *
     * @return Label
     */
    public String getMinLabel() {
        switch (mUnit) {
            case "%":
                return "<" + midThres + mUnit;
            case "D":
                return "<" + midThres;
            default:
                return "<" + UnitFormatter.formatAsUnit((long) midThres, UnitSystem.SI, mUnit);
        }
    }

    /**
     * Get label for mid value.
     *
     * @return Label
     */
    public String getMidLabel() {
        switch (mUnit) {
            case "%":
                return midThres + mUnit + " - " + highThres + mUnit;
            case "D":
                return midThres + " - " + highThres;
            default:
                return UnitFormatter.formatAsUnit((long) midThres, UnitSystem.SI, mUnit) + " - " + UnitFormatter.formatAsUnit((long) highThres, UnitSystem.SI, mUnit);
        }
    }

    /**
     * Get label for high value.
     *
     * @return Label
     */
    public String getMaxLabel() {
        switch (mUnit) {
            case "%":
                return ">" + highThres + mUnit;
            case "D":
                return ">" + highThres;
            default:
                return ">" + UnitFormatter.formatAsUnit((long) highThres, UnitSystem.SI, mUnit);
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
        return mUnit;
    }

    /**
     * Get answers.
     *
     * @return List of answers
     */
    public List<Answer> getAnswers() {
        List<Answer> list = new ArrayList<>();
        for (Country country : mCountries) {
            double value = 0.0;
            try {
                value = ansDouble.get(country);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String valuePresented;
            switch (mUnit) {
                case "%":
                    valuePresented = String.format("%.2f", value) + mUnit;
                    break;
                case "D":
                    valuePresented = String.format("%.2f", value);
                    break;
                default:
                    valuePresented = UnitFormatter.formatAsUnit((long) value, UnitSystem.SI, mUnit);
                    break;
            }
            int color = ColorUtils.blendARGB(mCategory.getMinColor(), mCategory.getMaxColor(), (ans.get(country) - 1) * 0.5f);
            Answer answer = new Answer(this, country, valuePresented, value, color);
            list.add(answer);
        }
        return list;
    }

    /**
     * Set isCorrectAnswer hashmap.
     *
     * @param country   Country
     * @param isCorrect Is the answer for country correct or not
     */
    public void setAnswer(Country country, boolean isCorrect) {
        isCorrectAnswer.put(country, isCorrect);
    }

    /**
     * Check whether the answer for country is correct.
     *
     * @param country Country
     * @return True if answer is correct
     */
    public boolean getIsCorrectAnswer(Country country) {
        return isCorrectAnswer.get(country);
    }
}
