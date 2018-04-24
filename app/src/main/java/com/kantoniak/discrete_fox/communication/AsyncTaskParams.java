package com.kantoniak.discrete_fox.communication;

public class AsyncTaskParams {

    public String getQuery() {
        return mquery;
    }
    public String getDesc() { return mdesc; }

    String mquery;
    String mdesc;

    AsyncTaskParams(String query, String desc) {
        mquery = query;
        mdesc = desc;
    }

}
