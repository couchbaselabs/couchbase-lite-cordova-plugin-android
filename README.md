# overview
A reference implementation of a [cordova native plugin](https://cordova.apache.org/docs/en/10.x/guide/hybrid/plugins/index.html) for couchbase lite on Android. 

In order to use Couchbase Lite as embedded database within your Cordova-based app, you will need a way to access Couchbase Lite’s native APIs from within your Cordova web application. Cordova plugins allow web-based apps running in a Cordova webview to access native platform functionality through a Javascript interface.

The Cordova plugin example exports a subset of native Couchbase Lite APIs and makes it available to Cordova apps. This is intended to be used as a reference. You can extend this plugin to expose other relevant APIs per [plugin development guide](https://cordova.apache.org/docs/en/10.x/guide/platforms/android/plugin.html) 

**NOTE**: The plugin **does not** bundle Couchbase Lite native framework. You will include Couchbase Lite library when building your Cordova or Ionic app. The Getting Started instructions below describes the same.

**LICENSE**: The source code for the plugin is Apache-licensed, as specified in LICENSE. However, the usage of Couchbase Lite will be guided by the terms and conditions specified in Couchbase's Enterprise or Community License agreements.


![](https://i0.wp.com/blog.couchbase.com/wp-content/uploads/2018/10/JS-stuff.jpg?w=900)

## Exported APIs
The following is list of APIs (& features) exported by the plugin. 


| API methods | Native Class | Description |
| :---: | :---: | :--- |
| createOrOpenDatabase (with specified Configuration) | Database | Initializes a Couchbase Lite database with a given name and database options. If the database does not yet exist, it will be created. |
| DatabaseConfiguration | Database | Helper method to create database configuration object. |
| checkDatabase | Database | Checks if database exists in the given directory path. |
| closeDatabase | Database | Close database synchronously. Before closing the database, the active replicators, listeners and live queries will be stopped. |
| copyDatabase | Database | Copies a canned databaes from the given path to a new database with the given name and the configuration. The new database will be created at the directory specified in the configuration. Without given the database configuration, the default configuration that is equivalent to setting all properties in the configuration to nil will be used.  |
| dbAddListener | Database | Adds a database change listener. Changes will be posted on the main thread(android)/queue(ios) |
| dbRemoveListener | Database | Removes existing database change listener. |
| saveDocument (With JSON OBJECT) | MutableDocument | Saves a document to the database. When write operations are executed concurrently, the last writer will overwrite all other written values. |
| mutableDocumentSetString | MutableDocument | Updates an existing document with a key/value pair property that is of string value. |
| getDocument | MutableDocument | Gets a Document object with the given ID. |
| deleteDocument | MutableDocument | Delete a document in the database. When write operations are executed concurrently, the last writer will overwrite all other written values.|
| saveBlob | Database | Saves blob on the database. NOTE: successCallback returns metadata that must be used to retreive the blob using the getBlob function.  It's very important that you keep a reference to the metadata if you need to retreive the blob. |
| saveBlobFromEmbeddedResource | Database | Saves Blob object from a file that is embedded in the Native project (AssetCatalog for iOS and Resource folder for Android).  NOTE: successCallback returns metadata that must be used  to retreive the blob using the getBlob function.  It's very important that you keep a reference to the metadata if you need to retreive the blob.  This is a helper function for javascript developers with no Native equivilant call |
| saveBlobFromFileURL | Database | Saves Blob object from a file that is saved on the device in a folder accessible by the application. This might require application configuration changes to allow application to read files stored on the device.|
| getBlob  | Database | Get a Blob object for the given metadata. |
| enableConsoleLogging  | Database | Allows to configure console logging. Logs are printed on native IDE. Very useful for debugging. |
| createValueIndex  | Database | Creates a value index for regular queries. |
| createFTSIndex  | Database | Creates a full-text search index for full-text search query with the match operator. |
| deleteIndex  | Database | Deletes an index |
| query  | Query | N1QL query to execute against the database and return results. |
| queryAddListener | Query | Adds a query change listener. Changes will be posted on the main thread(android)/queue(ios). |
| queryRemoveListener | Query | Removes a query change listener. |
| ReplicatorConfiguration | Replicator | Helper method to create replicator configuration object. |
| start | Replicator | Initializes a replicator with the given configuration. The replicator is used for  replicating document changes between a local database and a target database. The replicator can be bidirectional or either push or pull. The replicator can also be one-short or continuous. The replicator runs asynchronously, so observe the status property to be notified of progress. |
| stop | Replicator | Stops a running replicator. This method returns immediately; when the replicator actually stops, the replicator will change its status’s activity level to stopped and the replicator change notification will be notified accordingly. |
| BasicAuthenticator | BasicAuthentication  | The BasicAuthentiatior is an authenticator that will authenticate using HTTP Basic auth with the given username and password. This should only be used over an SSL/TLS connection, as otherwise it's very easy for anyone sniffing network traffic to read the password. |
| SessionAuthenticator | SessionAuthentication | The SessionAuthenticatior is an authenticator that will authenticate by using the session ID of the session created by a Sync Gateway |
| addChangeListener | Replicator | Adds a replication change listener. Changes will be posted on the main thread(android)/queue(ios). |
| removeChangeListener | Replicator | Removes the replication change listener. |


## Getting Started

### Integrating the Plugin into your Ionic App

The Cordova plugin can be integrated within cordova or ionic app projects. 

**NOTE** that  Ionic now recommends [capacitor](https://capacitorjs.com) for native access within Ionic apps. However, a cordova plugin can also be used and the instructions here is to be used as a guide. You may also migrate the plugin to capacitor and submit as a contribution!

For enterprise apps, there is a [Ionic plugin](https://ionic.io/integrations/couchbase-lite) for couchbase Lite.


The step-by-step instructions below illustrates how you can integrate and use the plugin within a blank Angular Ionic Project for Android platform. You will do something similar when building your own app. 

*  Create a blank ionic project without capacitor integration per instructions in the [Starter's Guide](https://ionicframework.com/docs/developing/starting). Use Angular as language. 

*  Install android platform into your Ionic project 

```bash
ionic cordova platform add android
```
* Build your project for Android

```bash
ionic cordova build android
```
* Install the plugin by adding the appropriate Github repo. If you fork the repo and modify it, then be sure to point it to the right URL!

```bash
ionic cordova plugin add https://github.com/rajagp/couchbase-lite-cordova-plugin-android.git
```

### Adding couchbase-lite-android framework as a dependency

The plugin does not come bundled with the cordova plugin. You will have to include the appropriately licensed Couchbase Lite Android library as dependency within your app. The Cordova reference plugin requires minimal version of **Couchbase Lite v3.0.0**. 

Couchbase Lite can be downloaded from Couchbase [downloads](https://www.couchbase.com/downloads) page or can be pulled in via maven as described in [Couchbase Lite Android Getting Started Guides](https://docs.couchbase.com/couchbase-lite/current/android/gs-install.html).

We discuss the steps to add the Couchbase Lite framework dependency depending on how you downloaded the framework. 

* Open the Android project located inside your ionic project under directory: `/path/to/ionic/app/platforms/android` using Android Studio.

**To add couchbase-lite-android as an .aar file**

* Create a a new directory called 'libs' under your Android project
* Copy the .aar files from within your downloaded Couchbase Lite package to the 'libs' folder 

![](https://blog.couchbase.com/wp-content/uploads/2021/08/adding-couchbase-lite-aar-files.png)

* In your 'app' level `build.gradle` file and add your library file path under dependencies. 
**NOTE**: It is important that you add the dependency line OUTSIDE Of the "// SUB-PROJECT DEPENDENCIES" block

```bash
dependencies {
    implementation fileTree(dir: 'libs', include: '*.jar')
    implementation files('libs/couchbase-lite-android-ee-3.0.0.aar', 'libs/okhttp-3.14.7.jar','libs/okio-1.17.2.jar')
    // SUB-PROJECT DEPENDENCIES START
    implementation(project(path: ":CordovaLib"))
    implementation "com.android.support:support-annotations:27.+"
    // SUB-PROJECT DEPENDENCIES END

}
```


**Include couchbase-lite-android sdk from maven**

- In your 'app' level `build.gradle` file, add your library file path. Follow the instructions in [Couchbase Lite Android Getting Started Guides](https://docs.couchbase.com/couchbase-lite/current/android/gs-install.html) for URL or maven repository etc.
 ```
 dependencies {
    implementation 'com.couchbase.lite:couchbase-lite-android:${version}'
 }
```

### Run your Ionic project

* You can run the app directly from Android Studio or issue the following command from command line
 
 ```bash
 ionic cordova run android
 ```


## Usage

Here are a few examples of using the plugin in your app

* To use the plugin, open your Ionic app and declare the plugin at the on top of your component.js file. 

```
declare var CBL: any;

@Component({
  selector: 'app-folder',
  templateUrl: './folder.page.html',
  styleUrls: ['./folder.page.scss'],
})
...
```

**Create OR Open Database**
```
const config = {
    encryptionKey: "{{ENCRYPTION_KEY}}",
    directory: "{{DIRECTORY}}"
};
let dbName = '{{DATABASE_NAME}}'

CBL.createOrOpenDatabase(dbName, config, function(rs) { }, function(error) { });
```

**Close Database**

```
const config = {
    encryptionKey: "{{ENCRYPTION_KEY}}",
    directory: "{{DIRECTORY}}"
};
let dbName = "{{DATABASE_NAME}}";

CBL.closeDatabase(dbName, config, function(rs) { }, function(error) { });

```

**Copy Database**

```
let dbName = "{{DATABASE_NAME}}";
let newDbName = "{{NEW_DATABASE_NAME}}";

const config = {
    encryptionKey: "{{ENCRYPTION_KEY}}",
    directory: "{{DIRECTORY}}"
};

const newConfig = {
    encryptionKey: "{{ENCRYPTION_KEY}}",
    directory: "{{DIRECTORY}}"
};

CBL.copyDatabase(dbName, config, newDbName, newConfig, function(rs) { }, function(error) { });

```

**Delete Database**

```
const config = {
    encryptionKey: "{{ENCRYPTION_KEY}}",
    directory: "{{DIRECTORY}}"
};
let dbName = "{{DATABASE_NAME}}";

CBL.deleteDatabase(dbName, config, function(rs) { }, function(error) { });

```


**Save Document**
```
let id = "{{DOCUMENT_ID}}";
let document = "{{JSON_OBJECT}}"; e.g { foo : 'bar', adam : 'eve' }
let dbName = "{{DATABASE_NAME}}";

CBL.saveDocument(id, document, dbName, function(rs) { }, function(error) { });
```

**Adding/Updating key value to Document**
```
let id = "{{DOCUMENT_ID}}";

let dbName = "{{DATABASE_NAME}}";
let key = "{{KEY}}";
let value = "{{VALUE}}";

CBL.mutableDocumentSetString(id, key, value, dbName,  function(rs) { }, function(err) { });
```

**Get Document**
```
let id = "{{DOCUMENT_ID}}"
let dbName = "{{DATABASE_NAME}}";

CBL.getDocument(id, dbName, function(result) { }, function(error) { });
```

**Delete Document**
```
let id = "{{DOCUMENT_ID}}"
let dbName = "{{DATABASE_NAME}}";

CBL.deleteDocument(id, dbName, function(result) { }, function(error) { });
```


**Save Blob using Base64**
```
let dbName = "{{DATABASE_NAME}}";
let contentType = "{{CONTENT_TYPE}}";
let blobData = "{{BLOB_DATA}}"; <== Base64

CBL.saveBlob(dbName, contentType, blobData, function(rs) { }, function(err) {  });
```

**Save Blob using Embedded Resource**
```
let dbName = "{{DATABASE_NAME}}";
let contentType = "{{CONTENT_TYPE}}";
let drawableResource = "{{RESOURCE_NAME}}"; <== asset placed under drawable directory (native)

CBL.saveBlobFromEmbeddedResource(dbName, contentType, drawableResource, function(rs) { }, function(err) { });
```

**Save Blob using Native File URL**
```
let dbName = "{{DATABASE_NAME}}";
let contentType = "{{CONTENT_TYPE}}";
let imageURL = "{{NATIVE_FILE_URL}}";

CBL.saveBlobFromFileURL(dbName, contentType, imageURL, function(rs) { }, function(err) { });
```

**Get Blob**
```
let dbName = "{{DATABASE_NAME}}";
let blob = {{Blob from Document}};

CBL.getBlob(dbName, blob, function(blob) { }, function(error) { });

```

**Add Change Listener**

```
let dbName = "{{DATABASE_NAME}}";

CBL.dbAddListener(dbName, function(blob) { }, function(error) { });

```

**Remove Change Listener**

```
let dbName = "{{DATABASE_NAME}}";

CBL.dbRemoveListener(dbName, function(blob) { }, function(error) { });

```

**Enable Logging**

Logs will show up on native console output

```
let domain =  CBL.Domain.DATABASE;   //See available option from CBL.Domain object
let logLevel = CBL.LogLevel.DEBUG;  //See available option from CBL.LogLevel object

CBL.enableConsoleLogging(domain, logLevel, function(result) { }, function(error) { });

```

**Create Value Index**

```
let dbName = "{{DATABASE_NAME}}";
let indexName = "{{INDEX_NAME}}";
let indexes = [{{INDEX_ARRAY}}];

CBL.createValueIndex(dbName, indexName, indexes, function(rs) {}, function(err) { });

```

**Create FTS Index**

```
let dbName = "{{DATABASE_NAME}}";
let indexName = "{{INDEX_NAME}}";
let indexes = [{{INDEX_ARRAY}}];
let ignoreAccents = {{true OR false}};
let language = "{{LANGUAGE_VALUE}}"; 

CBL.createValueIndex(dbName, indexName, ignoreAccents, language, indexes, function(rs) { }, function(error) { });
```

**Delete Index**

```
let dbName = "{{DATABASE_NAME}}";
let indexName = "{{INDEX_NAME}}";
CBL.deleteIndex(dbName, indexName, function(rs) { }, function(error) { });
```

**Execute Query**

```
let dbName = "{{DATABASE_NAME}}";
let query = "{{QUERY_STRING}}";
CBL.query(dbName, query, function(rs) { }, function(error) { });
```

**queryAddListener**

```
let dbName = "{{DATABASE_NAME}}";
let onQueryChange = function (rs) { console.log(rs) };
let query = "{{QUERY_STRING}}";
CBL.queryAddListener(dbName, query, 'onQueryChange', function (rs) { console.log(rs); }, function (err) { console.log(err)});
```

**queryRemoveListener**

```
let dbName = "{{DATABASE_NAME}}";
let query = "{{QUERY_STRING}}";
CBL.queryRemoveListener(dbName, query, function (rs) { console.log(rs); }, function (err) { console.log(err)});
```

**Init Replicator**

```
let dbName = "{{DATABASE_NAME}}";
var replicatorConfig = CBL.ReplicatorConfiguration(dbName,'ws://{{SYNC_GATEWAY_IP}}/{{dbName}}');
replicatorConfig.continuous = {{true/false}};
replicatorConfig.authenticator = CBL.BasicAuthenticator('{{USERNAME}}', '{{PASSWORD}}');
replicatorConfig.channels = ['channel.{{USERNAME}}'];
replicatorConfig.replicatorType = CBL.ReplicatorType.PUSH_AND_PULL; //{{ PUSH / PULL / PUSH_AND_PULL }}

let replicator = CBL.Replicator(replicatorConfig, function(rs) { console.log (rs);} , function(err) {console.log(err); }); // returns Replicator Hash in success callback.
```

**Start Replicator**

```
let hash = "{{REPLICATOR_HASH}}"
replicator.start(hash, function(rs) {  console.log(rs); }, function(err) { console.log(err) });
```

**Stop Replicator**

```
let hash = "{{REPLICATOR_HASH}}";
CBL.stop(hash, function(rs) {  console.log(rs); }, function(err) { console.log(err) });
```

**Add change listener**

```
let hash = "{{REPLICATOR_HASH}}";
let replicatorCB = function (rs) { console.log(rs) };
CBL.addChangeListener(hash, 'replicatorCB', function(rs) { console.log(rs) }, function(err) { console.log(err) });
```

**Remove change listener**

```
let hash = "{{REPLICATOR_HASH}}";
CBL.removeChangeListener(hash, function(rs) { console.log(rs) }, function(err) { console.log(err) });
```

## Quick Debug

You can also use [chrome inspect](https://developer.chrome.com/docs/devtools/) to attach to app running on the Android device or emulator and to direct invoke the CBL APIs. This is very convenient for quick testing to check to see if your plugin API is working as expected.

The plugin exposes 'CBL' object globally and the methods exposed by the plugin can be called directly from chrome inspect tool.
 
To test whether its exposed methods work properly -
1. Launch your app with plugin integrated  in your emulator/device(in case of device, make sure USB debugging is enabled ).
2. Open Google Chrome.
3. Enter the following on Chrome's address bar:
```
chrome://inspect/#devices
```
4. Open 'Console' Tab.
5. Now you can use 'CBL' object to call the plugin's exposed methods. Here is an example

```
CBL.createOrOpenDatabase('Database', { 'encryptionKey': '', 'directory' : 'database'}, function(rs) { console.log(rs)}, function(err) { console.log(err) });
```

![](https://blog.couchbase.com/wp-content/uploads/2021/08/chrome-inspect.gif)

## Updates to Plugin

If you update the plugin such as adding a new API, don't forget to  remove the plugin and follow instructions outlined above to add it to the app
```
ionic cordova plugin rm cordova.plugin.couchbaselite
```
