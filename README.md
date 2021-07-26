# overview
A reference implementation of a [cordova native plugin](https://cordova.apache.org/docs/en/10.x/guide/hybrid/plugins/index.html) for couchbase lite on Android. 

In order to use Couchbase Lite as embedded database within your Cordova-based app, you will need a way to access Couchbase Lite’s native APIs from within your Cordova web application. Cordova plugins allow web-based apps running in a Cordova webview to access native platform functionality through a Javascript interface.

The Cordova plugin example exports a subset of native Couchbase Lite API functionality. This is intended to be used as a reference. You can extend this plugin to expose other relevant APIs per [plugin development guide](https://cordova.apache.org/docs/en/10.x/guide/platforms/android/plugin.html) 

![](https://i0.wp.com/blog.couchbase.com/wp-content/uploads/2018/10/JS-stuff.jpg?w=900)
# Exported APIs
The following list of Couchbase Lite (Android) APIs is exported by the plugin. 

This is WIP

| Create Database with specified Configuration | Database |
| :---: | :---: |
| Create DatabaseConfiguration | DatabaseConfiguration |
| Close Database | Database |
| copyDatabase | Database |
| AddChangeListener to listen for database changes | Database |
| RemoveChangeListener | Database |
| saveDocument (With JSONString) | MutableDocument |
| saveBlob  | Database |
| deleteDocument | MutableDocument |
| getDocument | MutableDocument |

# Build Instructions
- clone this repository

## To use couchbase-lite-android sdk using an .aar file follow these steps:
- cd into the cloned repository
- copy 'build.gradle' file from ./plugin-build-gradle/**aar/build.gradle** and paste it inside ./src/android/
- rename your .aar file to 'couchbase-lite-android.aar' and place it inside directory ./libs 

## To use couchbase-lite-android sdk from maven follow these steps:
- cd into the cloned repository
- copy 'build.gradle' file from ./plugin-build-gradle/**maven/build.gradle** and paste it inside ./src/android/
- version of the library can be changed by editing the build.gradle file
 ```
 dependencies {
    implementation 'com.couchbase.lite:couchbase-lite-android:${version}'
 }
```

## Next steps: Install the plugin in your project by using the following command:

```
- cd into your Ionic project folder
- ionic cordova plugin install {{PATH_TO_PLUGIN}}

example: ionic cordova plugin install ../couchbase-lite-cordova-plugin-android

```

# Sample Usage Instructions

## To use the plugin declare it on top of your component: 

```declare var CouchbaseLitePlugin: any;```

## Create Database 
```
const config = {
    dbName: "{{DATABASE_NAME}}",
    directory: "{{DIRECTORY_NAME}}"
};
CouchbaseLitePlugin.createDatabase(config, function(result) { }, function(error) { });
```

## Save Document
```
const params = {
    dbName: "{{DATABASE_NAME}}",
    docId: "{{NEW_DOCUMENT_ID}}",
    document: {
      "foo" : "bar",
      "hello" : "world"
    }
}

CouchbaseLitePlugin.saveDocument(params, function(result) { }, function(error) { });
```

## Get Document 
```
const params = {
    dbName: "{{DATABASE_NAME}}",
    docId: "{{NEW_DOCUMENT_ID}}",
};

CouchbaseLitePlugin.getDocument(params, function(result) { }, function(error) { });
```


## Set Blob

```
const config = {
    dbName: "{{DATABASE_NAME}}",
    imageData: {{Base64 String}},
    contentType: {{CONTENT_TYPE}}
};

CouchbaseLitePlugin.setBlob(config, function(blob) { }, function(error) { });

```


## Get Blob

```
const config = {
    dbName: "{{DATABASE_NAME}}",
    blob: {{Blob from Document}},
};

CouchbaseLitePlugin.getBlob(config, function(blob) { }, function(error) { });

```

## Add Change Listener

```
const config = {
    dbName: "{{DATABASE_NAME}}"
};

CouchbaseLitePlugin.addChangeListener(config, function(blob) { }, function(error) { });

```

## Remove Change Listener

```
const config = {
    dbName: "{{DATABASE_NAME}}"
};

CouchbaseLitePlugin.removeChangeListener(config, function(blob) { }, function(error) { });

```

## Close Database

```
const config = {
    dbName: "{{DATABASE_NAME}}"
};

CouchbaseLitePlugin.closeDatabase(config, function(blob) { }, function(error) { });

```
# Sample App

[Sample UserProfile Ionic app](https://github.com/rajagp/userprofile-couchbase-mobile-cordova-android/tree/main/standalone) 

# Couchbase Lite Version
This version of Cordova plugin requires Couchbase Lite 3.0.0(Beta).

