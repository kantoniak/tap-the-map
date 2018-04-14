package com.kantoniak.discrete_fox.communication;

public class AsyncTaskParams {

    public String getQuery() {
        return mquery;
    }
    public int getOffset() { return moffset; }
    public String getDesc() { return mdesc; }

    int moffset;
    String mquery;
    String mdesc;

    AsyncTaskParams(String query, int offset, String desc) {
        mquery = query;
        moffset = offset;
        mdesc = desc;
    }

}
