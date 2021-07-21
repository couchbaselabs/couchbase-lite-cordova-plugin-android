package cordova.plugin.couchbaselite.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.couchbase.lite.Blob;
import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseChange;
import com.couchbase.lite.DatabaseChangeListener;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.EncryptionKey;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.LogDomain;
import com.couchbase.lite.LogLevel;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.internal.utils.JSONUtils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cordova.plugin.couchbaselite.enums.ResultCode;
import cordova.plugin.couchbaselite.objects.DatabaseArgument;
import cordova.plugin.couchbaselite.objects.DatabaseResource;
import cordova.plugin.couchbaselite.objects.DocumentArgument;

import static com.couchbase.lite.internal.utils.JSONUtils.fromJSON;

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

    public ResultCode createDatabase(DatabaseArgument dbArgument) {
        try {

            DatabaseConfiguration dbConfig = getDatabaseConfig(dbArgument);
            String dbName = dbArgument.getName();
            if (databases.get(dbName) != null) {
                return ResultCode.EXIST;
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

    public ResultCode deleteDatabase(DatabaseArgument dbArgument) {
        try {
            String dbName = dbArgument.getName();
            if (!databases.containsKey(dbName)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST;
            }

            if (dbName != null && !dbName.equals("")) {
                DatabaseResource resource = databases.get(dbName);
                resource.getDatabase().removeChangeListener(resource.getListenerToken());
                resource.getDatabase().delete();
                databases.remove(dbName);
                return ResultCode.SUCCESS;
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return ResultCode.ERROR;
    }

    public ResultCode copyDatabase(DatabaseArgument currentDbArgs, DatabaseArgument newDbArgs) {
        try {

            if (!databases.containsKey(currentDbArgs.getName())) {
                return ResultCode.DATABASE_DOES_NOT_EXIST;
            }
            if (new File(newDbArgs.getDirectory() + "/" + newDbArgs.getName() + ".cblite2").isDirectory()) {
                return ResultCode.DATABASE_ALREADY_EXISTS;
            }

            String dbPath = databases.get(currentDbArgs.getName()).getDatabase().getPath();
            File file = new File(dbPath);
            if (file != null) {
                DatabaseConfiguration config = getDatabaseConfig(newDbArgs);
                Database.copy(file, newDbArgs.getName(), config);
                return ResultCode.SUCCESS;
            }
            return ResultCode.ERROR;

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

        if (directory != null && !directory.equals("")) {
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
                resource.getDatabase().removeChangeListener(resource.getListenerToken());
                resource.getDatabase().close();
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

    public String getDocument(DocumentArgument docArgs) {

        String dbName = docArgs.getDbName();
        String docId = docArgs.getId();

        if (!databases.containsKey(dbName)) {
            return ResultCode.DATABASE_DOES_NOT_EXIST.toString();
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

            Database db = this.databases.get(dbName).getDatabase();
            Document doc = db.getDocument(docId);
            db.delete(doc);

            return ResultCode.SUCCESS;

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return ResultCode.ERROR;
    }

    public String setBlob(String fileURI, String dbName) {
        try {
            if (!databases.containsKey(dbName)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST.toString();
            }

            if (fileURI.length() > 0) {

                URL fileURL = new URL(fileURI);
                Database db = databases.get(dbName).getDatabase();
                Blob blob = new Blob("image", fileURL);
                db.saveBlob(blob);

                return blob.toJSON();

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResultCode.ERROR.toString();
    }

    public String getBlob(JSONObject blobObj, String dbName) {

        try {
            if (!databases.containsKey(dbName)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST.toString();
            }

            Map<String, Object> map = JSONUtils.fromJSON(blobObj);

            Database db = databases.get(dbName).getDatabase();
            Blob blob = db.getBlob(map);
            String imgBase64 = Base64.encodeToString(blob.getContent(), Base64.NO_WRAP);

            return imgBase64;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ResultCode.ERROR.toString();
    }

    public ResultCode addChangeListener(String database, CallbackContext callbackContext) {
        if (!databases.containsKey(database)) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "Database does not exist.");
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
            return ResultCode.DATABASE_DOES_NOT_EXIST;
        }

        DatabaseResource dbResource = databases.get(database);
        Database db = dbResource.getDatabase();
        ListenerToken listenerToken = db.addChangeListener(new DatabaseChangeListener() {
            @Override
            public void changed(@NonNull DatabaseChange change) {

                if (change != null) {
                    for (String docId : change.getDocumentIDs()) {
                        Document doc = db.getDocument(docId);
                        if (doc != null) {
                            Log.i("DatabaseChangeEvent", "Document was added/updated");

                            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "DatabaseChangeEvent: Document was added/updated.");
                            pluginResult.setKeepCallback(true);
                            callbackContext.sendPluginResult(pluginResult);

                        } else {
                            Log.i("DatabaseChangeEvent", "Document was deleted");

                            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "DatabaseChangeEvent: Document was deleted.");
                            pluginResult.setKeepCallback(true);
                            callbackContext.sendPluginResult(pluginResult);

                        }
                    }
                }

            }
        });

        dbResource.setListenerToken(listenerToken);

        return ResultCode.SUCCESS;
    }

    public ResultCode removeChangeListener(String database, CallbackContext callbackContext) {

        if (!databases.containsKey(database)) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "Database does not exist.");
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
            return ResultCode.DATABASE_DOES_NOT_EXIST;
        }

        DatabaseResource dbResource = databases.get(database);
        Database db = dbResource.getDatabase();

        if (dbResource.getListenerToken() != null) {
            db.removeChangeListener(dbResource.getListenerToken());
        }

        return ResultCode.SUCCESS;
    }

    public ResultCode enableLogging() {

        Database.log.getConsole().setDomains(LogDomain.ALL_DOMAINS);
        Database.log.getConsole().setLevel(LogLevel.DEBUG);

        return ResultCode.SUCCESS;
    }
}
