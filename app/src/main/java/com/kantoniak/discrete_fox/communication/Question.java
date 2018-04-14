package com.kantoniak.discrete_fox.communication;

import com.kantoniak.discrete_fox.scene.Country;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Question {
    private final String mlink;
    private HashMap<String, Integer> ans;
    private HashMap<String, Double> ansDouble;
    public static final int NUMBEROFCOUNTRIES = 5;

    double midThres;
    double highThres;
    String mdesc;
    int mminColor;
    int mmaxColor;
    String[] mcountries;

    Question(String link, HashMap<String, HashMap<Integer, Double>> data, int year, String desc, int minColor, int maxColor, String[] countries) {
        mcountries = countries;
        mdesc = desc;
        mlink = link;
        mminColor = minColor;
        mmaxColor = maxColor;
        ansDouble = new HashMap<>();
        ArrayList<Double> valueList = new ArrayList<>();
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            HashMap<Integer, Double> hmval = (HashMap<Integer, Double>)pair.getValue();
            double val = hmval.get(year);
            valueList.add(val);
            ansDouble.put((String)pair.getKey(), val);
            //it.remove(); // avoids a ConcurrentModificationException
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
        midThres = mid;
        highThres = high;
    }

    public Integer getCorrectAnswer(String country) {
        return ans.get(country);
    }

    public String getHighLabel() {
        return String.format("%.2f", highThres) + " <";
    }

    public String getMidLabel() {
        return String.format("%.2f", midThres) + " - " + String.format("%.2f", highThres);
    }

    public String getLowLabel() {
        return "< " + String.format("%.2f", midThres);
    }

    public int getMminColor() {
        return mminColor;
    }

    public int getMmaxColor() {
        return mmaxColor;
    }

    public String[] getCountries() {
        return mcountries;
    }
}
