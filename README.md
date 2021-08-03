# overview
A reference implementation of a [cordova native plugin](https://cordova.apache.org/docs/en/10.x/guide/hybrid/plugins/index.html) for couchbase lite on Android. 

In order to use Couchbase Lite as embedded database within your Cordova-based app, you will need a way to access Couchbase Lite’s native APIs from within your Cordova web application. Cordova plugins allow web-based apps running in a Cordova webview to access native platform functionality through a Javascript interface.

The Cordova plugin example exports a subset of native Couchbase Lite API functionality. This is intended to be used as a reference. You can extend this plugin to expose other relevant APIs per [plugin development guide](https://cordova.apache.org/docs/en/10.x/guide/platforms/android/plugin.html) 

![](https://i0.wp.com/blog.couchbase.com/wp-content/uploads/2018/10/JS-stuff.jpg?w=900)
# Exported APIs
The following list of Couchbase Lite (Android) APIs is exported by the plugin. 

This is WIP

| API methods | Native Class |
| :---: | :---: |
| createDatabase (with specified Configuration) | Database |
| closeDatabase | Database |
| copyDatabase | Database |
| dbAddListener | Database |
| dbRemoveListener | Database |
| saveDocument (With JSON OBJECT) | MutableDocument |
| mutableDocumentSetString | MutableDocument |
| getDocument | MutableDocument |
| deleteDocument | MutableDocument |
| mutableDocumentSetBlob  | MutableDocument |
| documentSetBlobFromEmbeddedResource | MutableDocument |
| documentSetBlobFromFileUrl | MutableDocument |
| getBlob  | Database |
| enableLogging  | Database |



## Plugin integration 

This plugin can be integrated in cordova or ionic projects. These steps are for integrating with a blank Angular Ionic Project.

- Create a blank ionic project without capacitor integration. Use Angular as language. 
(Starter's Guide: https://ionicframework.com/docs/developing/starting )
- Install android platform into your ionic project using:
``` 
ionic cordova platform add android
```
- Build your project using command: 
```
ionic cordova build android
```
- Install this plugin using command: 
```
ionic cordova plugin add https://github.com/rajagp/couchbase-lite-cordova-plugin-android.git
```

## Adding couchbase-lite-android sdk as dependency in your ionic project:

## To use couchbase-lite-android sdk using an .aar file follow these steps:
- open android studio and open android project located inside your ionic project under directory: ./platforms/android
- place your library .aar file inside your android project creating a new directory called 'libs'. 
```
e.g.:  ./app/libs/couchbase-lite-android-ee-3.0.0.aar
```
- open your 'app' level build.gradle file and add your library file path under dependencies.
``` 
e.g.: implementation files('libs/couchbase-lite-android-ee-3.0.0.aar')
```


## To use couchbase-lite-android sdk from maven follow these steps:
- open android studio and open android project located inside your ionic project under directory: ./platforms/android
- open your 'app' level build.gradle file and add your library file path.
 ```
 dependencies {
    implementation 'com.couchbase.lite:couchbase-lite-android:${version}'
 }
```
## Running Ionic project

```
ionic cordova run android
```


# Sample Usage Instructions

## To use the plugin declare it on top of your component: 

```declare var cbLite: any;```

## Create Database 
```
const config = {
    encryptionKey: "{{ENCRYPTION_KEY}}",
    directory: "{{DIRECTORY}}"
};
let dbName = '{{DATABASE_NAME}}'

cbLite.createDatabase(dbName, config, function(rs) { }, function(error) { });
```

## Close Database

```
const config = {
    encryptionKey: "{{ENCRYPTION_KEY}}",
    directory: "{{DIRECTORY}}"
};
let dbName = "{{DATABASE_NAME}}";

cbLite.closeDatabase(dbName, config, function(rs) { }, function(error) { });

```

## Copy Database

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

cbLite.closeDatabase(dbName, config, newDbName, newConfig, function(rs) { }, function(error) { });

```

## Delete Database

```
const config = {
    encryptionKey: "{{ENCRYPTION_KEY}}",
    directory: "{{DIRECTORY}}"
};
let dbName = "{{DATABASE_NAME}}";

cbLite.deleteDatabase(dbName, config, function(rs) { }, function(error) { });

```


## Save Document
```
let id = "{{DOCUMENT_ID}}";
let document = "{{JSON_OBJECT}}"; e.g { foo : 'bar', adam : 'eve' }
let dbName = "{{DATABASE_NAME}}";

cbLite.saveDocument(id, document, dbName, function(rs) { }, function(error) { });
```

## Adding/Updating key value to Document
```
let id = "{{DOCUMENT_ID}}";

let dbName = "{{DATABASE_NAME}}";
let key = "{{KEY}}";
let value = "{{VALUE}}";

cbLite.mutableDocumentSetString(id, key, value, dbName,  function(rs) { }, function(err) { });
```

## Get Document 
```
let id = "{{DOCUMENT_ID}}"
let dbName = "{{DATABASE_NAME}}";

cbLite.getDocument(id, dbName, function(result) { }, function(error) { });
```

## Delete Document 
```
let id = "{{DOCUMENT_ID}}"
let dbName = "{{DATABASE_NAME}}";

cbLite.deleteDocument(id, dbName, function(result) { }, function(error) { });
```


## Set Blob using Base64
```
let id = "{{DOCUMENT_ID}}"
let dbName = "{{DATABASE_NAME}}";
let key = "{{BLOB_KEY}}";
let contentType = "{{CONTENT_TYPE}}";
let blobData = "{{BLOB_DATA}}"; <== Base64

cbLite.mutableDocumentSetBlob(id, dbName, key, contentType, blobData, function(rs) { }, function(err) {  });
```

## Set Blob using Embedded Resource
```
let id = "{{DOCUMENT_ID}}"
let dbName = "{{DATABASE_NAME}}";
let key = "{{BLOB_KEY}}";
let contentType = "{{CONTENT_TYPE}}";
let drawableResource = "{{RESOURCE_NAME}}"; <== asset placed under drawable directory (native)

cbLite.mutableDocumentSetBlob(id, dbName, contentType, key, drawableResource, function(rs) { }, function(err) { });
```

## Set Blob using Native File URL
```
let id = "{{DOCUMENT_ID}}";
let dbName = "{{DATABASE_NAME}}";
let key = "{{BLOB_KEY}}";
let contentType = "{{CONTENT_TYPE}}";
let imageURL = "{{NATIVE_FILE_URL}}";

cbLite.mutableDocumentSetBlob(id, dbName, contentType, key, imageURL, function(rs) { }, function(err) { });
```

## Get Blob
```
let dbName = "{{DATABASE_NAME}}";
let blob = {{Blob from Document}};

cbLite.getBlob(dbName, blob, function(blob) { }, function(error) { });

```

## Add Change Listener

```
let dbName = "{{DATABASE_NAME}}";

cbLite.dbAddListener(dbName, function(blob) { }, function(error) { });

```

## Remove Change Listener

```
let dbName = "{{DATABASE_NAME}}";

cbLite.dbRemoveListener(dbName, function(blob) { }, function(error) { });

```

## Enable Logging (logs shows on native)

```
cbLite.enableLogging(function(blob) { }, function(error) { });

```

## Steps to Test the plugin if you are adding/modifying

The plugin exposes 'CBL' object globaly and the methods exposed by the plugin can be called. To test whether its exposed methods work properly we can perform the following:
1. Create a test ionic project and install the plugin
2. Run your project in your emulator / device. ( make sure USB debugging is enabled )
3. Open Google Chrome
4. Enter chrome://inspect/#devices on Chrome's address bar
5. Open 'Console' Tab
6. Now you can use 'CBL' object to call the plugin's exposed methods.
```
example: CBL.createDatabase('Database', { 'encryptionKey': '', 'directory' : 'database'}, function(rs) { console.log(rs)}, function(err) { console.log(err) });
```
