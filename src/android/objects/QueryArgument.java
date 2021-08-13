package com.couchbase.cblite.objects;

public class QueryArgument {

    private String databaseName;
    private String query;

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
}
