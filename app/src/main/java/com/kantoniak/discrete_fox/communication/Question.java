package com.kantoniak.discrete_fox.communication;

import com.kantoniak.discrete_fox.communication.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Question {
    private final String mlink;
    private HashMap<String, Level> ans;
    private HashMap<String, Double> ansDouble;

    double midThres;
    double highThres;

    Question(String link, HashMap<String, HashMap<Integer, Double>> data, int year) {
        mlink = link;
        ansDouble = new HashMap<>();
        ArrayList<Double> valueList = new ArrayList<>();
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            HashMap<Integer, Double> hmval = (HashMap<Integer, Double>)pair.getValue();
            double val = hmval.get(year);
            valueList.add(val);
            ansDouble.put((String)pair.getKey(), val);
            it.remove(); // avoids a ConcurrentModificationException
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

    void createAnswers(HashMap<String, Double> data) {
        ans = new HashMap<>();
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Double val = (Double)pair.getValue();
            Level l = Level.LOW;
            if (val > highThres) {
                l = Level.HIGH;
            } else if (val > midThres) {
                l = Level.MEDIUM;
            }
            ans.put((String)pair.getKey(), l);
        }
    }

    void setThreshold(double mid, double high) {
        midThres = mid;
        highThres = high;
    }
}
