var exec = require('cordova/exec');


exports.createDatabase = function (arg0, success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'createDatabase', [arg0]);
};

exports.saveDocument = function (arg0, success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'saveDocument', [arg0]);
};

exports.getDocument = function (arg0, success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'getDocument', [arg0]);
};

exports.closeDatabase = function (arg0, success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'closeDatabase', [arg0]);
};

exports.deleteDatabase = function (arg0, success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'deleteDatabase', [arg0]);
};

exports.copyDatabase = function (success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'copyDatabase', [arg0])
}
