package com.kantoniak.discrete_fox.ask;

import android.support.v4.graphics.ColorUtils;

import com.kantoniak.discrete_fox.country.Country;
import com.kantoniak.discrete_fox.country.CountryUtil;
import com.trivago.triava.util.UnitFormatter;
import com.trivago.triava.util.UnitSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Question {
    private final String mlink;
    private HashMap<Country, Integer> ans;
    private HashMap<Country, Double> ansDouble;
    public static final int NUMBEROFCOUNTRIES = 5;
    /**
     * List of the country codes.
     */
    private static final List<String> COUNTRY_CODES = CountryUtil.getEurostatCodes();

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
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Double val = ((Double)pair.getValue()) * multiplier;
            valueList.add(val);
            Country country = Country.Builder.fromEuCode((String)pair.getKey());
            ansDouble.put(country, val);
        }
        setCountries();
        createThresholds(valueList);
        createAnswers(ansDouble);
    }

    /**
     * Checks whether we can use this particular permutation.
     * @return whether we can use given permutation
     */
    private boolean isShuffleLegit() {
        boolean legit = true;
        for (int j = 0; j < NUMBEROFCOUNTRIES; j++) {

            if (COUNTRY_CODES.get(j) == null) {
                legit = false;
            }
        }
        return legit;
    }

    private void setCountries() {
        List<Country> availableCountries = new ArrayList<>(ansDouble.keySet());
        assert(availableCountries.size() > NUMBEROFCOUNTRIES);
        Collections.shuffle(availableCountries);

        mcountries = availableCountries.subList(0, NUMBEROFCOUNTRIES);
    }

    void createThresholds(ArrayList<Double> valueList) {
        double mid = 0;
        double high = 0;

        double mini = Collections.min(valueList);
        double maxi = Collections.max(valueList);

        mid = mini + (maxi - mini) / 3;
        high = mid + (maxi - mini) / 3;

        setThreshold(mid, high);
    }

    public String getDesc() {
        return mdesc;
    }

    public QuestionCategory getCategory() {
        return mcategory;
    }

    void createAnswers(HashMap<Country, Double> data) {
        ans = new HashMap<>();
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Double val = (Double)pair.getValue();
            int l = 1;
            if (val > highThres) {
                l = 3;
            } else if (val > midThres) {
                l = 2;
            }
            ans.put((Country)pair.getKey(), l);
        }
    }

    void setThreshold(double mid, double high) {
        midThres = Math.round(mid * 100.0) / 100.0;
        highThres = Math.round(high * 100.0) / 100.0;
    }

    public Integer getCorrectAnswer(Country country) {
        return ans.get(country);
    }

    public List<Country> getCountries() {
        return mcountries;
    }

    public String getMinLabel() {
        if (munit.equals("%")) {
            return "<" + midThres + munit;
        } else if (munit.equals("D")) {
            return "<" + midThres;
        } else {
            return "<" + UnitFormatter.formatAsUnit((long)midThres, UnitSystem.SI, munit);
        }
    }

    public String getMidLabel() {
        if (munit.equals("%")) {
            return midThres + munit + " - " + highThres + munit;
        } else if (munit.equals("D")) {
            return midThres + " - " + highThres;
        } else {
            return UnitFormatter.formatAsUnit((long)midThres, UnitSystem.SI, munit) + " - " + UnitFormatter.formatAsUnit((long)highThres, UnitSystem.SI, munit);
        }
    }

    public String getMaxLabel() {
        if (munit.equals("%")) {
            return ">" + highThres + munit;
        } else if (munit.equals("D")) {
            return ">" + highThres;
        } else {
            return ">" + UnitFormatter.formatAsUnit((long)highThres, UnitSystem.SI, munit);
        }
    }

    public HashMap<Country, Double> getAnsDouble() {
        return ansDouble;
    }

    public String getUnit() {
        return munit;
    }

    public List<Answer> getAnswers() {
        List<Answer> list = new ArrayList<>();
        for (Country country: mcountries) {
            double value = 0.0;
            try {
                value = ansDouble.get(country);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String valuePresented;
            if (munit.equals("%")) {
                valuePresented = String.format("%.2f", value) + munit;
            } else if (munit.equals("D")) {
                valuePresented = String.format("%.2f", value);
            } else {
                valuePresented = UnitFormatter.formatAsUnit((long) value, UnitSystem.SI, munit);
            }
            int color = ColorUtils.blendARGB(mcategory.getMinColor(), mcategory.getMaxColor(), (ans.get(country)-1)*0.5f);
            Answer answer = new Answer(country, valuePresented, value, color);
            list.add(answer);
        }
        return list;
    }
}
