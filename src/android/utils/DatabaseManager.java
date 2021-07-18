package cordova.plugin.couchbaselite.utils;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseChange;
import com.couchbase.lite.DatabaseChangeListener;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.EncryptionKey;
import com.couchbase.lite.ListenerToken;
import com.couchbase.lite.MutableDocument;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cordova.plugin.couchbaselite.enums.ResultCode;
import cordova.plugin.couchbaselite.objects.DatabaseArgument;
import cordova.plugin.couchbaselite.objects.DatabaseResource;
import cordova.plugin.couchbaselite.objects.DocumentArgument;

public class DatabaseManager {

    private static Map<String, DatabaseResource> databases;

    private static Database database;
    private static DatabaseManager instance = null;
    private ListenerToken listenerToken;


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


    public ResultCode createDatabase(DatabaseArgument dbArgument) {
        try {

            DatabaseConfiguration dbConfig = getDatabaseConfig(dbArgument);

            if (databases.get(dbArgument.name) != null) { // Database already exists
                return ResultCode.EXIST;
            }

            if (dbArgument.name != null && dbConfig != null) {
                Database database = new Database(dbArgument.name, dbConfig);
                databases.put(dbArgument.name, new DatabaseResource(database, dbConfig));
                return ResultCode.SUCCESS;
            } else if (dbArgument.name != null) {
                Database database = new Database(dbArgument.name);
                databases.put(dbArgument.name, new DatabaseResource(database));
                return ResultCode.SUCCESS;
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return ResultCode.ERROR;
    }

    public ResultCode deleteDatabase(DatabaseArgument dbArgument) {
        try {

            if (!databases.containsKey(dbArgument.name)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST;
            }

            if (dbArgument.name != null && !dbArgument.name.equals("")) {
                DatabaseResource resource = databases.get(dbArgument.name);
                resource.getDatabase().delete();
                databases.remove(dbArgument.name);
                return ResultCode.SUCCESS;
            }

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return ResultCode.ERROR;
    }

    public ResultCode copyDatabase(DatabaseArgument currentDbArgs, DatabaseArgument newDbArgs) {
        try {
            File oldDbpath = new File(currentDbArgs.getDirectory());
            DatabaseConfiguration config = getDatabaseConfig(newDbArgs);

            if (oldDbpath != null) {
                Database.copy(oldDbpath, newDbArgs.getName(), config);
                return ResultCode.SUCCESS;
            }

            return ResultCode.ERROR;

        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return ResultCode.ERROR;
    }


    private DatabaseConfiguration getDatabaseConfig(DatabaseArgument args) {
        if ((args.directory == null && args.encryptionKey == null) || (args.directory.equals("") && args.encryptionKey.equals(""))) {
            return null;
        }

        DatabaseConfiguration dbConfig = new DatabaseConfiguration();

        if (args.directory != null && !args.directory.equals("")) {
            dbConfig.setDirectory(args.directory);
        }

        if (args.encryptionKey != null && !args.encryptionKey.equals("")) {
            dbConfig.setEncryptionKey(new EncryptionKey(args.encryptionKey));
        }

        return dbConfig;
    }

    private void registerForDatabaseChanges() {

        listenerToken = database.addChangeListener(new DatabaseChangeListener() {
            @Override
            public void changed(final DatabaseChange change) {
                if (change != null) {
                    for (String docId : change.getDocumentIDs()) {
                        Document doc = database.getDocument(docId);
                        if (doc != null) {
                            Log.i("DatabaseChangeEvent", "Document was added/updated");
                        } else {
                            Log.i("DatabaseChangeEvent", "Document was deleted");
                        }
                    }
                }
            }
        });
    }

    public ResultCode closeDatabase(DatabaseArgument dbArgument) {
        try {

            if (!databases.containsKey(dbArgument.name)) {
                return ResultCode.DATABASE_DOES_NOT_EXIST;
            }

            if (dbArgument.name != null && !dbArgument.name.equals("")) {
                DatabaseResource resource = databases.get(dbArgument.name);
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

   /* private void deregisterForDatabaseChanges() {
        if (listenerToken != null) {
            database.removeChangeListener(listenerToken);
        }
    }*/
}
