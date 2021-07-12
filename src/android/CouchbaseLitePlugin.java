package cordova.plugin.couchbaselite;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cordova.plugin.couchbaselite.utils.DatabaseManager;

/**
 * This class echoes a string called from JavaScript.
 */
public class CouchbaseLitePlugin extends CordovaPlugin {


    public static final String INITIALIZE_DATABASE = "initializeDB";

    public static final String CLOSE_DATABASE = "closeDB";

    public static final String SAVE_DATA = "saveData";

    public static final String LOAD_DATA = "loadData";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        switch (action) {

            case INITIALIZE_DATABASE:
                String dbName = args.getString(0);
                String dirName = args.getString(1);
                this.initializeDatabase(dbName, dirName, callbackContext);
                return true;

            case CLOSE_DATABASE:
                this.closeDatabase(callbackContext);
                return true;

            case SAVE_DATA:

                String docID = args.getString(0);
                JSONObject obj = args.getJSONObject(1);

                this.saveDocument(docID, obj, callbackContext);
                return true;

            case LOAD_DATA:

                String documentID = args.getString(0);
                this.loadDocument(documentID, callbackContext);
                return true;

            default:
                return false;
        }

    }

    private void initializeDatabase(String dbName, String dirName, CallbackContext callbackContext) {
        if (dbName != null && dbName.length() > 0) {
            Context context = this.cordova.getActivity().getApplicationContext();

            DatabaseManager dbMgr = DatabaseManager.getSharedInstance();
            dbMgr.initCouchbaseLite(context);
            dbMgr.openOrCreateDatabase(context, dbName, dirName);

            callbackContext.success();
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void closeDatabase(CallbackContext callbackContext) {
        if (DatabaseManager.getDatabase() != null) {

            boolean closed = DatabaseManager.getSharedInstance().closeDatabase();

            if (closed) {
                callbackContext.success();
            } else {
                callbackContext.error("Could not close database.");
            }

        } else {
            callbackContext.error("Could not close database.");
        }
    }


    private void saveDocument(String docID, JSONObject object, CallbackContext callbackContext) {

        try {
            Map<String, Object> documentMap = new HashMap<>();

            JSONArray keys = object.names();

            for (int i = 0; i < keys.length(); i++) {
                String key = keys.getString(i);
                String value = object.getString(key);
                documentMap.put(key, value);
            }

            try {
                Database database = DatabaseManager.getDatabase();
                if (database != null) {
                    MutableDocument mutableDocument = new MutableDocument(docID, documentMap);
                    database.save(mutableDocument);
                    callbackContext.success();
                } else {
                    callbackContext.error("Database not initialized.");
                }
            } catch (CouchbaseLiteException e) {
                callbackContext.error(e.getMessage());
            }

        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
        }
    }


    private void loadDocument(String docID, CallbackContext callbackContext) {

        Database database = DatabaseManager.getDatabase();
        if (database != null) {
            Document document = database.getDocument(docID);
            if (document != null) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    Iterator<String> iterator = document.iterator();
                    while ((iterator.hasNext())) {
                        String key = iterator.next();
                        Object value = document.getValue(key);
                        jsonObject.put(key, value);
                    }

                    PluginResult result = new PluginResult(PluginResult.Status.OK, jsonObject);
                    result.setKeepCallback(false);
                    callbackContext.sendPluginResult(result);
                    callbackContext.success();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                callbackContext.error("Invalid Document");
            }

        } else {
            callbackContext.error("Database not initialized.");
        }

    }

}
