package cordova.plugin.couchbaselite;

import android.content.Context;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cordova.plugin.couchbaselite.enums.ResultCode;
import cordova.plugin.couchbaselite.objects.DocumentArgument;
import cordova.plugin.couchbaselite.utils.DatabaseManager;
import cordova.plugin.couchbaselite.objects.DatabaseArgument;

/**
 * This class echoes a string called from JavaScript.
 */
public class CouchbaseLitePlugin extends CordovaPlugin {

    private static final String TAG = "cordova.plugin.couchbase";

    private static final String ACTION_CREATE_DATABASE = "createDatabase";
    private static final String ACTION_CLOSE_DATABASE = "closeDatabase";
    private static final String ACTION_DELETE_DATABASE = "deleteDatabase";
    private static final String ACTION_COPY_DATABASE = "copyDatabase";
    private static final String ACTION_ADD_CHANGE_LISTENER = "addChangeListener";
    private static final String ACTION_REMOVE_CHANGE_LISTENER = "removeChangeListener";
    private static final String ACTION_DELETE_DOCUMENT = "deleteDocument";
    private static final String ACTION_GET_DOCUMENT = "getDocument";
    private static final String ACTION_SAVE_DOCUMENT = "saveDocument";

    private static final String ACTION_SET_BLOB = "setBlob";
    private static final String ACTION_GET_BLOB = "getBlob";

 /* private static final String ACTION_INIT_MUTABLE_DOCUMENT = "createMutableDocument";
    private static final String ACTION_MUTABLE_DOCUMENT_STRING = "mutableDocumentSetString";
    private static final String ACTION_MUTABLE_DOCUMENT_BLOB = "mutableDocumentSetBlob";*/

    private static final String ACTION_ENABLE_LOGGING = "enableLogging";

    private Context context;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        context = this.cordova.getActivity().getApplicationContext();

