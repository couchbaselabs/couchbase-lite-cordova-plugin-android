package com.couchbase.cblite.objects;

import org.json.JSONObject;

public class ListenerArgument {

    private String databaseName;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    private String callback;


    public ListenerArgument(String dbName) {
        this.databaseName = dbName;
    }

    public ListenerArgument(String dbName, String callback) {

        this.databaseName = dbName;
        this.callback = callback;
    }




}
