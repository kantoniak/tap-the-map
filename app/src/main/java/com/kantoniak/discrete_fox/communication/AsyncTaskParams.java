package com.kantoniak.discrete_fox.communication;

public class AsyncTaskParams {
    public ContentObject getContent() {
        return mcontent;
    }

    public String getQuery() {
        return mquery;
    }

    String mquery;
    ContentObject mcontent;

    AsyncTaskParams(String query, ContentObject content) {
        mquery = query;
        mcontent = content;
    }

}
