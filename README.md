## Overview

A reference implementation of a [cordova native plugin](https://cordova.apache.org/docs/en/10.x/guide/hybrid/plugins/index.html) for [couchbase lite on Android](https://docs.couchbase.com/couchbase-lite/3.0/android/quickstart.html). 

In order to use Couchbase Lite as an embedded database within your Cordova-based app, you will need a way to access Couchbase Lite’s native APIs from within your Cordova web application. Cordova plugins allow web-based apps running in a Cordova webview to access native platform functionality through a Javascript interface.

The Cordova plugin  exports a subset of native Couchbase Lite APIs and makes it available to Cordova apps. The plugin isn't a simple passthrough layer. It also acts as a DAO layer and implements additional logic (like a database manager) on top of the underlying native API layer to simplify JS app implementation. 

The plugin is not officially supported by Couchbase. It is intended to be used as a reference. You can extend this plugin to expose other relevant APIs per [plugin development guide](https://cordova.apache.org/docs/en/10.x/guide/platforms/android/plugin.html) 

*NOTE*: The plugin **does not** bundle Couchbase Lite native framework. You will include Couchbase Lite library when building your Cordova or Ionic app. The Getting Started instructions below describe the same.


**LICENSE**: The source code for the plugin is Apache-licensed, as specified in LICENSE. However, the usage of Couchbase Lite will be guided by the terms and conditions specified in Couchbase's Enterprise or Community License agreements.


![](https://i0.wp.com/blog.couchbase.com/wp-content/uploads/2018/10/JS-stuff.jpg?w=900)

## Exported APIs
The following is list of APIs (& features) exported by the plugin. Please refer to the Couchbase Lite native []API specification](https://docs.couchbase.com/mobile/3.0.0-beta02/couchbase-lite-android/com/couchbase/lite/package-summary.html) for an authoritative description of the API functionality. As indicated earlier, in some cases, the plugin isn't a simple passthrough. There is additional logic implemented by the plugin so there may not be an exact 1:1 correlation with the native API definitions. 


| API methods | Native Class | Description |
| :---: | :---: | :--- |
| createOrOpenDatabase (with specified Configuration) | Database | Initializes a Couchbase Lite database with a given name and database options. If the database does not yet exist, it will be created. |
| DatabaseConfiguration | Database | Helper method to create database configuration object. |
| checkDatabase | Database | Checks if database exists in the given directory path. |
| closeDatabase | Database | Close database synchronously. Before closing the database, the active replicators, listeners and live queries will be stopped. |
| copyDatabase | Database | Copies a canned databaes from the given path to a new database with the given name and the configuration. The new database will be created at the directory specified in the configuration. Without given the database configuration, the default configuration that is equivalent to setting all properties in the configuration to nil will be used.  |
| dbAddListener | Database | Adds a database change listener to listen for document changes to underlying database. There can only one one database listener associated with a database. Changes will be posted on the main thread(android)/queue(ios) |
| dbRemoveListener | Database | Removes existing database change listener. |
| saveDocument (With JSON OBJECT) | MutableDocument | Saves (or updates) a document to the database. If the document exists, the contents will be overwritten.  When write operations are executed concurrently, the last writer will overwrite all other written values. |
| mutableDocumentSetString | MutableDocument | Updates an existing document with a key/value pair property that is of string value. This is included as an example. The API can be extended to support other JSON types. |
| getDocument | MutableDocument | Gets a Document object with the given ID. |
| deleteDocument | MutableDocument | Delete a document in the database. When write operations are executed concurrently, the last writer will overwrite all other written values.|
| saveBlob | Database | Saves blob on the database. NOTE: successCallback returns metadata that must be used to retreive the blob using the getBlob function.  It's very important that you keep a reference to the metadata if you need to retreive the blob. |
| saveBlobFromEmbeddedResource | Database | Saves Blob object from a file that is embedded in the Native project (AssetCatalog for iOS and Resource folder for Android).  NOTE: successCallback returns metadata that must be used  to retreive the blob using the getBlob function.  It's very important that you keep a reference to the metadata if you need to retreive the blob.  This is a helper function for javascript developers with no Native equivilant call |
| saveBlobFromFileURL | Database | Saves Blob object from a file that is saved on the device in a folder accessible by the application. This might require application configuration changes to allow application to read files stored on the device.|
| getBlob  | Database | Get a Blob object for the given metadata. |
| enableConsoleLogging  | Database | Allows to configure console logging. Logs are printed on native IDE. Very useful for debugging. |
| createValueIndex  | Database | Creates a value index for regular queries. |
| createFTSIndex  | Database | Creates a full-text search index for full-text search query with the match operator. |
| deleteIndex  | Database | Deletes an index (FTS or value index) |
| query  | Query | N1QL query to execute against the database and return results. |
| queryAddListener | Query | Creates a query and adds a query change listener. Changes will be posted on the main thread(android)/queue(ios). |
| queryRemoveListener | Query | Removes a query change listener. |
| ReplicatorConfiguration | Replicator | Helper method to create replicator configuration object. |
| start | Replicator | Initializes a replicator with the given configuration. The replicator is used for  replicating document changes between a local database and a target database. The replicator can be bidirectional or either push or pull. The replicator can also be one-short or continuous. The replicator runs asynchronously, so observe the status property to be notified of progress. |
| stop | Replicator | Stops a running replicator. This method returns immediately; when the replicator actually stops, the replicator will change its status’s activity level to stopped and the replicator change notification will be notified accordingly. |
| BasicAuthenticator | BasicAuthentication  | The BasicAuthentiatior is an authenticator that will authenticate using HTTP Basic auth with the given username and password. This should only be used over an SSL/TLS connection, as otherwise it's very easy for anyone sniffing network traffic to read the password. |
| SessionAuthenticator | SessionAuthentication | The SessionAuthenticatior is an authenticator that will authenticate by using the session ID of the session created by a Sync Gateway |
| addChangeListener | Replicator | Adds a replication change listener. Changes will be posted on the main thread(android)/queue(ios). |
| removeChangeListener | Replicator | Removes the replication change listener. |


## Sample App
A full working sample app is available [here](https://github.com/couchbaselabs/userprofile-couchbase-mobile-cordova-android:)
## Getting Started

### Integrating the Plugin into your Ionic App

The Cordova plugin can be integrated within cordova or ionic app projects. 

**NOTE** that Ionic now recommends [capacitor](https://capacitorjs.com) for native access within Ionic apps. However, a cordova plugin can also be used and the instructions here is to be used as a guide. You may also migrate the plugin to capacitor and submit as a contribution!

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
ionic cordova plugin add https://github.com/couchbaselabs/couchbase-lite-cordova-plugin-android.git
```

### Adding couchbase-lite-android framework as a dependency

The plugin does not come bundled with the cordova plugin. You will have to include the appropriately licensed Couchbase Lite Android library as dependency within your app. The Cordova reference plugin requires minimal version of **Couchbase Lite v3.0.0**. 

Couchbase Lite can be downloaded from Couchbase [downloads](https://www.couchbase.com/downloads) page or can be pulled in via maven as described in [Couchbase Lite Android Getting Started Guides](https://docs.couchbase.com/couchbase-lite/current/android/gs-install.html).

We discuss the steps to add the Couchbase Lite framework dependency depending on how you downloaded the framework. 

* Open the Android project located inside your ionic project under directory: `/path/to/ionic/app/platforms/android` using Android Studio.

**Include couchbase-lite-android sdk from maven**

- In your 'app' level `build.gradle` file, add your library file path. Follow the instructions in [Couchbase Lite Android Getting Started Guides](https://docs.couchbase.com/couchbase-lite/current/android/gs-install.html) for URL or maven repository etc.
 ```
 dependencies {
    implementation 'com.couchbase.lite:couchbase-lite-android:${version}'
 }
```

**To add couchbase-lite-android as an .aar file**

* Create a a new directory called 'libs' under your Android project
* Copy the .aar files from within your downloaded Couchbase Lite package to the 'libs' folder 

![](https://blog.couchbase.com/wp-content/uploads/2021/08/adding-couchbase-lite-aar-files.png)

* In your 'app' level `build.gradle` file and add your library file path under dependencies. 
**NOTE**: It is important that you add the dependency line OUTSIDE Of the "// SUB-PROJECT DEPENDENCIES" block

Example: 
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




### Run your Ionic project

* You can run the app directly from Android Studio IDE or issue the following command from command line
 
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
    encryptionKey: "{{ENCRYPTION_KEY}}", // optional
    directory: "{{DIRECTORY}}" // optional
};
let dbName = '{{DATABASE_NAME}}'

// Config parameter is optional

CBL.createOrOpenDatabase(dbName, config, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});
```

**Close Database**

```

let dbName = "{{DATABASE_NAME}}";

CBL.closeDatabase(dbName,  function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});

```

**Copy Database**

```
let dbName = "{{DATABASE_NAME}}";
let newDbName = "{{NEW_DATABASE_NAME}}";


const newConfig = {
    encryptionKey: "{{ENCRYPTION_KEY}}",
    directory: "{{DIRECTORY}}"
};

CBL.copyDatabase(dbName, newDbName, newConfig, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});

```

**Delete Database**

```

let dbName = "{{DATABASE_NAME}}";

CBL.deleteDatabase(dbName, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});

```


**Save Document**
```
let docId = "{{DOCUMENT_ID}}"; // must be a unique Id. Otherwise, treated as update and will override existing document
let document = "{{JSON_OBJECT}}"; e.g { "type": "user", "name": "eve", "address" : "100 Main Street" }
let dbName = "{{DATABASE_NAME}}";

CBL.saveDocument(docId, document, dbName, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});
```

**Adding/Updating key value to Document**
```
let docId = "{{DOCUMENT_ID}}";

let dbName = "{{DATABASE_NAME}}";
let key = "{{KEY}}";
let value = "{{VALUE}}"; // Only supports string type. Extend for other types

CBL.mutableDocumentSetString(docId, key, value, dbName,  function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});
```

**Get Document**
```
let docId = "{{DOCUMENT_ID}}"
let dbName = "{{DATABASE_NAME}}";

CBL.getDocument(docId, dbName, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});
```

**Delete Document**
```
let docId = "{{DOCUMENT_ID}}"
let dbName = "{{DATABASE_NAME}}";

CBL.deleteDocument(docId, dbName, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});
```


**Save Blob using Base64**
```
let dbName = "{{DATABASE_NAME}}";
let contentType = "{{CONTENT_TYPE}}"; // example - 'image/jpeg'
let blobData = "{{BLOB_DATA}}"; <== Base64

// This saves blob to database and returns metadata associated with blob in result
CBL.saveBlob(dbName, contentType, blobData, function(blobMeta) {console.log("blobMeta:" + JSON.stringify(blobMeta)) }, function(error) {console.log(error)});

// The blob metadata returned from saveBlob() API must be stored in the document
document['profilePic'] = blobMeta;
CBL.saveDocument(docId, document, dbName, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});

```

**Save Blob using Embedded Resource**
```
let dbName = "{{DATABASE_NAME}}";
let contentType = "{{CONTENT_TYPE}}"; // example - 'image/jpeg'
let drawableResource = "{{RESOURCE_NAME}}"; <== asset placed under drawable directory (native)

// This saves blob to database and returns metadata associated with blob in result
CBL.saveBlobFromEmbeddedResource(dbName, contentType, drawableResource, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});

// The blob metadata returned from saveBlob() API must be stored in the document
document['profilePic'] = blobMeta;
CBL.saveDocument(docId, document, dbName, function(blobMeta) {console.log("blobMeta:" + JSON.stringify(blobMeta)) }, function(error) {console.log(error)});

```

**Save Blob using Native File URL**
```
let dbName = "{{DATABASE_NAME}}";
let contentType = "{{CONTENT_TYPE}}"; // example - 'image/jpeg'
let imageURL = "{{NATIVE_FILE_URL}}";

// This saves blob to database and returns metadata associated with blob in result
CBL.saveBlobFromFileURL(dbName, contentType, imageURL, function(blobMeta) {console.log("blobMeta:" + JSON.stringify(blobMeta)) }, function(error) {console.log(error)});

// The blob metadata returned from saveBlob() API must be stored in the document
document['profilePic'] = blobMeta;
CBL.saveDocument(docId, document, dbName, function(blobMeta) {console.log("blobMeta:" + JSON.stringify(blobMeta)) }, function(error) {console.log(error)});


```

**Get Blob**
```
let dbName = "{{DATABASE_NAME}}";
let blobMeta = {{Blob Metadata from Document}}; // This is the blob metadata that was returned from the saveBlob() call

CBL.getBlob(dbName, blobMeta, function(blob) {console.log("blob:" + blob.content) }, function(error) {console.log(error)});

```

**Add Database Change Listener**

```
let dbName = "{{DATABASE_NAME}}";

// Only one database listener will be associated with a database
CBL.dbAddListener(dbName, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});

```

**Remove Change Listener**

```
let dbName = "{{DATABASE_NAME}}";

CBL.dbRemoveListener(dbName, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});

```

**Enable Logging**

Logs will show up on native console output

```
let domain =  CBL.Domain.DATABASE;   //See available option from CBL.Domain object
let logLevel = CBL.LogLevel.DEBUG;  //See available option from CBL.LogLevel object

CBL.enableConsoleLogging(domain, logLevel, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});

```

**Create Value Index**

```
let dbName = "{{DATABASE_NAME}}";
let indexName = "{{INDEX_NAME}}";
let indexes = [{{INDEX_ARRAY}}];

CBL.createValueIndex(dbName, indexName, indexes, function(rs) {}, function(err) { });

```

**Create Full Text Search (FTS) Index**

```
let dbName = "{{DATABASE_NAME}}";
let ftsIndex = "{{INDEX_NAME}}";
let indexes = [{{INDEX_ARRAY}}]; // example ["type","name"] will create indexes on type and name properties of document
let ignoreAccents = {{true OR false}}; // optional
let language = "{{LANGUAGE_VALUE}}"; // optional. 

CBL.createFTSIndex(dbName, ftsIndex, ignoreAccents, language, indexes, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});
```

**Delete Index**

```
let dbName = "{{DATABASE_NAME}}";
let indexName = "{{INDEX_NAME}}";
CBL.deleteIndex(dbName, indexName, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});
```

**Create and Execute Query Once**

```
let dbName = "{{DATABASE_NAME}}";

// Refer to native APIs for value and FTS query examples 
let query = "{{QUERY_STRING}}"; // Example "SELECT * FROM _"  or "SELECT * from _ WHERE MATCH(ftsIndex,"searchString""
CBL.query(dbName, query, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});
```

**queryAddListener**

```
let dbName = "{{DATABASE_NAME}}";
let onQueryChangeCallback = function (rs) { console.log(rs) };
let query = "{{QUERY_STRING}}";
CBL.queryAddListener(dbName, query, 'onQueryChangeCallback',function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});
```

**queryRemoveListener**

```
let dbName = "{{DATABASE_NAME}}";
let query = "{{QUERY_STRING}}";
CBL.queryRemoveListener(dbName, query, function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});
```

**Init Replicator**

```
let dbName = "{{DATABASE_NAME}}";
var replicatorConfig = CBL.ReplicatorConfiguration(dbName,'ws://{{SYNC_GATEWAY_ENDPOINT}}/{{dbName}}'); // example: ws://sync-gateway:4984/db

replicatorConfig.authenticator = CBL.BasicAuthenticator('{{USERNAME}}', '{{PASSWORD}}'); //optional
replicatorConfig.continuous = {{true/false}}; // optional
replicatorConfig.channels = ['channel.{{USERNAME}}']; // optional
replicatorConfig.replicatorType = CBL.ReplicatorType.PUSH_AND_PULL; // optional. {{ PUSH / PULL / PUSH_AND_PULL }}


let replicator = CBL.Replicator(replicatorConfig, function(result) { 
                    console.log("result:" + result);                 
              }, 
          function(error) {console.log(error)});
  

```

**Start Replicator**

```
replicator.start(function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});
```

**Stop Replicator**

```
replicator.stop(function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});
```

**Add Replicator change listener**

```
let replicatorCallback = function (rs) { console.log(rs) };

replicator.addChangeListener('replicatorCallback', function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});
```

**Remove Replicator change listener**

```
replicator.removeChangeListener(function(result) {console.log("result:" + JSON.stringify(result)) }, function(error) {console.log(error)});
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

