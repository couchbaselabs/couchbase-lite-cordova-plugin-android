package com.couchbase.cblite.objects;

import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.Query;

public class QueryListenerResource {

    private Query query;
    private ListenerToken queryChangeListenerToken;
    private String getQueryChangeListenerJSFunction;


    public QueryListenerResource() {

    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public ListenerToken getQueryChangeListenerToken() {
        return queryChangeListenerToken;
    }

    public void setQueryChangeListenerToken(ListenerToken queryChangeListenerToken) {
        this.queryChangeListenerToken = queryChangeListenerToken;
    }

    public String getQueryChangeListenerJSFunction() {
        return getQueryChangeListenerJSFunction;
    }

    public void setQueryChangeListenerJSFunction(String getQueryChangeListenerJSFunction) {
        this.getQueryChangeListenerJSFunction = getQueryChangeListenerJSFunction;
    }

}
