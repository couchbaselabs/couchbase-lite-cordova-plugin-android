package com.couchbase.cblite;

import android.content.Context;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.couchbase.cblite.enums.ResultCode;
import com.couchbase.cblite.objects.DeleteIndexArgument;
import com.couchbase.cblite.objects.DocumentArgument;
import com.couchbase.cblite.objects.FTSIndexArgument;
import com.couchbase.cblite.objects.ListenerArgument;
import com.couchbase.cblite.objects.QueryArgument;
import com.couchbase.cblite.objects.ValueIndexArgument;
import com.couchbase.cblite.utils.DatabaseManager;
import com.couchbase.cblite.objects.DatabaseArgument;

import java.util.ArrayList;
import java.util.List;

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

  private static final String ACTION_SAVE_DOCUMENT = "saveDocument";
  private static final String ACTION_MUTABLE_DOCUMENT_SET_STRING = "mutableDocumentSetString";
  private static final String ACTION_GET_DOCUMENT = "getDocument";


  private static final String ACTION_MUTABLE_DOCUMENT_SET_BLOB = "mutableDocumentSetBlob";
  private static final String ACTION_MUTABLE_DOCUMENT_SET_BLOB_EMBEDDED = "mutableDocumentSetBlobFromEmbeddedResource";
  private static final String ACTION_MUTABLE_DOCUMENT_SET_BLOB_FILE_URL = "mutableDocumentSetBlobFromFileUrl";
  private static final String ACTION_GET_BLOB = "getBlob";

  private static final String ACTION_QUERY_DATABASE = "queryDb";
  private static final String ACTION_CREATE_VALUE_INDEX = "createValueIndex";
  private static final String ACTION_CREATE_FTS_INDEX = "createFTSIndex";
  private static final String ACTION_DELETE_INDEX = "deleteIndex";


  private static final String ACTION_ENABLE_LOGGING = "enableLogging";

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

      case ACTION_MUTABLE_DOCUMENT_SET_BLOB:
        mutableDocumentSetBlob(args, callbackContext);
        return true;

      case ACTION_MUTABLE_DOCUMENT_SET_BLOB_EMBEDDED:
        mutableDocumentSetBlobFromEmbeddedResource(args, callbackContext);
        return true;

      case ACTION_MUTABLE_DOCUMENT_SET_BLOB_FILE_URL:
        mutableDocumentSetBlobFromFileURL(args, callbackContext);
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
      } else if (blobData != null && documentId != null) {
        docArgument.setDbName(dbName);
        docArgument.setId(documentId);
        docArgument.setBlobData(blobData);
        docArgument.setContentType(contentType);
        docArgument.setKeyValue(key);
      } else if (blobData != null && documentId == null) {
        docArgument.setDbName(dbName);
        docArgument.setBlobData(blobData);
      } else if (key != null && value != null) {
        docArgument.setDbName(dbName);
        docArgument.setId(documentId);
        docArgument.setKeyValue(key);
        docArgument.setValueValue(value);
      } else if (acResource != null && documentId != null) {
        docArgument.setDbName(dbName);
        docArgument.setId(documentId);
        docArgument.setAcResource(acResource);
        docArgument.setKeyValue(key);
        docArgument.setContentType(acResource);
      } else if (fileUrl != null && documentId != null) {
        docArgument.setDbName(dbName);
        docArgument.setId(documentId);
        docArgument.setKeyValue(key);
        docArgument.setFileUrl(fileUrl);
        docArgument.setContentType(contentType);
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

  private void closeDatabase(JSONArray args, CallbackContext callbackContext) {

    try {


      String dbName = args.getString(0);
      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      DatabaseArgument arg = new DatabaseArgument();
      arg.setName(dbName);
      ResultCode result = dbMgr.closeDatabase(arg);


      PluginResult pluginResult;
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
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
      } else if (result == ResultCode.DATABASE_ALREADY_EXISTS) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database with same name already exists.");
      } else {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: no arguments or invalid arguments passed in.");
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
      ResultCode result = dbMgr.addChangeListener(arguments, callbackContext);

      if (result == ResultCode.DATABASE_DOES_NOT_EXIST) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: database does not exist.");
      } else if (result == ResultCode.ERROR) {
        pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: adding change listener.");
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

  private void mutableDocumentSetBlob(JSONArray args, CallbackContext callbackContext) {

    try {

      JSONObject params = args.getJSONObject(0);

      DocumentArgument docArgs = parseDocumentArguments(params);
      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      String result = dbMgr.createDocumentBlob(docArgs);

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

  private void mutableDocumentSetBlobFromEmbeddedResource(JSONArray args, CallbackContext callbackContext) {

    try {

      JSONObject params = args.getJSONObject(0);

      DocumentArgument docArgs = parseDocumentArguments(params);
      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      String result = dbMgr.createDocumentBlobFromEmbeddedResource(docArgs, context);

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

  private void mutableDocumentSetBlobFromFileURL(JSONArray args, CallbackContext callbackContext) {

    try {

      JSONObject params = args.getJSONObject(0);

      DocumentArgument docArgs = parseDocumentArguments(params);
      DatabaseManager dbMgr = DatabaseManager.getSharedInstance(context);
      String result = dbMgr.createDocumentBlobFromFileURL(docArgs, context);

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

      if (name == null || query == null) {

        PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, "error: missing arguments.");
        pluginResult.setKeepCallback(false);
        callbackContext.sendPluginResult(pluginResult);

        return null;
      }

      argument.setDatabaseName(name);
      argument.setQuery(query);

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
      JSONArray indexExprValArr = dictionary.has("indexes") ? dictionary.getJSONArray("indexes") : null;

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
      JSONArray indexExprValArr = dictionary.has("indexes") ? dictionary.getJSONArray("indexes") : null;
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


}
