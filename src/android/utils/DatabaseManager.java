package com.couchbase.cblite.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;

import com.couchbase.cblite.enums.ResultCode;
import com.couchbase.cblite.objects.DatabaseArgument;
import com.couchbase.cblite.objects.DatabaseResource;
import com.couchbase.cblite.objects.DeleteIndexArgument;
import com.couchbase.cblite.objects.DocumentArgument;
import com.couchbase.cblite.objects.FTSIndexArgument;
import com.couchbase.cblite.objects.ListenerArgument;
import com.couchbase.cblite.objects.QueryArgument;
import com.couchbase.cblite.objects.ValueIndexArgument;
import com.couchbase.lite.Blob;
import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseChange;
import com.couchbase.lite.DatabaseChangeListener;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.EncryptionKey;
import com.couchbase.lite.FullTextIndexConfiguration;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.LogDomain;
import com.couchbase.lite.LogLevel;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.ValueIndexConfiguration;
import com.couchbase.lite.internal.utils.JSONUtils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseManager {

    private static Map<String, DatabaseResource> databases;

    private static Database database;
    private static DatabaseManager instance = null;


    public DatabaseManager() {
    }

    public static DatabaseManager getSharedInstance(Context c) {
        if (instance == null) {
            instance = new DatabaseManager();
            databases = new HashMap<>();
            CouchbaseLite.init(c);
        }

        return instance;
    }

    public static Map<String, DatabaseResource> getDatabases() {
        return databases;
    }


    public static Database getDatabase() {
        return database;
    }

    public static void setDatabase(Database database) {
        DatabaseManager.database = database;
    }

    public ResultCode createOrOpenDatabase(DatabaseArgument dbArgument) {
        try {

            DatabaseConfiguration dbConfig = getDatabaseConfig(dbArgument);
            String dbName = dbArgument.getName();

            if (databases.containsKey(dbName)) {
                return ResultCode.DATABASE_ALREADY_EXISTS;
            }

            if (dbName != null && dbConfig != null) {

                Database database = new Database(dbName, dbConfig);
                databases.put(dbName, new DatabaseResource(database, dbConfig));
                return ResultCode.SUCCESS;

            } else if (dbName != null) {

                Database database = new Database(dbName);

                databases.put(dbName, new DatabaseResource(database));
                return ResultCode.SUCCESS;

            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return ResultCode.ERROR;
    }

    public ResultCode databaseExists(DatabaseArgument dbArgument) {

        DatabaseConfiguration dbConfig = getDatabaseConfig(dbArgument);
        String dbName = dbArgument.getName();

        if (dbName != null && dbConfig != null) {
            boolean exists = Database.exists(dbName, new File(dbArgument.getDirectory()));

            if (exists) {
                return ResultCode.DATABASE_ALREADY_EXISTS;
            } else {
                return ResultCode.DATABASE_DOES_NOT_EXIST;
            }
        }

        return ResultCode.ERROR;

    }

    public ResultCode deleteDatabase(DatabaseArgument dbArgument) {
        try {
            String dbName = dbArgument.getName();
            if (!databases.containsKey(dbName)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST;
            }

            if (dbName != null && !dbName.equals("")) {
                DatabaseResource resource = databases.get(dbName);
                resource.getDatabase().delete();
                databases.remove(dbName);
                return ResultCode.SUCCESS;
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return ResultCode.ERROR;
    }

    public ResultCode copyDatabase(String resourceDbName, Context context, DatabaseArgument newDbArgs) {
        try {

            String resourceDbPath = String.format("%s/%s", context.getFilesDir(), resourceDbName + ".cblite2");
            File file = new File(resourceDbPath);

            DatabaseConfiguration config = getDatabaseConfig(newDbArgs);
            if (config != null) {
                Database.copy(file, newDbArgs.getName(), config);
            } else {
                return ResultCode.MISSING_PARAM;
            }
            return ResultCode.SUCCESS;

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return ResultCode.ERROR;
    }

    private DatabaseConfiguration getDatabaseConfig(DatabaseArgument args) {

        String directory = args.getDirectory();
        String encryptionKey = args.getEncryptionKey();

        if ((directory == null && encryptionKey == null) || (directory.equals("") && encryptionKey.equals(""))) {
            return null;
        }

        DatabaseConfiguration dbConfig = new DatabaseConfiguration();

        if (!directory.equals("")) {
            dbConfig.setDirectory(directory);
        }

        if (encryptionKey != null && !encryptionKey.equals("")) {
            dbConfig.setEncryptionKey(new EncryptionKey(encryptionKey));
        }

        return dbConfig;
    }

    public ResultCode closeDatabase(DatabaseArgument dbArgument) {
        try {

            String dbName = dbArgument.getName();
            if (!databases.containsKey(dbName)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST;
            }

            if (dbName != null && !dbName.equals("")) {

                DatabaseResource resource = databases.get(dbName);
                resource.getDatabase().close();
                databases.remove(dbName);
                return ResultCode.SUCCESS;
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return ResultCode.ERROR;
    }

    public ResultCode createDocument(DocumentArgument docArgs) {

        try {

            String dbName = docArgs.getDbName();
            String docId = docArgs.getId();
            JSONObject document = docArgs.getDocument();

            if (document == null || document.length() == 0) {
                return ResultCode.MISSING_DOCUMENT_PARAM;
            }

            if (!databases.containsKey(dbName)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST;
            }

            Database database = databases.get(dbName).getDatabase();
            MutableDocument doc = new MutableDocument(docId, document.toString());
            database.save(doc);

            return ResultCode.SUCCESS;

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return ResultCode.ERROR;
    }

    public String setKeyValuePairDocument(DocumentArgument docArgs) {

        try {

            String docId = docArgs.getId();
            String dbName = docArgs.getDbName();
            String key = docArgs.getKeyValue();
            String value = docArgs.getValueValue();

            if (!databases.containsKey(dbName)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST.toString();
            }

            if (docId == null || docId.equals("") || key == null || key.equals("")) {
                return ResultCode.MISSING_PARAM.toString();
            }

            Database database = databases.get(dbName).getDatabase();

            Document document = database.getDocument(docId);
            MutableDocument mutDoc = document != null ? document.toMutable() : new MutableDocument(docId);
            mutDoc.setString(key, value);
            database.save(mutDoc);

            Document resultDoc = database.getDocument(docId);

            return resultDoc.toJSON();


        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return ResultCode.ERROR.toString();
    }

    public String getDocument(DocumentArgument docArgs) {

        String dbName = docArgs.getDbName();
        String docId = docArgs.getId();

        if (!databases.containsKey(dbName)) {
            return ResultCode.DATABASE_DOES_NOT_EXIST.toString();
        }

        if (docId == null || docId.equals("")) {
            return ResultCode.MISSING_PARAM.toString();
        }

        Database db = this.databases.get(dbName).getDatabase();
        Document doc = db.getDocument(docId);

        if (doc != null) {
            return doc.toJSON();
        }

        return null;
    }

    public ResultCode deleteDocument(DocumentArgument docArguments) {

        try {
            String dbName = docArguments.getDbName();
            String docId = docArguments.getId();

            if (!databases.containsKey(dbName)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST;
            }

            Database db = databases.get(dbName).getDatabase();
            Document doc = db.getDocument(docId);

            if (doc != null) {
                db.delete(doc);
            } else {
                return ResultCode.DOCUMENT_DOES_NOT_EXIST;
            }

            return ResultCode.SUCCESS;

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return ResultCode.ERROR;
    }

    public String createDocumentBlob(DocumentArgument docArgs) {

        try {
            String dbName = docArgs.getDbName();
            String imageBase64 = docArgs.getBlobData();
            String contentType = docArgs.getContentType();
            String documentId = docArgs.getId();
            String key = docArgs.getKeyValue();

            if (!databases.containsKey(dbName)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST.toString();
            }

            if (imageBase64 == null || imageBase64.length() == 0) {
                return ResultCode.EMPTY_IMAGE_DATA.toString();
            }

            if (documentId == null || documentId.equals("") || key == null || key.equals("") || contentType == null || contentType.equals("")) {
                return ResultCode.MISSING_PARAM.toString();
            }

            byte[] decodedString = Base64.decode(imageBase64, Base64.DEFAULT);
            Blob blob = new Blob(contentType, decodedString);

            Database db = databases.get(dbName).getDatabase();
            Document doc = db.getDocument(documentId);

            if (doc != null) {
                MutableDocument mutDoc = doc.toMutable();
                mutDoc.setBlob(key, blob);
                db.save(mutDoc);
                Document newDoc = db.getDocument(documentId);
                return newDoc.toJSON();
            } else {
                return ResultCode.DOCUMENT_DOES_NOT_EXIST.toString();
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String createDocumentBlobFromEmbeddedResource(DocumentArgument docArgs, Context context) {

        try {
            String dbName = docArgs.getDbName();
            String acResource = docArgs.getAcResource();
            String contentType = docArgs.getContentType();
            String documentId = docArgs.getId();
            String key = docArgs.getKeyValue();


            if (!databases.containsKey(dbName)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST.toString();
            }

            if (acResource == null || acResource.equals("")) {
                return ResultCode.EMPTY_IMAGE_DATA.toString();
            }

            if (documentId == null || documentId.equals("") || key == null || key.equals("") || contentType == null || contentType.equals("")) {
                return ResultCode.MISSING_PARAM.toString();
            }

            int drawableResourceId = context.getResources().getIdentifier(acResource, "drawable", context.getPackageName());

            if (drawableResourceId > 0) {
                Drawable drawable = context.getResources().getDrawable(drawableResourceId, null);

                if (drawable == null) {
                    return ResultCode.EMPTY_OR_INVALID_IMAGE_DATA.toString();
                }

                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapByteArr = stream.toByteArray();

                Blob blob = new Blob(contentType, bitmapByteArr);
                Database db = databases.get(dbName).getDatabase();

                Document doc = db.getDocument(documentId);

                if (doc != null) {
                    MutableDocument mutDoc = doc.toMutable();
                    mutDoc.setBlob(key, blob);
                    db.save(mutDoc);
                    Document newDoc = db.getDocument(documentId);
                    return newDoc.toJSON();
                } else {
                    return ResultCode.DOCUMENT_DOES_NOT_EXIST.toString();
                }
            } else {
                return ResultCode.EMPTY_IMAGE_DATA.toString();
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String createDocumentBlobFromFileURL(DocumentArgument docArgs, Context context) {
        try {
            String dbName = docArgs.getDbName();
            String fileURL = docArgs.getFileUrl();
            String contentType = docArgs.getContentType();
            String documentId = docArgs.getId();
            String key = docArgs.getKeyValue();


            if (!databases.containsKey(dbName)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST.toString();
            }

            if (fileURL == null) {
                return ResultCode.EMPTY_IMAGE_DATA.toString();
            }

            if (documentId == null || documentId.equals("") || key == null || key.equals("") || contentType == null || contentType.equals("")) {
                return ResultCode.MISSING_PARAM.toString();
            }

            if (fileURL.startsWith("file://")) {
                fileURL = fileURL.replace("file://", "");
            }

            File imgFile = new File(fileURL);

            if (!imgFile.exists()) {
                return ResultCode.EMPTY_OR_INVALID_IMAGE_DATA.toString();
            }


            Bitmap bmImg = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            if (bmImg != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmImg.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bitmapByteArr = stream.toByteArray();

                Blob blob = new Blob(contentType, bitmapByteArr);
                Database db = databases.get(dbName).getDatabase();

                Document doc = db.getDocument(documentId);

                if (doc != null) {
                    MutableDocument mutDoc = doc.toMutable();
                    mutDoc.setBlob(key, blob);
                    db.save(mutDoc);

                    Document newDoc = db.getDocument(documentId);
                    return newDoc.toJSON();
                } else {
                    return ResultCode.DOCUMENT_DOES_NOT_EXIST.toString();
                }

            } else {
                return ResultCode.EMPTY_OR_INVALID_IMAGE_DATA.toString();
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getBlob(DocumentArgument docArgs) {

        try {

            String dbName = docArgs.getDbName();
            String blobString = docArgs.getBlobData();

            if (!databases.containsKey(dbName)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST.toString();
            }

            if (blobString == null) {
                return ResultCode.EMPTY_OR_INVALID_IMAGE_DATA.toString();
            }

            JSONObject blobObject = new JSONObject(blobString);
            Map<String, Object> map = JSONUtils.fromJSON(blobObject);

            Database db = databases.get(dbName).getDatabase();
            Blob blob = db.getBlob(map);

            String contentType = blob.getContentType();
            String imgBase64 = Base64.encodeToString(blob.getContent(), Base64.NO_WRAP);

            JSONObject result = new JSONObject();
            result.put("contentType", contentType);
            result.put("content", imgBase64);

            return result.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ResultCode.ERROR.toString();
    }

   
    public ResultCode addChangeListener(ListenerArgument listenerArgument, CordovaWebView webView) {

        String database = listenerArgument.getDatabaseName();
        if (!databases.containsKey(database)) {
            return ResultCode.DATABASE_DOES_NOT_EXIST;
        }

        String jsCallbackFn = listenerArgument.getCallback();

        if (jsCallbackFn != null && !jsCallbackFn.trim().equals("")) {

            DatabaseResource dbResource = databases.get(database);
            Database db = dbResource.getDatabase();

            Map<String, ArrayList<String>> docChanges = new HashMap<>();
            docChanges.put("deleted", new ArrayList<String>());
            docChanges.put("addOrChanged", new ArrayList<String>());

            if (dbResource.getListenerToken() == null) {
                ListenerToken listenerToken = db.addChangeListener(new DatabaseChangeListener() {
                    @Override
                    public void changed(DatabaseChange change) {

                        if (change != null) {
                            for (String docId : change.getDocumentIDs()) {
                                Document doc = db.getDocument(docId);
                                if (doc == null) {
                                    docChanges.get("deleted").add(docId);
                                } else {
                                    docChanges.get("addOrChanged").add(docId);
                                }
                            }

                            try {
                                String strDocChanges = "Found " + change.getDocumentIDs().size() + " change(s)";
                                JSONObject object = new JSONObject();
                                object.put("message", strDocChanges);
                                object.put("docChanges", docChanges.toString());

                                String params = object.toString();
                                webView.loadUrl("javascript:" + jsCallbackFn + "(" + params + ")");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                    }
                });

                dbResource.setListenerToken(listenerToken);
            } else {
                return ResultCode.CHANGE_LISTENER_ALREADY_EXISTS;
            }
        }

        return ResultCode.SUCCESS;
    }

    public ResultCode removeChangeListener(ListenerArgument listenerArgument, CallbackContext callbackContext) {

        String database = listenerArgument.getDatabaseName();
        if (!databases.containsKey(database)) {
            return ResultCode.DATABASE_DOES_NOT_EXIST;
        }

        DatabaseResource dbResource = databases.get(database);
        Database db = dbResource.getDatabase();

        if (dbResource.getListenerToken() != null) {
            db.removeChangeListener(dbResource.getListenerToken());
            dbResource.setListenerToken(null);
        }

        return ResultCode.SUCCESS;
    }

    public ResultCode enableLogging() {

        Database.log.getConsole().setDomains(LogDomain.ALL_DOMAINS);
        Database.log.getConsole().setLevel(LogLevel.DEBUG);

        return ResultCode.SUCCESS;
    }


    public ResultCode query(QueryArgument argument, CallbackContext callbackContext) {

        try {
            String database = argument.getDatabaseName();
            String queryString = argument.getQuery();

            if (!databases.containsKey(database)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST;
            }

            if (queryString == null || queryString.equals("")) {
                return ResultCode.MISSING_PARAM;
            }

            DatabaseResource dbResource = databases.get(database);
            Database db = dbResource.getDatabase();

            Query query = db.createQuery(queryString);
            ResultSet rows = query.execute();

            Result row;
            JSONArray json = new JSONArray();
            while ((row = rows.next()) != null) {
                JSONObject rowObject = new JSONObject(row.toJSON());
                json.put(rowObject);
            }

            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, json);
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);

            return ResultCode.SUCCESS;

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            return ResultCode.JSON_ERROR;
        }
        return ResultCode.ERROR;
    }

    public ResultCode createValueIndex(ValueIndexArgument argument) {
        try {
            String database = argument.getDatabaseName();
            String indexName = argument.getIndexName();
            List<String> indexExpressionList = argument.getIndexExpressions();

            if (!databases.containsKey(database)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST;
            }

            DatabaseResource dbResource = databases.get(database);
            Database db = dbResource.getDatabase();

            String indexExpression = TextUtils.join(",", indexExpressionList);
            ValueIndexConfiguration indexConfig = new ValueIndexConfiguration(indexExpression);
            db.createIndex(indexName, indexConfig);

            return ResultCode.SUCCESS;

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return ResultCode.ERROR;
    }

    public ResultCode createFTSIndex(FTSIndexArgument argument) {
        try {
            String database = argument.getDatabaseName();
            String indexName = argument.getIndexName();
            String language = argument.getLanguage();
            boolean ignoreAccents = argument.isIgnoreAccents();

            List<String> indexExpressionList = argument.getIndexExpressions();

            if (!databases.containsKey(database)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST;
            }

            DatabaseResource dbResource = databases.get(database);
            Database db = dbResource.getDatabase();

            String indexExpressions = TextUtils.join(",", indexExpressionList);

            FullTextIndexConfiguration indexConfig = new FullTextIndexConfiguration(indexExpressions);
            indexConfig.ignoreAccents(ignoreAccents);
            indexConfig.setLanguage(language);

            db.createIndex(indexName, indexConfig);

            return ResultCode.SUCCESS;

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return ResultCode.ERROR;
    }

    public ResultCode deleteIndex(DeleteIndexArgument argument) {
        try {
            String database = argument.getDatabaseName();
            String indexName = argument.getIndexName();

            if (!databases.containsKey(database)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST;
            }

            DatabaseResource dbResource = databases.get(database);
            Database db = dbResource.getDatabase();
            db.deleteIndex(indexName);

            return ResultCode.SUCCESS;

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return ResultCode.ERROR;
    }
}
