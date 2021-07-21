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

exports.deleteDocument = function (arg0, success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'deleteDocument', [arg0]);
};

exports.closeDatabase = function (arg0, success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'closeDatabase', [arg0]);
};

exports.deleteDatabase = function (arg0, success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'deleteDatabase', [arg0]);
};

exports.copyDatabase = function (arg0, success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'copyDatabase', [arg0])
}

exports.setBlob = function (arg0, success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'setBlob', [arg0])
}

exports.getBlob = function (arg0, success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'getBlob', [arg0])
}

exports.addChangeListener = function (arg0, success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'addChangeListener', [arg0])
}

exports.removeChangeListener = function (arg0, success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'removeChangeListener', [arg0])
}

exports.enableLogging = function (success, error) {
    exec(success, error, 'CouchbaseLitePlugin', 'enableLogging')
}
