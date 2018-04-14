package com.kantoniak.discrete_fox.communication;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Class that represents parsed json retrieved from Eurostat API.
 */
public class APIResponse {
    private static final String LINK = "http://appsso.eurostat.ec.europa.eu/nui/show.do?dataset=";

    APIResponse(String version, String label, String href, String source, String updated, String status, JSONObject extension, JSONObject value, JSONObject dimension, JSONArray id, JSONArray size, ContentObject content) {
        mVersion = version;
        mLabel = label;
        try {
            mLink = LINK + extension.getString("datasetId");
        } catch (Exception e) {
            mLink = "";
        }
        mSource = source;
        mUpdated = updated;

        mcontent = content;
        mcontent.setup(dimension, value, id, size);
    }

    public String getVersion() {
        return mVersion;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getLink() {
        return mLink;
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

    /**
     * Fields for general data.
     */
    private String mVersion;
    private String mLabel;
    private String mSource;
    private String mUpdated;
    /**
     * Link to the data presented on the website.
     */
    private String mLink;
    /**
     * Object that keeps the specific data.
     */
    private ContentObject mcontent;
}
