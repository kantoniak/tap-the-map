package com.kantoniak.discrete_fox.communication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class ContentObject {
    private int moffset;

    /**
     * Setup PupulationWithNoToilet object and populate the hashmap in proper way.
     *
     * @param dimensions dimensions retrieved from Eurostat
     * @param values exact values retrieved from Eurostat
     * @param id list of dimension ids
     * @param size list of dimensions sizes
     */
    ContentObject(JSONObject dimensions, JSONObject values, JSONArray id, JSONArray size, int offset) {
        moffset = offset;
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
                // foreach year
                Iterator<String> tempYear = indexYear.keys();
                while (tempYear.hasNext()) {
                    String key2 = tempYear.next();
                    int idx2 = indexYear.getInt(key2);

                    int key = (idx1 * size.getInt(moffset) + idx2);

                    double value;
                    try {
                        value = values.getDouble(String.valueOf(key));
                    } catch (Exception e) {
                        value = -1;
                    }
                    String label = labelCountry.getString(key1);

                    // Place data into hashmap
                    HashMap<Integer, Double> country = data.get(label);
                    if (country == null) {
                        country = new HashMap<>();
                    }
                    country.put(Integer.valueOf(key2), value);
                    data.put(label, country);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, HashMap<Integer, Double>> data;

    /**
     * Retrieve data from hashmap in convenient way.
     *
     * @param country which country we need data for
     * @param year which year we need data for
     * @return data for given country and year
     */
    public String getValueForCountry(String country, int year) {
        Double value = data.get(country).get(year);
        String res = "No Data";
        if (value != -1.0) {
            res = value.toString() + "%";
        }
        return country + " " + String.valueOf(year) + ": " + res;
    }

    public HashMap<String, HashMap<Integer, Double>> getHashMap() {
        return data;
    }
}
