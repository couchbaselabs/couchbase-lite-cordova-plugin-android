# overview
A reference implementation of a [cordova native plugin](https://cordova.apache.org/docs/en/10.x/guide/hybrid/plugins/index.html) for couchbase lite on Android. 

In order to use Couchbase Lite as embedded database within your Cordova-based app, you will need a way to access Couchbase Lite’s native APIs from within your Cordova web application. Cordova plugins allow web-based apps running in a Cordova webview to access native platform functionality through a Javascript interface.

The Cordova plugin example exports a subset of native Couchbase Lite API functionality. This is intended to be used as a reference. You can extend this plugin to expose other relevant APIs per [plugin development guide](https://cordova.apache.org/docs/en/10.x/guide/platforms/android/plugin.html) 

![](https://i0.wp.com/blog.couchbase.com/wp-content/uploads/2018/10/JS-stuff.jpg?w=900)
# Exported APIs
The following list of Couchbase Lite (Android) APIs is exported by the plugin. 

This is WIP

| Create Database with specified Configuration | Database |
| Create DatabaseConfiguration | DatabaseConfiguration |
| Close Database | Database |
| copyDatabase | Database |
| AddChangeListener to listen for database changes | Database |
| RemoveChangeListener | Database |
| saveDocument (With JSONString) | MutableDocument |
| saveBlob  | Database) |
| deleteDocument | MutableDocument |
| getDocument | MutableDocument |

# Build Instructions
TBD

# Sample Usage Instructions
TBD

# Sample App
TBD (Add reference to user profile sample app)

# Couchbase Lite Version
This version of Cordova plugin requires Couchbase Lite 3.0.0(Beta).

