package com.couchbase.cblite;

import android.content.Context;
import android.content.res.AssetManager;

import com.couchbase.cblite.enums.ResultCode;
import com.couchbase.cblite.objects.DatabaseArgument;
import com.couchbase.cblite.objects.DatabaseResource;
import com.couchbase.cblite.objects.DeleteIndexArgument;
import com.couchbase.cblite.objects.DocumentArgument;
import com.couchbase.cblite.objects.FTSIndexArgument;
import com.couchbase.cblite.objects.ListenerArgument;
import com.couchbase.cblite.objects.QueryArgument;
import com.couchbase.cblite.objects.ValueIndexArgument;
import com.couchbase.cblite.utils.DatabaseManager;
import com.couchbase.lite.BasicAuthenticator;
import com.couchbase.lite.Database;
import com.couchbase.lite.ReplicatorConfiguration;
import com.couchbase.lite.SessionAuthenticator;
import com.couchbase.lite.URLEndpoint;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class echoes a string called from JavaScript.
 */
public class CBLite extends CordovaPlugin {

  private static final String ACTION_CREATE_OPEN_DATABASE = "createOrOpenDatabase";
  private static final String ACTION_CLOSE_DATABASE = "closeDatabase";
  private static final String ACTION_DELETE_DATABASE = "deleteDatabase";
  private static final String ACTION_COPY_DATABASE = "copyDatabase";
  private static final String ACTION_ADD_CHANGE_LISTENER = "addChangeListener";
  private static final String ACTION_REMOVE_CHANGE_LISTENER = "removeChangeListener";
  private static final String ACTION_DELETE_DOCUMENT = "deleteDocument";
  private static final String ACTION_DATABASE_EXISTS = "databaseExists";

  private static final String ACTION_SAVE_DOCUMENT = "saveDocument";
  private static final String ACTION_MUTABLE_DOCUMENT_SET_STRING = "mutableDocumentSetString";
  private static final String ACTION_GET_DOCUMENT = "getDocument";


  private static final String ACTION_SAVE_BLOB = "saveBlob";
  private static final String ACTION_SAVE_BLOB_EMBEDDED_RESOURCE = "saveBlobFromEmbeddedResource";
  private static final String ACTION_SAVE_BLOB_FILE_URL = "saveBlobFromFileUrl";
  private static final String ACTION_GET_BLOB = "getBlob";

  private static final String ACTION_QUERY_DATABASE = "queryDb";
  private static final String ACTION_CREATE_VALUE_INDEX = "createValueIndex";
  private static final String ACTION_CREATE_FTS_INDEX = "createFTSIndex";
  private static final String ACTION_DELETE_INDEX = "deleteIndex";

  private static final String ACTION_ENABLE_LOGGING = "enableLogging";


  private static final String ACTION_REPLICATOR_START = "replicatorStart";
  private static final String ACTION_REPLICATOR_STOP = "replicatorStop";
  private static final String ACTION_REPLICATION_ADD_LISTENER = "replicationAddChangeListener";
  private static final String ACTION_REPLICATION_REMOVE_LISTENER = "replicationRemoveChangeListener";

  private static final String QUERY_ADD_CHANGE_LISTENER = "queryAddChangeListener";
  private static final String QUERY_REMOVE_CHANGE_LISTENER = "queryRemoveChangeListener";

  private Context context;


  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

    context = this.cordova.getActivity().getApplicationContext();