        switch (action) {

            case ACTION_CREATE_DATABASE:
                this.createDatabase(args, callbackContext);
                return true;
            case ACTION_CLOSE_DATABASE:
                closeDatabase(args, callbackContext);
                return true;
            case ACTION_COPY_DATABASE:
                copyDatabase(args, callbackContext);
                return true;
            case ACTION_DELETE_DATABASE:
                deleteDatabase(args, callbackContext);
                return true;
            case ACTION_ADD_CHANGE_LISTENER:
                addChangeListener(args, callbackContext);
                return true;
            case ACTION_REMOVE_CHANGE_LISTENER:
                removeChangeListener(args, callbackContext);
                return true;
            case ACTION_SAVE_DOCUMENT:
                saveDocument(args, callbackContext);
                return true;
            case ACTION_GET_DOCUMENT:
                getDocument(args, callbackContext);
                return true;

           /* case ACTION_INIT_MUTABLE_DOCUMENT:
                mutableDocument(args, callbackContext);
                return true;
            case ACTION_MUTABLE_DOCUMENT_STRING:
                mutableDocumentString(args, callbackContext);
                return true;
            case ACTION_MUTABLE_DOCUMENT_BLOB:
                mutableDocumentBlob(args, callbackContext);
                return true;*/

            case ACTION_SET_BLOB:
                setBlob(args, callbackContext);
                return true;
            case ACTION_GET_BLOB:
                getBlob(args, callbackContext);
                return true;
            case ACTION_DELETE_DOCUMENT:
                deleteDocument(args, callbackContext);
                return true;
            case ACTION_ENABLE_LOGGING:
                enableLogging(callbackContext);
                return true;

            default:
                return false;
        }

    }

    private void createDatabase(JSONArray args, CallbackContext callbackContext) {

        try {
            DatabaseArgument dbArguments = this.parseDatabaseArguments(args.getJSONObject(0), callbackContext);
            DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
            ResultCode result = dbMgr.createDatabase(dbArguments);

            PluginResult pluginResult;

            if (result == ResultCode.SUCCESS || result == ResultCode.EXIST) {
                pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
            } else {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error creating database.");
            }
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);

        } catch (JSONException e) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Invalid arguments passed in.");
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
            e.printStackTrace();
        }

    }

    private DatabaseArgument parseDatabaseArguments(JSONObject dictionary, CallbackContext callbackContext) {
        DatabaseArgument argument = new DatabaseArgument();
        Context context = this.cordova.getActivity().getApplicationContext();

        try {
            String name = dictionary.has("name") ? dictionary.getString("name").toLowerCase() : null;
            String directory = dictionary.has("directory") ? dictionary.getString("directory") : null;
            String encryptionKey = dictionary.has("encryptionKey") ? dictionary.getString("encryptionKey") : null;

            if (name != null) {
                argument.setName(name);
            } else {
                callbackContext.error("error: database name must be passed in as argument.");
                return argument;
            }

            if (directory != null) {
                argument.setDirectory(String.format("%s/%s", context.getFilesDir(), directory));
            }

            if (encryptionKey != null) {
                argument.setEncryptionKey(encryptionKey);
            }

            return argument;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return argument;
    }

    private DocumentArgument parseDocumentArguments(JSONObject dictionary) {
        DocumentArgument docArgument = null;

        try {

            String dbName = dictionary.has("database") ? dictionary.getString("database").toLowerCase() : null;
            String documentId = dictionary.has("id") ? dictionary.getString("id") : null;
            JSONObject document = dictionary.has("document") ? (JSONObject) dictionary.get("document") : null;

            if (dbName == null || dbName.equals("") || documentId == null || documentId.equals("")) {
                return null;
            }

            if (document != null) {
                docArgument = new DocumentArgument(documentId, document, dbName);
            } else {
                docArgument = new DocumentArgument(documentId, dbName);
            }

            return docArgument;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void closeDatabase(JSONArray args, CallbackContext callbackContext) {

        try {

            DatabaseArgument arguments = parseDatabaseArguments(args.getJSONObject(0), callbackContext);
            DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
            ResultCode result = dbMgr.closeDatabase(arguments);


            PluginResult pluginResult;
            if (result == ResultCode.SUCCESS) {
                pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
            } else if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Database does not exist.");
            } else {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error closing database.");
            }

            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);

        } catch (JSONException e) {

            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Invalid arguments passed in.");
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
            e.printStackTrace();
        }
    }

    private void copyDatabase(JSONArray args, CallbackContext callbackContext) {

        DatabaseArgument currentDbArgs;
        DatabaseArgument newDbArgs;

        try {
            JSONObject configs = args.getJSONObject(0);

            JSONObject newConfig = configs.getJSONObject("newConfig");
            JSONObject currentConfig = configs.getJSONObject("currentConfig");

            newDbArgs = parseDatabaseArguments(newConfig, callbackContext);
            currentDbArgs = parseDatabaseArguments(currentConfig, callbackContext);


            DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
            ResultCode result = dbMgr.copyDatabase(currentDbArgs, newDbArgs);

            PluginResult pluginResult;
            if (result == ResultCode.SUCCESS) {
                pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
            } else if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Source Database does not exist.");
            } else if (result == ResultCode.DATABASE_ALREADY_EXISTS) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "A database with same name already exists.");
            } else {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error copying database.");
            }

            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);

        } catch (JSONException e) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Invalid arguments passed in.");
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
            e.printStackTrace();
        }
    }

    private void setBlob(JSONArray args, CallbackContext callbackContext) {

        try {

            JSONObject params = args.getJSONObject(0);

            String fileURI = params.getString("fileURI");
            String database = params.getString("database");

            DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
            String result = dbMgr.setBlob(fileURI, database);

            PluginResult pluginResult;
            if (result == null) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error setting Blob value.");
            } else if (result.equals(String.valueOf(ResultCode.DATABASE_DOES_NOT_EXIST))) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Database does not exist.");
            } else if (result.equals(String.valueOf(ResultCode.ERROR))) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error setting Blob value.");
            } else {
                pluginResult = new PluginResult(PluginResult.Status.OK, new JSONObject(result));
            }

            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);

        } catch (JSONException e) {

            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Invalid arguments passed in.");
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
            e.printStackTrace();
        }

    }

    private void getBlob(JSONArray args, CallbackContext callbackContext) {

        try {

            JSONObject params = args.getJSONObject(0);

            JSONObject blob = (JSONObject) params.get("blob");
            String database = params.getString("database");

            DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
            String result = dbMgr.getBlob(blob, database);

            PluginResult pluginResult;
            if (result == null) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error getting Blob value.");
            } else if (result.equals(String.valueOf(ResultCode.DATABASE_DOES_NOT_EXIST))) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Database does not exist.");
            } else if (result.equals(String.valueOf(ResultCode.ERROR))) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error getting Blob value.");
            } else {
                pluginResult = new PluginResult(PluginResult.Status.OK, result);
            }

            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);

        } catch (JSONException e) {

            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Invalid arguments passed in.");
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
            e.printStackTrace();
        }

    }

    private void addChangeListener(JSONArray args, final CallbackContext callbackContext) {

        try {

            JSONObject params = args.getJSONObject(0);
            String database = params.getString("database");

            DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
            ResultCode result = dbMgr.addChangeListener(database, callbackContext);

            PluginResult pluginResult;
            if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Database does not exist.");
            } else if (result == ResultCode.ERROR) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error adding change listener.");
            } else {
                pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
            }

            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        } catch (JSONException e) {

            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Invalid arguments passed in.");
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
            e.printStackTrace();
        }

    }

    private void removeChangeListener(JSONArray args, CallbackContext callbackContext) {

        try {

            JSONObject params = args.getJSONObject(0);
            String database = params.getString("database");

            DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
            ResultCode result = dbMgr.removeChangeListener(database, callbackContext);

            PluginResult pluginResult;
            if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Database does not exist.");
            } else if (result == ResultCode.ERROR) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error removing change listener.");
            } else {
                pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
            }

            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);

        } catch (JSONException e) {

            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Invalid arguments passed in.");
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
            e.printStackTrace();
        }
    }

    private void mutableDocument(JSONArray args, CallbackContext callbackContext) {

    }

    private void mutableDocumentString(JSONArray args, CallbackContext callbackContext) {

    }

    private void mutableDocumentBlob(JSONArray args, CallbackContext callbackContext) {

    }

    private void saveDocument(JSONArray args, CallbackContext callbackContext) {

        try {

            DocumentArgument docArguments = parseDocumentArguments(args.getJSONObject(0));

            if (docArguments != null) {

                DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
                ResultCode result = dbMgr.createDocument(docArguments);

                PluginResult pluginResult;
                if (result == ResultCode.SUCCESS) {
                    pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
                } else if (result == ResultCode.MISSING_DOCUMENT_PARAM) {
                    pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no document found in arguments, please send document in JSON string format");
                } else if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
                    pluginResult = new PluginResult(PluginResult.Status.ERROR, "Database does not exist.");
                } else {
                    pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error creating document.");
                }

                pluginResult.setKeepCallback(false);
                callbackContext.sendPluginResult(pluginResult);

            } else {

                PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error missing parameters.");
                pluginResult.setKeepCallback(false);
                callbackContext.sendPluginResult(pluginResult);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Invalid arguments passed in.");
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
        }

    }

    private void getDocument(JSONArray args, CallbackContext callbackContext) {

        try {
            DocumentArgument docArguments = parseDocumentArguments(args.getJSONObject(0));

            if (docArguments != null) {
                DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
                String result = dbMgr.getDocument(docArguments);

                PluginResult pluginResult;

                if (result != null) {
                    if (result.equals(String.valueOf(ResultCode.DATABASE_DOES_NOT_EXIST))) {
                        pluginResult = new PluginResult(PluginResult.Status.ERROR, "Database does not exist.");
                    } else {
                        pluginResult = new PluginResult(PluginResult.Status.OK, new JSONObject(result));
                    }
                } else {
                    pluginResult = new PluginResult(PluginResult.Status.ERROR, "Document not found.");
                }

                pluginResult.setKeepCallback(false);
                callbackContext.sendPluginResult(pluginResult);

            } else {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error missing parameters.");
                pluginResult.setKeepCallback(false);
                callbackContext.sendPluginResult(pluginResult);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Invalid arguments passed in.");
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
        }

    }

    private void deleteDocument(JSONArray args, CallbackContext callbackContext) {
        try {
            DocumentArgument docArguments = parseDocumentArguments(args.getJSONObject(0));

            if (docArguments != null) {
                DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
                ResultCode resultCode = dbMgr.deleteDocument(docArguments);

                PluginResult pluginResult;
                if (resultCode == ResultCode.SUCCESS) {
                    pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
                } else {
                    pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error deleting document.");
                }

                pluginResult.setKeepCallback(false);
                callbackContext.sendPluginResult(pluginResult);


            } else {
                callbackContext.error("Error missing parameters.");
            }

        } catch (JSONException e) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Invalid arguments passed in.");
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
        }
    }

    private void enableLogging(CallbackContext callbackContext) {

            DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
            ResultCode result = dbMgr.enableLogging();

            PluginResult pluginResult;
            if (result == ResultCode.SUCCESS) {
                pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
            } else {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error enabling logger for the database.");
            }

            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
    }

    private void deleteDatabase(JSONArray args, CallbackContext callbackContext) {

        try {
            DatabaseArgument arguments = parseDatabaseArguments(args.getJSONObject(0), callbackContext);
            DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
            ResultCode result = dbMgr.deleteDatabase(arguments);

            PluginResult pluginResult;
            if (result == ResultCode.SUCCESS) {
                pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
            } else if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Database does not exist.");
            } else {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error deleting database.");
            }

            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);

        } catch (JSONException e) {
            e.printStackTrace();
            PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Invalid arguments passed in.");
            pluginResult.setKeepCallback(false);
            callbackContext.sendPluginResult(pluginResult);
        }

    }

}
