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