    switch (action) {

      case ACTION_CREATE_OPEN_DATABASE:
        this.createOrOpenDatabase(args, callbackContext);
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
      case ACTION_MUTABLE_DOCUMENT_SET_STRING:
        mutableDocumentSetString(args, callbackContext);
        return true;

      case ACTION_SAVE_BLOB:
        saveBlob(args, callbackContext);
        return true;

      case ACTION_SAVE_BLOB_EMBEDDED_RESOURCE:
        saveBlobFromEmbeddedResource(args, callbackContext);
        return true;

      case ACTION_SAVE_BLOB_FILE_URL:
        saveBlobFromFileURL(args, callbackContext);
        return true;

      case ACTION_GET_BLOB:
        getBlob(args, callbackContext);
        return true;

      case ACTION_DELETE_DOCUMENT:
        deleteDocument(args, callbackContext);
        return true;

      case ACTION_ENABLE_LOGGING:
        enableLogging(args, callbackContext);
        return true;

      case ACTION_QUERY_DATABASE:
        cordova.getThreadPool().execute(new Runnable() {
          @Override
          public void run() {
            queryDb(args, callbackContext);
          }
        });

        return true;

      case ACTION_CREATE_VALUE_INDEX:
        createValueIndex(args, callbackContext);
        return true;

      case ACTION_CREATE_FTS_INDEX:
        createFTSIndex(args, callbackContext);
        return true;

      case ACTION_DELETE_INDEX:
        deleteIndex(args, callbackContext);
        return true;

      case ACTION_DATABASE_EXISTS:
        databaseExists(args, callbackContext);
        return true;

      case ACTION_REPLICATOR_START:
        replicatorStart(args, callbackContext);
        return true;

      case ACTION_REPLICATOR_STOP:
        replicatorStop(args, callbackContext);
        return true;

      case ACTION_REPLICATION_ADD_LISTENER:
        replicationAddListener(args, callbackContext);
        return true;

      case ACTION_REPLICATION_REMOVE_LISTENER:
        replicationRemoveListener(args, callbackContext);
        return true;

      case QUERY_ADD_CHANGE_LISTENER:
        queryAddChangeListener(args, callbackContext);
        return true;
      case QUERY_REMOVE_CHANGE_LISTENER:
        queryRemoveChangeListener(args, callbackContext);
        return true;
      default:
        return false;
    }

  }

  private DatabaseArgument parseDatabaseArguments(JSONObject dictionary, CallbackContext callbackContext) {
    DatabaseArgument argument = new DatabaseArgument();
    Context context = this.cordova.getActivity().getApplicationContext();

    try {
      String name = dictionary.has("dbName") ? dictionary.getString("dbName").toLowerCase() : null;
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
    DocumentArgument docArgument = new DocumentArgument();

    try {

      String dbName = dictionary.has("dbName") ? dictionary.getString("dbName").toLowerCase() : null;
      if (dbName == null || dbName.equals("")) {
        return null;
      }

      String documentId = dictionary.has("id") ? dictionary.getString("id") : null;
      JSONObject document = dictionary.has("document") ? (JSONObject) dictionary.get("document") : null;
      String blobData = dictionary.has("blobData") ? dictionary.getString("blobData") : null;
      String key = dictionary.has("key") ? dictionary.getString("key") : null;
      String value = dictionary.has("value") ? dictionary.getString("value") : null;
      String contentType = dictionary.has("contentType") ? dictionary.getString("contentType") : null;

      String acResource = dictionary.has("resourceName") ? dictionary.getString("resourceName") : null;
      String fileUrl = dictionary.has("fileUrl") ? dictionary.getString("fileUrl") : null;


      if (document != null && documentId != null) {
        docArgument.setDbName(dbName);
        docArgument.setId(documentId);
        docArgument.setDocument(document);
      } else if (blobData != null || acResource != null || fileUrl != null) {
        docArgument.setDbName(dbName);
        docArgument.setContentType(contentType);
        if (fileUrl != null) {
          docArgument.setFileUrl(fileUrl);
        } else if (acResource != null) {
          docArgument.setAcResource(acResource);
        } else {
          docArgument.setBlobData(blobData);
        }
      } else if (key != null && value != null) {
        docArgument.setDbName(dbName);
        docArgument.setId(documentId);
        docArgument.setKeyValue(key);
        docArgument.setValueValue(value);
      } else {
        docArgument.setDbName(dbName);
        docArgument.setId(documentId);
      }
      return docArgument;
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return null;
  }

  private ListenerArgument parseListenerArguments(JSONObject dictionary) {

    ListenerArgument listenerArgument = null;

    try {

      String dbName = dictionary.has("dbName") ? dictionary.getString("dbName").toLowerCase() : null;
      String jsCallback = dictionary.has("jsCallback") ? dictionary.getString("jsCallback") : null;


      if (dbName == null || dbName.equals("")) {
        return null;
      }

      if (jsCallback != null) {
        listenerArgument = new ListenerArgument(dbName, jsCallback);
      } else {
        listenerArgument = new ListenerArgument(dbName);
      }

      return listenerArgument;

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return null;
  }

  private void createOrOpenDatabase(JSONArray args, CallbackContext callbackContext) {

    try {
      DatabaseArgument dbArguments = this.parseDatabaseArguments(args.getJSONObject(0), callbackContext);
      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      ResultCode result = dbMgr.createOrOpenDatabase(dbArguments);

      PluginResult pluginResult;

      if (result == ResultCode.SUCCESS || result == ResultCode.DATABASE_ALREADY_EXISTS) {
        pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: couldn't create or open database");
      }
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

    } catch (JSONException e) {
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }

  }

  private void databaseExists(JSONArray args, CallbackContext callbackContext) {

    try {
      DatabaseArgument dbArguments = this.parseDatabaseArguments(args.getJSONObject(0), callbackContext);
      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      ResultCode result = dbMgr.databaseExists(dbArguments);

      PluginResult pluginResult;

      if (result == ResultCode.DATABASE_ALREADY_EXISTS) {
        pluginResult = new PluginResult(PluginResult.Status.OK, true);
      } else if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
        pluginResult = new PluginResult(PluginResult.Status.OK, false);
      } else {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error while checking database, make sure you are passing valid parameters.");
      }
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

    } catch (JSONException e) {
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }

  }

  private void closeDatabase(JSONArray args, CallbackContext callbackContext) {

    try {

      PluginResult pluginResult;
      String dbName = args.getString(0);
      if (dbName.equals("")) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
        return;
      }

      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      DatabaseArgument arg = new DatabaseArgument();
      arg.setName(dbName);
      ResultCode result = dbMgr.closeDatabase(arg);

      if (result == ResultCode.SUCCESS) {
        pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
      } else if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: couldn't close database");
      }

      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }
  }

  private void copyDatabase(JSONArray args, CallbackContext callbackContext) {

    DatabaseArgument newDbArgs;

    try {

      PluginResult pluginResult;
      JSONObject configs = args.getJSONObject(0);
      String resourceDbName = configs.getString("fromPath");

      if (resourceDbName.equals("")) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
        return;
      }

      JSONObject newConfig = configs.getJSONObject("newConfig");

      newDbArgs = parseDatabaseArguments(newConfig, callbackContext);

      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      ResultCode result = dbMgr.copyDatabase(resourceDbName, context, newDbArgs);

      if (result == ResultCode.SUCCESS) {
        pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
      } else if (result == ResultCode.INVALID_DB_NAME) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: trying to copy database but new name or current name isn't correctly passed as arguments.");
      } else if (result == ResultCode.MISSING_PARAM) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: error copying database, please ensure the resource file you are trying to copy exists.");
      }

      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

    } catch (JSONException e) {
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }
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
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: couldn't delete database");
      }

      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

    } catch (JSONException e) {
      e.printStackTrace();
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
    }

  }

  private void addChangeListener(JSONArray args, final CallbackContext callbackContext) {

    try {

      JSONObject params = args.getJSONObject(0);

      PluginResult pluginResult;
      ListenerArgument arguments = parseListenerArguments(params);

      if (arguments == null) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database name must be passed in as argument.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
        return;
      }

      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);


      CordovaWebView cdv = this.webView;
      ResultCode result = dbMgr.addChangeListener(arguments, cdv);

      if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
      } else if (result == ResultCode.ERROR) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: adding change listener.");
      } else if (result == ResultCode.CHANGE_LISTENER_ALREADY_EXISTS) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error:  listener token already exists for this database, remove first before trying to add a new listener.");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
      }

      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);
    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }

  }

  private void removeChangeListener(JSONArray args, CallbackContext callbackContext) {

    try {

      JSONObject params = args.getJSONObject(0);

      PluginResult pluginResult;
      ListenerArgument arguments = parseListenerArguments(params);

      if (arguments == null) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database name must be passed in as argument.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
        return;
      }

      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      ResultCode result = dbMgr.removeChangeListener(arguments, callbackContext);

      if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
      } else if (result == ResultCode.CHANGE_LISTENER_DOES_NOT_EXISTS) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: couldn't find listener token to remove");
      } else if (result == ResultCode.ERROR) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: error removing change listener.");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
      }

      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
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
            pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no databases found in collection, please open/create a database first");
          } else if (result.equals(String.valueOf(ResultCode.MISSING_PARAM))) {
            pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
          } else {
            pluginResult = new PluginResult(PluginResult.Status.OK, new JSONObject(result));
          }
        } else {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: document not found.");
        }

        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);

      } else {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
      }

    } catch (JSONException e) {
      e.printStackTrace();
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
    }

  }

  private void getBlob(JSONArray args, CallbackContext callbackContext) {

    try {

      JSONObject params = args.getJSONObject(0);

      DocumentArgument docArgs = parseDocumentArguments(params);
      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      String result = dbMgr.getBlob(docArgs);

      PluginResult pluginResult;

      if (result.equals(String.valueOf(ResultCode.DATABASE_DOES_NOT_EXIST))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
      } else if (result.equals(String.valueOf(ResultCode.EMPTY_OR_INVALID_IMAGE_DATA))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: invalid or empty blobData passed in.");
      } else if (result.equals(String.valueOf(ResultCode.ERROR))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: couldn't get blob from metadata passed in");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.OK, new JSONObject(result));
      }

      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }

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
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no document found in arguments");
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
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
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
        } else if (resultCode == ResultCode.DOCUMENT_DOES_NOT_EXIST) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: document does not exist.");
        } else {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error deleting document.");
        }

        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);


      } else {
        callbackContext.error("Error missing parameters.");
      }

    } catch (JSONException e) {
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
    }
  }

  private void mutableDocumentSetString(JSONArray args, CallbackContext callbackContext) {
    try {

      DocumentArgument docArguments = parseDocumentArguments(args.getJSONObject(0));

      if (docArguments != null) {

        DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
        String result = dbMgr.setKeyValuePairDocument(docArguments);

        PluginResult pluginResult;

        if (result.equals(ResultCode.MISSING_PARAM.toString())) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: invalid or missing parameters.");
        } else if (result.equals(ResultCode.DATABASE_DOES_NOT_EXIST.toString())) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
        } else if (result.equals(ResultCode.ERROR)) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: error creating document with specified key value.");
        } else {
          pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
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
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
    }
  }

  private void saveBlob(JSONArray args, CallbackContext callbackContext) {

    try {

      JSONObject params = args.getJSONObject(0);

      DocumentArgument docArgs = parseDocumentArguments(params);
      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      String result = dbMgr.saveBlob(docArgs);

      PluginResult pluginResult;
      if (result == null) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: trying to save blob data produced error.");
      } else if (result.equals(String.valueOf(ResultCode.EMPTY_IMAGE_DATA))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing argument image data.");
      } else if (result.equals(String.valueOf(ResultCode.DATABASE_DOES_NOT_EXIST))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
      } else if (result.equals(String.valueOf(ResultCode.DOCUMENT_DOES_NOT_EXIST))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: document does not exist.");
      } else if (result.equals(String.valueOf(ResultCode.MISSING_PARAM))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing arguments.");
      } else if (result.equals(String.valueOf(ResultCode.ERROR))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: error setting Blob value.");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.OK, new JSONObject(result));
      }

      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }

  }

  private void saveBlobFromEmbeddedResource(JSONArray args, CallbackContext callbackContext) {

    try {

      JSONObject params = args.getJSONObject(0);

      DocumentArgument docArgs = parseDocumentArguments(params);
      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      String result = dbMgr.saveBlobFromEmbeddedResource(docArgs, context);

      PluginResult pluginResult;
      if (result == null) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: trying to save blob data produced error.");
      } else if (result.equals(String.valueOf(ResultCode.EMPTY_OR_INVALID_IMAGE_DATA))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing or invalid argument acResource, make sure you have passed correct resource value.");
      } else if (result.equals(String.valueOf(ResultCode.DATABASE_DOES_NOT_EXIST))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
      } else if (result.equals(String.valueOf(ResultCode.DOCUMENT_DOES_NOT_EXIST))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: document does not exist.");
      } else if (result.equals(String.valueOf(ResultCode.MISSING_PARAM))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing arguments.");
      } else if (result.equals(String.valueOf(ResultCode.ERROR))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: error setting Blob value.");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.OK, new JSONObject(result));
      }

      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }
  }

  private void saveBlobFromFileURL(JSONArray args, CallbackContext callbackContext) {

    try {

      JSONObject params = args.getJSONObject(0);

      DocumentArgument docArgs = parseDocumentArguments(params);
      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      String result = dbMgr.saveBlobFromFileURL(docArgs, context);

      PluginResult pluginResult;

      if (result == null) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: trying to save blob data produced error.");
      } else if (result.equals(String.valueOf(ResultCode.EMPTY_OR_INVALID_IMAGE_DATA))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing or invalid argument fileURL, make sure you have passed correct file path value.");
      } else if (result.equals(String.valueOf(ResultCode.DATABASE_DOES_NOT_EXIST))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
      } else if (result.equals(String.valueOf(ResultCode.DOCUMENT_DOES_NOT_EXIST))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: document does not exist.");
      } else if (result.equals(String.valueOf(ResultCode.MISSING_PARAM))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing arguments.");
      } else if (result.equals(String.valueOf(ResultCode.ERROR))) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: error setting Blob value.");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.OK, new JSONObject(result));
      }

      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }
  }

  private void enableLogging(JSONArray args, CallbackContext callbackContext) {

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

  private QueryArgument parseQueryArguments(JSONObject dictionary, CallbackContext callbackContext) {

    QueryArgument argument = new QueryArgument();

    try {
      String name = dictionary.has("dbName") ? dictionary.getString("dbName").toLowerCase() : null;
      String query = dictionary.has("query") ? dictionary.getString("query") : null;
      String jsCallback = dictionary.has("jsCallback") ? dictionary.getString("jsCallback") : null;

      if (name == null || query == null) { //|| jsCallback == null || jsCallback.isEmpty()

        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing arguments.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
        return null;
      }

      argument.setDatabaseName(name);
      argument.setQuery(query);
      argument.setJSCallback(jsCallback);

      return argument;

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return argument;

  }

  private void queryDb(JSONArray args, CallbackContext callbackContext) {
    try {

      JSONObject params = args.getJSONObject(0);
      QueryArgument queryArgument = parseQueryArguments(params, callbackContext);

      if (queryArgument != null) {
        DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
        ResultCode result = dbMgr.query(queryArgument, callbackContext);
        if (result == ResultCode.SUCCESS) return;

        PluginResult pluginResult;
        if (result == ResultCode.JSON_ERROR) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: error parsing JSON on result set.");
        } else if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
        } else if (result == ResultCode.MISSING_PARAM) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing arguments.");
        } else {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: error in executing query.");
        }
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
      }

    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }
  }

  private ValueIndexArgument parseValueIndexQuery(JSONObject dictionary, CallbackContext callbackContext) {


    ValueIndexArgument argument = new ValueIndexArgument();

    try {
      String name = dictionary.has("dbName") ? dictionary.getString("dbName").toLowerCase() : null;
      String indexName = dictionary.has("indexName") ? dictionary.getString("indexName") : null;
      JSONArray indexExprValArr = dictionary.has("indexExpressions") ? dictionary.getJSONArray("indexExpressions") : null;

      if (name == null || indexName == null || indexExprValArr == null || indexExprValArr.length() <= 0) {

        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing arguments.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);

        return null;
      }

      argument.setDatabaseName(name);
      argument.setIndexName(indexName);


      List<String> indexes = new ArrayList<>();
      for (int i = 0; i < indexExprValArr.length(); i++) {
        indexes.add(indexExprValArr.getString(i));
      }
      argument.setIndexExpressions(indexes);


      return argument;

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return argument;

  }

  private void createValueIndex(JSONArray args, CallbackContext callbackContext) {

    try {

      JSONObject params = args.getJSONObject(0);
      ValueIndexArgument argument = parseValueIndexQuery(params, callbackContext);

      if (argument != null) {
        DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
        ResultCode result = dbMgr.createValueIndex(argument);

        PluginResult pluginResult;
        if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
        } else if (result == ResultCode.MISSING_PARAM) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing arguments.");
        } else if (result == ResultCode.ERROR) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: creating Value Index on database.");
        } else {
          pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
        }

        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
      }

    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }
  }

  private FTSIndexArgument parseFTSIndexQuery(JSONObject dictionary, CallbackContext callbackContext) {


    FTSIndexArgument argument = new FTSIndexArgument();

    try {
      String name = dictionary.has("dbName") ? dictionary.getString("dbName").toLowerCase() : null;
      String indexName = dictionary.has("indexName") ? dictionary.getString("indexName") : null;
      JSONArray indexExprValArr = dictionary.has("indexExpressions") ? dictionary.getJSONArray("indexExpressions") : null;
      String language = dictionary.has("language") ? dictionary.getString("language") : null;
      String ignoreAccents = dictionary.has("ignoreAccents") ? dictionary.getString("ignoreAccents") : null;


      if (name == null || indexName == null || indexExprValArr == null || indexExprValArr.length() <= 0) {

        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing arguments.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);

        return null;
      }

      argument.setDatabaseName(name);
      argument.setIndexName(indexName);
      argument.setIgnoreAccents(ignoreAccents != null && ignoreAccents.toLowerCase().equals("true"));
      argument.setLanguage(language);

      List<String> indexes = new ArrayList<>();
      for (int i = 0; i < indexExprValArr.length(); i++) {
        indexes.add(indexExprValArr.getString(i));
      }

      argument.setIndexExpressions(indexes);

      return argument;

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return argument;

  }

  private void createFTSIndex(JSONArray args, CallbackContext callbackContext) {

    try {

      JSONObject params = args.getJSONObject(0);
      FTSIndexArgument argument = parseFTSIndexQuery(params, callbackContext);

      if (argument != null) {
        DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
        ResultCode result = dbMgr.createFTSIndex(argument);

        PluginResult pluginResult;
        if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
        } else if (result == ResultCode.MISSING_PARAM) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing arguments.");
        } else if (result == ResultCode.ERROR) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: creating FTS Index on database.");
        } else {
          pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
        }

        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
      }

    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }
  }

  private DeleteIndexArgument parseDeleteIndexQuery(JSONObject dictionary, CallbackContext callbackContext) {


    DeleteIndexArgument argument = new DeleteIndexArgument();

    try {
      String name = dictionary.has("dbName") ? dictionary.getString("dbName").toLowerCase() : null;
      String indexName = dictionary.has("indexName") ? dictionary.getString("indexName") : null;

      if (name == null || indexName == null) {

        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing arguments.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);

        return null;
      }

      argument.setDatabaseName(name);
      argument.setIndexName(indexName);

      return argument;

    } catch (JSONException e) {
      e.printStackTrace();
    }

    return argument;
  }

  private void deleteIndex(JSONArray args, CallbackContext callbackContext) {
    try {

      JSONObject params = args.getJSONObject(0);
      DeleteIndexArgument argument = parseDeleteIndexQuery(params, callbackContext);

      if (argument != null) {
        DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
        ResultCode result = dbMgr.deleteIndex(argument);

        PluginResult pluginResult;
        if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
        } else if (result == ResultCode.MISSING_PARAM) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing arguments.");
        } else if (result == ResultCode.ERROR) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: error deleting index from Database.");
        } else {
          pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
        }

        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
      }

    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }
  }

  private void replicatorStart(JSONArray args, CallbackContext callbackContext) {

    try {

      ReplicatorConfiguration config = parseReplicatorConfiguration(args.getJSONObject(0), callbackContext);

      if (config != null) {

        DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
        ResultCode result = dbMgr.replicatorStart(config);

        PluginResult pluginResult;
        if (result == ResultCode.SUCCESS) {
          pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
        } else if (result == ResultCode.MISSING_PARAM) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Invalid or missing parameters");
        } else if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Database does not exist.");
        } else {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Error trying to start replicator");
        }

        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);

      } else {

        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Invalid or missing parameters.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
      }

    } catch (JSONException e) {
      e.printStackTrace();
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
    }

  }

  private void replicatorStop(JSONArray args, CallbackContext callbackContext) {

    try {

      PluginResult pluginResult;
      JSONObject config = args.getJSONObject(0);

      String dbName = config.has("dbName") ? config.getString("dbName") : null;

      if (dbName == null || dbName.isEmpty()) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
        return;
      }

      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      ResultCode resultCode = dbMgr.replicatorStop(dbName);

      if (resultCode == ResultCode.SUCCESS) {
        pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
      } else if (resultCode == ResultCode.DATABASE_DOES_NOT_EXIST) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no databases found in collection, please open/create a database first.");
      } else if (resultCode == ResultCode.REPLICATOR_DOES_NOT_EXIST) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: replicator not found.");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "Error stopping replicator.");
      }

      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

    } catch (JSONException e) {
      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
    }

  }

  private void replicationAddListener(JSONArray args, CallbackContext callbackContext) {
    try {

      JSONObject params = args.getJSONObject(0);

      PluginResult pluginResult;
      ListenerArgument arguments = parseListenerArguments(params);

      if (arguments == null) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database name must be passed in as argument.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
        return;
      }

      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);

      CordovaWebView cdv = this.webView;
      ResultCode result = dbMgr.replicationAddChangeListener(arguments, cdv);

      if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
      } else if (result == ResultCode.REPLICATOR_DOES_NOT_EXIST) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: replicator does not exist. Please create a replicator first to add the listener.");
      } else if (result == ResultCode.ERROR) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: adding change listener.");
      } else if (result == ResultCode.REPLICATOR_LISTENER_ALREADY_EXISTS) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error:  replicator listener token already exists for this database, remove first before trying to add a new listener.");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
      }

      pluginResult.setKeepCallback(true);
      callbackContext.sendPluginResult(pluginResult);

    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }

  }

  private void replicationRemoveListener(JSONArray args, CallbackContext callbackContext) {

    try {

      JSONObject params = args.getJSONObject(0);

      PluginResult pluginResult;
      ListenerArgument arguments = parseListenerArguments(params);

      if (arguments == null) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database name must be passed in as argument.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
        return;
      }

      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      ResultCode result = dbMgr.replicationRemoveChangeListener(arguments);

      if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
      } else if (result == ResultCode.REPLICATOR_LISTENER_DOES_NOT_EXISTS) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: couldn't find replicator listener token to remove");
      } else if (result == ResultCode.ERROR) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: error removing change listener.");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
      }

      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }
  }

  private void queryAddChangeListener(JSONArray args, CallbackContext callbackContext) {
    try {

      JSONObject params = args.getJSONObject(0);
      QueryArgument queryArgument = parseQueryArguments(params, callbackContext);

      if (queryArgument != null) {
        DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
        CordovaWebView cdv = this.webView;
        ResultCode result = dbMgr.queryAddChangeListener(queryArgument, cdv);

        PluginResult pluginResult;
        if (result == ResultCode.SUCCESS) {
          pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
        } else if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
        } else if (result == ResultCode.INVALID_QUERY) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: Invalid Query.");
        } else if (result == ResultCode.COULD_NOT_CREATE_QUERY) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "couldn't create new query to get key based on query description.");
        } else if (result == ResultCode.QUERY_LISTENER_ALREADY_EXISTS) {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: query listener token already exists for this database, remove first before trying to add a new listener.");
        } else {
          pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: error in executing query.");
        }
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
      }

    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }
  }

  private void queryRemoveChangeListener(JSONArray args, CallbackContext callbackContext) {

    try {

      JSONObject params = args.getJSONObject(0);

      String queryString = params.has("query") ? params.getString("query") : null;
      String dbName = params.has("dbName") ? params.getString("dbName") : null;

      PluginResult pluginResult;
      if (dbName == null) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database name must be passed in as argument.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
        return;
      }

      if (queryString == null || queryString.isEmpty()) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: query must be passed in as argument.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);
        return;
      }

      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      ResultCode result = dbMgr.queryRemoveChangeListener(queryString, dbName);

      if (result == ResultCode.DATABASE_OR_QUERY_RESOURCE_DOES_NOT_EXIST) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error:  no databases or query resources found in collection, please open/create a database first or create query listener first.");
      } else if (result == ResultCode.QUERY_RESOURCE_DOES_NOT_EXIST) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: couldn't find query resource, make sure it exists before removing the listener.");
      } else if (result == ResultCode.QUERY_LISTENER_DOES_NOT_EXIST) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: couldn't find query listener token to remove.");
      } else if (result == ResultCode.ERROR) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: error removing query change listener.");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.OK, "OK");
      }

      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);

    } catch (JSONException e) {

      PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
      pluginResult.setKeepCallback(false);
      callbackContext.sendPluginResult(pluginResult);
      e.printStackTrace();
    }
  }

  private ReplicatorConfiguration parseReplicatorConfiguration(JSONObject dictionary, CallbackContext callbackContext) {
    ReplicatorConfiguration config = null;
    Database database;

    try {
      String dbName = dictionary.has("databaseName") ? dictionary.getString("databaseName").toLowerCase() : null;
      String targetUrl = dictionary.has("target") ? dictionary.getString("target") : null;

      if (dbName == null || dbName.isEmpty() || targetUrl == null || targetUrl.isEmpty()) {

        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database name and targetUrl must be passed in as arguments.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);

        return null;
      }

      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      DatabaseResource dbr = dbMgr.getDatabases().get(dbName);
      if (dbr != null) {
        database = dbr.getDatabase();
      } else {
        DatabaseArgument dbArgs = new DatabaseArgument();
        dbArgs.setName(dbName);

        ResultCode resultCode = dbMgr.createOrOpenDatabase(dbArgs);

        if (resultCode == ResultCode.ERROR) {
          PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: couldn't open database for replication.");
          pluginResult.setKeepCallback(false);
          callbackContext.sendPluginResult(pluginResult);
          return null;
        } else {
          DatabaseResource dbr2 = dbMgr.getDatabases().get(dbName);
          database = dbr2.getDatabase();
        }
      }

      URI url = new URI(targetUrl);

      if (database.getName().equals(dbName)) {
        config = new ReplicatorConfiguration(database, new URLEndpoint(url));

        if (dictionary.has("continuous")) {
          config.setContinuous(dictionary.getBoolean("continuous"));
        }

        if (dictionary.has("headers")) {

          JSONArray headersArr = dictionary.getJSONArray("headers");

          if (headersArr.length() > 0) {
            Map<String, String> headerMap = new HashMap<>();
            for (int i = 0; i < headersArr.length(); i++) {
              JSONObject obj = headersArr.getJSONObject(i);
              String k = obj.keys().next();
              String v = obj.getString(k);
              headerMap.put(k, v);
            }
            config.setHeaders(headerMap);
          } else {
            config.setHeaders(null);
          }
        }

        if (dictionary.has("channels")) {

          JSONArray channelsArr = dictionary.getJSONArray("channels");

          if (channelsArr.length() > 0) {

            List<String> channels = new ArrayList<>();
            for (int i = 0; i < channelsArr.length(); i++) {
              channels.add(channelsArr.getString(i));
            }
            config.setChannels(channels);
          } else {
            config.setChannels(null);
          }
        }

        if (dictionary.has("documentIds")) {

          JSONArray documentIdsArr = dictionary.getJSONArray("documentIds");

          if (documentIdsArr.length() > 0) {

            List<String> documentIds = new ArrayList<>();
            for (int i = 0; i < documentIdsArr.length(); i++) {
              documentIds.add(documentIdsArr.getString(i));
            }
            config.setDocumentIDs(documentIds);
          } else {
            config.setDocumentIDs(null);
          }
        }

        if (dictionary.has("replicatorType")) {
          // TODO
          //config.setReplicatorType()  Deprecated ??

                  /*  String replicatorType = dictionary.getString("replicatorType");

                    switch(replicatorType) {
                        case "PULL":
                            config.setReplicatorType(ReplicatorConfiguration.ReplicatorType.PULL);
                            break;
                        case "PUSH":
                            config.setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH);
                            break;
                        default:
                            config.setReplicatorType(ReplicatorConfiguration.ReplicatorType.PUSH_AND_PULL);
                    }*/

        }

        if (dictionary.has("allowReplicatingInBackground")) {
          // TODO
          // not available for android ??;
        }

        if (dictionary.has("acceptOnlySelfSignedServerCertificate")) {
          config.setAcceptOnlySelfSignedServerCertificate(dictionary.getBoolean("acceptOnlySelfSignedServerCertificate"));
        }

        if (dictionary.has("pinnedServerCertificateUri")) {
          String pinnedServerCertificateUri = dictionary.getString("pinnedServerCertificateUri");
          byte[] pinnedServerCert = this.getPinnedCertFile(context, pinnedServerCertificateUri);
          // Set pinned certificate.
          config.setPinnedServerCertificate(pinnedServerCert);
        }

        if (dictionary.has("heartbeat")) {
          config.setHeartbeat(dictionary.getInt("heartbeat"));
        }

        if (dictionary.has("authenticator")) {
          JSONObject authObj = dictionary.getJSONObject("authenticator");

          if (authObj.has("authType") && authObj.getString("authType").equalsIgnoreCase("Basic")) {
            String username = authObj.has("username") ? authObj.getString("username") : null;
            String password = authObj.has("password") ? authObj.getString("password") : null;

            if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
              char[] passwordCharArray = new char[password.length()];

              for (int i = 0; i < password.length(); i++) {
                passwordCharArray[i] = password.charAt(i);
              }
              config.setAuthenticator(new BasicAuthenticator(username, passwordCharArray));
            }
          } else if (authObj.has("authType") && authObj.getString("authType").equalsIgnoreCase("Session")) {
            String sessionId = authObj.getString("sessionId");
            if (!sessionId.isEmpty()) {
              String cookieName = authObj.getString("cookieName");
              if (!cookieName.isEmpty()) {
                config.setAuthenticator(new SessionAuthenticator(sessionId, cookieName));
              } else {
                config.setAuthenticator(new SessionAuthenticator(sessionId));
              }
            }
          }
        }
      }

    } catch (JSONException | URISyntaxException e) {
      config = null;
      e.printStackTrace();
    }
    return config;
  }

  private byte[] getPinnedCertFile(Context context, String resource) {
    AssetManager assetManager = context.getAssets();
    InputStream is = null;
    try {
      is = assetManager.open(resource + ".cer");
      return new byte[is.available()];
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
