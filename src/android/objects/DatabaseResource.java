package cordova.plugin.couchbaselite.objects;

import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;

public class DatabaseResource {

    private Database database;
    private DatabaseConfiguration configuration;


    public DatabaseResource(Database db, DatabaseConfiguration config) {
        this.database = db;
        this.configuration = config;
    }


    public DatabaseResource(Database db) {
        this.database = db;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public DatabaseConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(DatabaseConfiguration configuration) {
        this.configuration = configuration;
    }


}
