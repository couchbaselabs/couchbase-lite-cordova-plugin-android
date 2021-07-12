var exec = require('cordova/exec');


exports.initializeDB = function (arg0, arg1, success, error) {

    /* 
        arg0 -  database name
        arg1 - database dirName
    */

    exec(success, error, 'CouchbaseLitePlugin', 'initializeDB', [arg0,arg1]);
};

exports.saveData = function (arg0, arg1, success, error) {

    /* 
        arg0 - documentID
        arg1 - Payload
    */

    exec(success, error, 'CouchbaseLitePlugin', 'saveData', [arg0,arg1]);
};

exports.loadData = function (arg0, success, error) {

    //arg0 - documentID

    exec(success, error, 'CouchbaseLitePlugin', 'loadData', [arg0]);
};

exports.closeDB = function (success, error) {

    /* 
        arg0 -  database name
        arg1 - database dirName
    */

    exec(success, error, 'CouchbaseLitePlugin', 'closeDB', []);
};
