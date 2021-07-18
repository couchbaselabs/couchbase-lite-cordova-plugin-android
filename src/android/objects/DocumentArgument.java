package cordova.plugin.couchbaselite.objects;

import org.json.JSONObject;

public class DocumentArgument {

    private String id;
    private JSONObject document;
    private String dbName;


    public DocumentArgument() {

    }

    public DocumentArgument(String id, String dbName) {
        this.id = id;
        this.document = null;
        this.dbName = dbName;
    }

    public DocumentArgument(String id, JSONObject document, String dbName) {
        this.id = id;
        this.document = document;
        this.dbName = dbName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONObject getDocument() {
        return document;
    }

    public void setDocument(JSONObject document) {
        this.document = document;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
}
