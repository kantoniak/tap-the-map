package com.kantoniak.discrete_fox.communication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public abstract class ContentObject {
    public void setup(JSONObject dimensions, JSONObject values, JSONArray id, JSONArray size) {}
    public HashMap<String, HashMap<Integer, Double>> getHashMap() { return null;}
}
