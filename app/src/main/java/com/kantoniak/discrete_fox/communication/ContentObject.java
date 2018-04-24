package com.kantoniak.discrete_fox.communication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class ContentObject {
    /**
     * Setup PupulationWithNoToilet object and populate the hashmap in proper way.
     *
     * @param dimensions dimensions retrieved from Eurostat
     * @param values exact values retrieved from Eurostat
     * @param id list of dimension ids
     * @param size list of dimensions sizes
     */
    ContentObject(JSONObject dimensions, JSONObject values, JSONArray id, JSONArray size) {
        data = new HashMap<>();
        try {
            JSONObject indexCountry = dimensions.getJSONObject("geo").getJSONObject("category").getJSONObject("index");
            JSONObject indexYear = dimensions.getJSONObject("time").getJSONObject("category").getJSONObject("index");
            JSONObject labelCountry = dimensions.getJSONObject("geo").getJSONObject("category").getJSONObject("label");
            // foreach country
            Iterator<String> tempCountry = indexCountry.keys();
            while (tempCountry.hasNext()) {
                String key1 = tempCountry.next();
                int idx1 = indexCountry.getInt(key1);
                Double value = null;
                int backup = 2;
                while (value == null && backup > -1) {
                    try {
                        value = values.getDouble(String.valueOf(idx1 * 3 + backup));
                    } catch (Exception e) {
                        value = null;
                    }
                    backup--;
                }
                if (value != null) {
                    String label = labelCountry.getString(key1);
                    data.put(label, value);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Double> data;

    /**
     * Retrieve data from hashmap in convenient way.
     *
     * @param country which country we need data for
     * @return data for given country and year
     */
    public String getValueForCountry(String country) {
        Double value = data.get(country);
        String res = "No Data";
        if (value != -1.0) {
            res = value.toString() + "%";
        }
        return country + ": " + res;
    }

    public HashMap<String, Double> getHashMap() {
        return data;
    }
}
