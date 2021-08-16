# overview
A reference implementation of a [cordova native plugin](https://cordova.apache.org/docs/en/10.x/guide/hybrid/plugins/index.html) for couchbase lite on Android. 

In order to use Couchbase Lite as embedded database within your Cordova-based app, you will need a way to access Couchbase Lite’s native APIs from within your Cordova web application. Cordova plugins allow web-based apps running in a Cordova webview to access native platform functionality through a Javascript interface.

The Cordova plugin example exports a subset of native Couchbase Lite APIs and makes it available to Cordova apps. This is intended to be used as a reference. You can extend this plugin to expose other relevant APIs per [plugin development guide](https://cordova.apache.org/docs/en/10.x/guide/platforms/android/plugin.html) 

*NOTE*: The plugin **does not** bundle Couchbase Lite native framework. You will include Couchbase Lite library when building your Cordova or Ionic app. The Getting Started instructions below describe the same.


![](https://i0.wp.com/blog.couchbase.com/wp-content/uploads/2018/10/JS-stuff.jpg?w=900)

## Exported APIs
The following is list of Couchbase Lite(Android) APIs exported by the plugin. 

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
| createValueIndex  | Database  |
| createFTSIndex  | Database |
| deleteIndex  | Database |
| query  | Query |



## Getting Started

### Integrating the Plugin into your Ionic App

The plugin can be integrated within cordova or ionic app projects. 

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

### Build and Run your Ionic project

You can run the app directly from Android Studio or issue the following command from command line

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

**Create Database**
```
const config = {
    encryptionKey: "{{ENCRYPTION_KEY}}",
    directory: "{{DIRECTORY}}"
};
let dbName = '{{DATABASE_NAME}}'

CBL.createDatabase(dbName, config, function(rs) { }, function(error) { });
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

CBL.closeDatabase(dbName, config, newDbName, newConfig, function(rs) { }, function(error) { });

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


**Set Blob using Base64**
```
let id = "{{DOCUMENT_ID}}"
let dbName = "{{DATABASE_NAME}}";
let key = "{{BLOB_KEY}}";
let contentType = "{{CONTENT_TYPE}}";
let blobData = "{{BLOB_DATA}}"; <== Base64

CBL.mutableDocumentSetBlob(id, dbName, key, contentType, blobData, function(rs) { }, function(err) {  });
```

**Set Blob using Embedded Resource**
```
let id = "{{DOCUMENT_ID}}"
let dbName = "{{DATABASE_NAME}}";
let key = "{{BLOB_KEY}}";
let contentType = "{{CONTENT_TYPE}}";
let drawableResource = "{{RESOURCE_NAME}}"; <== asset placed under drawable directory (native)

CBL.mutableDocumentSetBlob(id, dbName, contentType, key, drawableResource, function(rs) { }, function(err) { });
```

**Set Blob using Native File URL**
```
let id = "{{DOCUMENT_ID}}";
let dbName = "{{DATABASE_NAME}}";
let key = "{{BLOB_KEY}}";
let contentType = "{{CONTENT_TYPE}}";
let imageURL = "{{NATIVE_FILE_URL}}";

CBL.mutableDocumentSetBlob(id, dbName, contentType, key, imageURL, function(rs) { }, function(err) { });
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
CBL.enableLogging(function(blob) { }, function(error) { });

```

**Create Value Index**

```
let dbName = "{{DATABASE_NAME}}";
let indexName = "{{INDEX_NAME}}";
let indexes = [{{INDEX_ARRAY}}];

CBL.createValueIndex(dbName, indexName, indexes, function(rs) {}, function(err) { });

```
**Create FTS Index**

Logs will show up on native console output

```
let dbName = "{{DATABASE_NAME}}";
let indexName = "{{INDEX_NAME}}";
let indexes = [{{INDEX_ARRAY}}];
let ignoreAccents = false; // unavailable for android
let language = ""; // unavailable for android

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
CBL.createDatabase('Database', { 'encryptionKey': '', 'directory' : 'database'}, function(rs) { console.log(rs)}, function(err) { console.log(err) });
```

![](https://blog.couchbase.com/wp-content/uploads/2021/08/chrome-inspect.gif)

## Updates to Plugin

If you update the plugin such as adding a new API, don't forget to  remove the plugin and re-add it to the app
```
ionic cordova plugin rm cordova.plugin.couchbaselite
```
