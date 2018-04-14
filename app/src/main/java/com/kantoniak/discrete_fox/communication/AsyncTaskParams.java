package com.kantoniak.discrete_fox.communication;

public class AsyncTaskParams {

    public String getQuery() {
        return mquery;
    }
    public int getOffset() { return moffset; }

    int moffset;
    String mquery;

    AsyncTaskParams(String query, int offset) {
        mquery = query;
        moffset = offset;
    }

}
