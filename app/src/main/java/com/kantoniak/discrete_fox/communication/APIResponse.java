package com.kantoniak.discrete_fox.communication;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class that represents parsed json retrieved from Eurostat API.
 */
public class APIResponse {

    /**
     * Fields for general data.
     */
    private String mVersion;
    private String mLabel;
    private String mSource;
    private String mUpdated;
    /**
     * Object that keeps the specific data.
     */
    private ContentObject mcontent;

    APIResponse(String version, String label, String href, String source, String updated, String status, JSONObject extension, JSONObject value, JSONObject dimension, JSONArray id, JSONArray size) {
        mVersion = version;
        mLabel = label;
        mSource = source;
        mUpdated = updated;

        mcontent = new ContentObject(dimension, value);
    }

    public String getVersion() {
        return mVersion;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getSource() {
        return mSource;
    }

    public String getUpdated() {
        return mUpdated;
    }

    public ContentObject getContent() {
        return mcontent;
    }
}
