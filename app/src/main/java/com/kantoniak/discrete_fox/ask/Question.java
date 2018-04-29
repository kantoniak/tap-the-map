package com.kantoniak.discrete_fox.ask;

import android.support.v4.graphics.ColorUtils;

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
    private HashMap<String, Integer> ans;
    private HashMap<String, Double> ansDouble;
    public static final int NUMBEROFCOUNTRIES = 5;

    double midThres;
    double highThres;
    String mdesc;
    List<String> mcountries; // FIXME(kedzior): Should not use ISO code
    String munit;
    QuestionCategory mcategory;

    Question(String link, HashMap<String, Double> data, String desc, List<String> countries, String unit, QuestionCategory category, int multiplier) {
        mcategory = category;
        mcountries = countries;
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
            ansDouble.put((String)pair.getKey(), val);
        }
        createThresholds(valueList);
        createAnswers(ansDouble);
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

    void createAnswers(HashMap<String, Double> data) {
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
            ans.put((String)pair.getKey(), l);
        }
    }

    void setThreshold(double mid, double high) {
        midThres = Math.round(mid * 100.0) / 100.0;
        highThres = Math.round(high * 100.0) / 100.0;
    }

    public Integer getCorrectAnswer(String country) {
        return ans.get(country);
    }

    public List<String> getCountries() {
        return mcountries;
    }

    public String getMinLabel() {
        if (munit.equals("%")) {
            return "<" + midThres + munit;
        } else {
            return "<" + UnitFormatter.formatAsUnit((long)midThres, UnitSystem.SI, munit);
        }
    }

    public String getMidLabel() {
        if (munit.equals("%")) {
            return midThres + munit + " - " + highThres + munit;
        } else {
            return UnitFormatter.formatAsUnit((long)midThres, UnitSystem.SI, munit) + " - " + UnitFormatter.formatAsUnit((long)highThres, UnitSystem.SI, munit);
        }
    }

    public String getMaxLabel() {
        if (munit.equals("%")) {
            return ">" + highThres + munit;
        } else {
            return ">" + UnitFormatter.formatAsUnit((long)highThres, UnitSystem.SI, munit);
        }
    }

    public HashMap<String, Double> getAnsDouble() {
        return ansDouble;
    }

    public String getUnit() {
        return munit;
    }

    public List<Answer> getAnswers() {
        List<Answer> list = new ArrayList<>();
        for (String code: mcountries) {
            String fullName = CountryUtil.eurostatToName(code);
            double value = ansDouble.get(fullName);
            String valuePresented;
            if (munit.equals("%")) {
                valuePresented = String.format("%.2f", value) + munit;
            } else {
                valuePresented = UnitFormatter.formatAsUnit((long) value, UnitSystem.SI, munit);
            }
            int color = ColorUtils.blendARGB(mcategory.getMinColor(), mcategory.getMaxColor(), (ans.get(fullName)-1)*0.5f);
            Answer answer = new Answer(fullName, valuePresented, value, color);
            list.add(answer);
        }
        return list;
    }
}
