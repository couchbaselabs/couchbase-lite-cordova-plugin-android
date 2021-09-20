package com.couchbase.cblite.objects;

public class QueryArgument {

    private String databaseName;
    private String query;
    private  String JSCallback;

    public  QueryArgument() {

    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getJSCallback() {
        return JSCallback;
    }

    public void setJSCallback(String JSCallback) {
        this.JSCallback = JSCallback;
    }
}
