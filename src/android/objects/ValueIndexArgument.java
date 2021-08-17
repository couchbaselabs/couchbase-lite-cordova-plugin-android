package com.couchbase.cblite.objects;

import java.util.List;

public class ValueIndexArgument {

    private String databaseName;
    private String indexName;
    private List<String> indexExpressions;


    public List<String> getIndexExpressions() {
        return indexExpressions;
    }

    public void setIndexExpressions(List<String> indexes) {
        this.indexExpressions = indexes;
    }


    public ValueIndexArgument() {}

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }




}
