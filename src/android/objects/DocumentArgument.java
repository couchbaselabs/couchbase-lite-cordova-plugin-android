package com.couchbase.cblite.objects;

import org.json.JSONObject;


public class DocumentArgument {

    //needed to talk to documents
    private String id;
    private String dbName;
    private JSONObject document;

    //used for key/value pair
    private  String keyValue;
    private  String valueValue;

    //used for standard blobs
    private String contentType;
    private String blobData;
    private String fileUrl;

    //asset catalog resource
    private String acResource;


    public DocumentArgument() {
    }

   /* public DocumentArgument(String id, String dbName, String blobData) {
        this.id = id;
        this.dbName = dbName;
        this.blobData = blobData;
    }

    public DocumentArgument(String id, String dbName) {
        this.id = id;
        this.dbName = dbName;
        this.document = null;
    }

    public DocumentArgument(String id, JSONObject document, String dbName) {
        this.id = id;
        this.document = document;
        this.dbName = dbName;
    }

    public DocumentArgument(String id, String dbName, String keyValue, String valueValue) {
        this.id = id;
        this.dbName = dbName;
        this.keyValue = keyValue;
        this.valueValue = valueValue;
    }

    public DocumentArgument(String id, String dbName, String keyValue, String acResource, Boolean dummy) {
        this.id = id;
        this.dbName = dbName;
        this.keyValue = keyValue;
        this.acResource = acResource;
    }

    public DocumentArgument(String id, String dbName, String keyValue, String fileUrl, Boolean dummy, Boolean dummy2) {
        this.id = id;
        this.dbName = dbName;
        this.keyValue = keyValue;
        this.fileUrl = fileUrl;
    }

    public DocumentArgument(String id, String dbName, String keyValue, String contentType, String blobData) {
        this.id = id;
        this.dbName = dbName;
        this.keyValue = keyValue;
        this.contentType = contentType;
        this.blobData = blobData;
    }*/

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

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getValueValue() {
        return valueValue;
    }

    public void setValueValue(String valueValue) {
        this.valueValue = valueValue;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBlobData() {
        return blobData;
    }

    public void setBlobData(String blobData) {
        this.blobData = blobData;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getAcResource() {
        return acResource;
    }

    public void setAcResource(String acResource) {
        this.acResource = acResource;
    }
}

