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
import com.couchbase.lite.ListenerToken;

import java.util.concurrent.Executor;

public class DatabaseManager {
    private static Database database;
    private static DatabaseManager instance = null;
    private ListenerToken listenerToken;

    protected DatabaseManager() {

    }

    public static DatabaseManager getSharedInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }

        return instance;
    }


    public static Database getDatabase() {
        return database;
    }


    public void initCouchbaseLite(Context context) {
        CouchbaseLite.init(context);
    }

   
    public void openOrCreateDatabase(Context context, String dbName, String dirName)
   
    {
      
        DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(String.format("%s/%s", context.getFilesDir(), dirName));
      

        try {
         
            database = new Database(dbName, config);
          
            registerForDatabaseChanges();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    
    private void registerForDatabaseChanges()
    
    {
        
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

    public boolean closeDatabase()
   
    {
        try {
            if (database != null) {
                deregisterForDatabaseChanges();
               
                database.close();
               
                database = null;
                return true;
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return false;
    }

   
    private void deregisterForDatabaseChanges()
    
    {
        if (listenerToken != null) {
            
            database.removeChangeListener(listenerToken);
            
        }
    }
}
