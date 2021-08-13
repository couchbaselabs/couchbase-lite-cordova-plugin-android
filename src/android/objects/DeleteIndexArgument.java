package com.couchbase.cblite.objects;

public class DeleteIndexArgument {

    private String databaseName;
    private String indexName;

    public DeleteIndexArgument() {}

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
