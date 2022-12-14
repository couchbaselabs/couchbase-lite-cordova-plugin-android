package com.couchbase.cblite.objects;

import java.util.ArrayList;
import java.util.List;

public class FTSIndexArgument {

    private String databaseName;
    private String indexName;
    private boolean ignoreAccents;
    private String language;
    private List<String> indexExpressions;

    public boolean isIgnoreAccents() {
        return ignoreAccents;
    }

    public void setIgnoreAccents(boolean ignoreAccents) {
        this.ignoreAccents = ignoreAccents;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }



    public List<String> getIndexExpressions() {
        return indexExpressions;
    }

    public void setIndexExpressions(List<String> indexes) {
        this.indexExpressions = indexes;
    }

    public FTSIndexArgument() {}

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
