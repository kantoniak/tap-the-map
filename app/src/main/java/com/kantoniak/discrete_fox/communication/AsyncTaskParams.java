package com.kantoniak.discrete_fox.communication;

public class AsyncTaskParams {
    String mquery;
    String mdesc;

    public AsyncTaskParams(String query, String desc) {
        mquery = query;
        mdesc = desc;
    }

    public String getQuery() {
        return mquery;
    }
    public String getDesc() { return mdesc; }
}
